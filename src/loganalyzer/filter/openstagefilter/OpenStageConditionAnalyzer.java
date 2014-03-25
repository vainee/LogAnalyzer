/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.openstagefilter;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.LexicalException;
import loganalyzer.filter.interfaces.ICompiledCondition;
import loganalyzer.filter.interfaces.IConditionAnalyzer;
import loganalyzer.filter.interfaces.IParseCallback;
import loganalyzer.filter.openstagefilter.OpenStageConditionParser.States;
import loganalyzer.utils.Pair;


public class OpenStageConditionAnalyzer implements IConditionAnalyzer, IParseCallback<Pair<States, String>> {
    private CompiledCondition cc;
    
    private List<StackItem> stack = new LinkedList<>();
    
    public OpenStageConditionAnalyzer() {
        StackItem stackItem = new StackItem();        
        stackItem.setItemType(StackSymbols.DOLAR);
        stack.add(stackItem);
    }
    
    
    private final StackSymbols [][]precedenceTable = {
        //                             &&,                          ||,                          >,                          >=,                          <,                          <=,                          ==,                         (,                         i,                          ),                          ~,                          !=,                         !,                $
        /*&&*/{StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*||*/{StackSymbols.STACK_LESSER,   StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*>*/ {StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*=>*/{StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*<*/ {StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*<=*/{StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*==*/{StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},     
        /*(*/ {StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_EQUAL,   StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER, StackSymbols.STACK_FAULT},
        /*i*/ {StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_FAULT,  StackSymbols.STACK_FAULT,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_FAULT,  StackSymbols.STACK_GREATER},
        /*)*/ {StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_FAULT,  StackSymbols.STACK_FAULT,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,StackSymbols.STACK_GREATER},
        /*~*/ {StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*!=*/{StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*!*/ {StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER, StackSymbols.STACK_GREATER,  StackSymbols.STACK_GREATER, StackSymbols.STACK_LESSER, StackSymbols.STACK_GREATER},
        /*$*/ {StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER, StackSymbols.STACK_LESSER, StackSymbols.STACK_FAULT,   StackSymbols.STACK_LESSER,   StackSymbols.STACK_LESSER,  StackSymbols.STACK_LESSER, StackSymbols.DOLAR}
    };    
    @Override
    public ICompiledCondition getCompiledCondition(String expression) throws AnalyzerException, LexicalException {
        cc = new CompiledCondition();
        OpenStageConditionParser oscp = new OpenStageConditionParser();
        oscp.registerCallback(this);
        oscp.parse(expression);
        
        return cc;
    }

    @Override
    public void runCallback(Pair<States, String> event) {
        try {
            ConditionItem si = getConvertedToStackItem(event);
            if (si.getType() == StackSymbols.VARIABLE || si.getType() == StackSymbols.NUMBER || si.getType() == StackSymbols.STRING) {
                cc.addSymbol(si);
            }
            perform(si);
        } catch (AnalyzerException ex) {
            Logger.getLogger(OpenStageConditionAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void perform(ConditionItem event) throws AnalyzerException {
        StackItem si = getStackItem(event);
        
        StackSymbols oper = precedenceTable[stack.get(getTopTerminalIndex()).getItemType().getValue()][si.getItemType().getValue()];
        //printStack();
        switch(oper) {
            case STACK_EQUAL:
                stack.add(si);
                break;
            case STACK_GREATER:
                doReduction();     
                perform(event);
                break;
            case STACK_LESSER:
                int topTerminalIndex = getTopTerminalIndex();
                StackItem topTerminal = new StackItem();
                topTerminal.setItemType(StackSymbols.STACK_LESSER);
                stack.add(topTerminalIndex + 1, topTerminal);
                stack.add(si);
                break;
            case DOLAR:
                cc.addLastStep(stack.get(stack.size() - 1).getOriginalMessage());
                System.out.println("OK");
                break;
            default:
                throw new AnalyzerException("Bad order of operators/operands " + oper);
        }
        //printStack();
        
        
    }
    
    private void doReduction() throws AnalyzerException {
        int lastControl = getTopControlSymbolIndex();
        List<StackItem> toReduction = stack.subList(lastControl + 1, stack.size());    
     //   printStack();
        if (toReduction.size() == 1) { // E -> i
            stack.remove(lastControl);//<
            stack.get(lastControl).setItemType(StackSymbols.STACK_NONTERMINAL); 
            
            ConditionItem itm = cc.addStep(Operations.ASSIGN, stack.get(lastControl).getOriginalMessage());
            stack.get(lastControl).setOriginalMessage(itm);
            //printStack();
            System.out.println(lastControl);
        } else if (toReduction.size() == 3) {
            if (toReduction.get(1).isNonterminal()) { // E -> (E)
               stack.remove(lastControl); //<               
               stack.remove(lastControl);//(
               ConditionItem itm = cc.addStep(Operations.ASSIGN, stack.get(lastControl).getOriginalMessage());
               assert itm != null;
               
               stack.get(lastControl).setOriginalMessage(itm);
               stack.get(lastControl).setItemType(StackSymbols.STACK_NONTERMINAL);
               stack.remove(lastControl + 1); //)
            } else {// E -> E op E    
                System.out.println("E -> E op E");
                Operations oper;
                stack.remove(lastControl);//<
                ConditionItem op1 = stack.get(lastControl).getOriginalMessage();
                assert op1 != null;
                
                stack.remove(lastControl);//E
                oper = Operations.valueOf(stack.get(lastControl).getOriginalMessage().getType().toString());                
                stack.remove(lastControl);//op
                ConditionItem op2 = stack.get(lastControl).getOriginalMessage();
                assert op2 != null;
                
                ConditionItem itm = cc.addStep(oper, op1, op2);
                stack.get(lastControl).setOriginalMessage(itm);  
                assert itm != null;
                stack.get(lastControl).setItemType(StackSymbols.STACK_NONTERMINAL);
            }
        } else if (toReduction.size() == 2) { //E -> !E
            stack.remove(lastControl); //<   
            if (stack.get(lastControl).getType() == StackSymbols.NOT) {  
                stack.remove(lastControl); //!
                ConditionItem itm = cc.addStep(Operations.NOT, stack.get(lastControl).getOriginalMessage());
                assert itm != null;

                stack.get(lastControl).setOriginalMessage(itm);
                stack.get(lastControl).setItemType(StackSymbols.STACK_NONTERMINAL);
            }

        } else {
            throw new AnalyzerException("Bad order of operators/operands");
        }
   //     printStack();
    }
    
    private StackItem getStackItem(ConditionItem event) {
        StackSymbols index = event.getType();
        if (index.getValue() > StackSymbols.DOLAR.getValue()) {
            index = StackSymbols.VARIABLE;
        }
        
        StackItem si = new StackItem(event);
        si.setItemType(index);
        
        
        return si;
    }
    
    private int getTopTerminalIndex() throws AnalyzerException {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i).isTerminal()){
                //System.out.println("topTerminal: "+stack.get(i).getItemType());
                return i;
            }
        }
        
        throw new AnalyzerException("Stack does not contain a terminal symbol!");
    }
    
    private int getTopControlSymbolIndex() throws AnalyzerException {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i).isStackControlItem()){
                return i;
            }
        }
        
        throw new AnalyzerException("Stack does not a control item!");
    }    

    private ConditionItem getConvertedToStackItem(Pair<States, String> itm) {     
        ConditionItem ci = new ConditionItem();
        ci.setType(StackSymbols.valueOf(itm.first.toString()));
        if (ci.getType() == StackSymbols.VARIABLE) {
            ci.setName(itm.second);
        } else {
            ci.setValue(itm.second);
        }        
        
        return ci;
    }
    
    private class StackItem {
        private ConditionItem originalMessage = null;
        private StackSymbols itemType = StackSymbols.ERROR;
        
        public boolean isStackControlItem() {
            return itemType.getValue() >= StackSymbols.STACK_GREATER.getValue() && itemType.getValue() <= StackSymbols.STACK_EQUAL.getValue();
        }
        
        public boolean isTerminal() {
            return itemType.getValue() >= StackSymbols.AND.getValue() && itemType.getValue() <= StackSymbols.DOLAR.getValue();
        }
       
        public StackSymbols getType() {
            return itemType;
        }    
        
        public void setType(StackSymbols type) {
            this.itemType = type;
        }     
        
        public boolean isNonterminal() {
            return itemType == StackSymbols.STACK_NONTERMINAL;
        }

        public StackSymbols getItemType() {
            return itemType;
        }

        public void setItemType(StackSymbols itemType) {
            this.itemType = itemType;
        }

        public ConditionItem getOriginalMessage() {
            return originalMessage;
        }

        public void setOriginalMessage(ConditionItem originalMessage) {
            this.originalMessage = originalMessage;
        }
        
        public StackItem() {
            this.originalMessage = null;
        }
        
        public StackItem(ConditionItem originalMessage) {
            this.originalMessage = originalMessage;
        }          
    }
    
    private void printStack() {
        System.out.print(" | ");
        for (int i = 0; i < stack.size(); i++) {
            System.out.print(stack.get(i).getItemType().toString() + " | ");
        }
        
        System.out.println();
    }
    
    public enum Operations {
        AND,
        OR,
        GREATER,
        GREATER_EQUAL,
        LESSER,
        LESSER_EQUAL,        
        ASSIGN,
        CONTAINS,
        EQUAL,
        EVAL,
        NOT_EQUAL,
        NOT,
        ERROR
    }
    
    public enum StackSymbols {
        AND(0),
        OR(1),
        GREATER(2),
        GREATER_EQUAL(3),
        LESSER(4),
        LESSER_EQUAL(5),
        EQUAL(6),
        L_BRACKET(7),
        VARIABLE(8),
        R_BRACKET(9),                 
        CONTAINS(10),  
        NOT_EQUAL(11),
        NOT(12),
        DOLAR(13),
        STRING(14),
        NUMBER(15),        
        ERROR(16), 
        REGEX(17),
        BOOL(18),
        CONST(19), // E -> i
        STACK_GREATER(20),
        STACK_LESSER(21),
        STACK_EQUAL(22),
        STACK_FAULT(23),
        STACK_NONTERMINAL(24);
        
        private int value;
        
        boolean isNumber() {
            return value == NUMBER.getValue();
        }
        
        boolean isString() {
            return value == STRING.getValue();
        }        
        
        boolean isBool() {
            return value == BOOL.getValue();
        }   
                  
        
        StackSymbols(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }        
    }
}
