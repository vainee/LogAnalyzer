/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.openstagefilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import loganalyzer.parsers.IParsedMessage;
import loganalyzer.datatypes.Comparator;
import static loganalyzer.datatypes.Comparator.compare;
import loganalyzer.datatypes.Comparator.Operators;
import loganalyzer.datatypes.DataTypeHelper;
import loganalyzer.datatypes.DateTimeFactory;
import loganalyzer.datatypes.IData;
import loganalyzer.datatypes.IDataTypeFactory;
import loganalyzer.datatypes.IntegerFactory;
import loganalyzer.datatypes.StringFactory;
import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.InterpretException;
import loganalyzer.filter.interfaces.ICompiledCondition;
import loganalyzer.filter.openstagefilter.OpenStageConditionAnalyzer.StackSymbols;

/**
 *
 * @author kj000027
 */
public class CompiledCondition implements ICompiledCondition {
    private Map<String, ConditionItem> symbols = new HashMap<>();
    private Map<String, IData> values;
    private List<Instruction> instructions = new LinkedList<>();
    private int tmpConstCounter = 0;
    private final IData exprTrue;
    private final IData exprFalse;
    
    public CompiledCondition() {
        exprTrue = DataTypeHelper.getInstance().getFactoryByDatatype("Number").getNewInstance("1");
        exprFalse = DataTypeHelper.getInstance().getFactoryByDatatype("Number").getNewInstance("0");
    }
    
    /**
     * 
     * @return - Returns new unique identifier for constant.
     */
    private String getNextTmpConstName() {
        return "$"+String.valueOf(tmpConstCounter++);
    }
    
    @Override
    public boolean eval(IParsedMessage parsedMessage) throws InterpretException {
        for (Instruction ins : instructions) {
            //ins.printInstruction();
            switch (ins.getOperation()) {
                case ASSIGN:
                  //  System.out.println("EVAL:ASSING");
                 //   ins.printInstruction();
                    if (ins.getOp1().getType() == StackSymbols.VARIABLE) {
                        loadFromMap(ins.getOp1(), parsedMessage);                        
                    } else {
                        ins.getDst().setType(ins.getOp1().getType());
                    }
                    ins.getDst().setValue(ins.getOp1().getValue());

                break;
                    
                case GREATER:
                 //   System.out.println("EVAL:GREATER");
                    if (Comparator.isGreater(ins.getOp1().getValue(), ins.getOp2().getValue())) {
                        ins.getDst().setValue(exprTrue);
                    } else {
                        ins.getDst().setValue(exprFalse);                    
                    }
                  //  ins.printInstruction();                     
                    break;
                    
                case GREATER_EQUAL:
                   // System.out.println("EVAL:GREATER_EQUAL");
                    if (Comparator.isGreaterEqual(ins.getOp1().getValue(), ins.getOp2().getValue())) {
                        ins.getDst().setValue(exprTrue);
                    } else {
                        ins.getDst().setValue(exprFalse);                    
                    }
                   // ins.printInstruction();                      
                    break;
                    
                case LESSER:
                  //  System.out.println("EVAL:LESSER");
                    if (Comparator.isLesser(ins.getOp1().getValue(), ins.getOp2().getValue())) {
                        ins.getDst().setValue(exprTrue);
                    } else {
                        ins.getDst().setValue(exprFalse);                    
                    }
                   // ins.printInstruction();                       
                    break;
                    
                case LESSER_EQUAL:
                  //  System.out.println("EVAL:LESSER_EQUAL");              
                    if (Comparator.isLesserEqual(ins.getOp1().getValue(), ins.getOp2().getValue())) {
                        ins.getDst().setValue(exprTrue);
                    } else {
                        ins.getDst().setValue(exprFalse);                    
                    }
                 //   ins.printInstruction();                                                             
                    break;
                
                case OR:
                  //  System.out.println("EVAL:OR");
                    if (isTrue(ins.getOp1()) || isTrue(ins.getOp2())) {
                        ins.getDst().setValue(exprTrue);
                    } else {
                        ins.getDst().setValue(exprFalse);                    
                    }
                 //   ins.printInstruction();                     
                    break;
                    
                case AND:
                 //   System.out.println("EVAL:AND");
                    if (isTrue(ins.getOp1()) && isTrue(ins.getOp2())) {
                        ins.getDst().setValue(exprTrue);
                    } else {
                        ins.getDst().setValue(exprFalse);                    
                    }
                 //   ins.printInstruction();                    
                    break;
                        
                case EQUAL:
                  //  System.out.println("EVAL:EQUAL");
                    ins.getDst().setValue(Comparator.isEqual(ins.op1.getValue(), ins.op2.getValue()) ? exprTrue :exprFalse);                    
                  //  ins.printInstruction();
                    break;
                    
                case NOT_EQUAL:
                //    System.out.println("EVAL:NOT_EQUAL");
                    ins.getDst().setValue(compare(ins.op1.getValue(), ins.op2.getValue()) != Operators.EQUAL ? exprTrue :exprFalse);                    
                 //   ins.printInstruction();                    
                    break;
                    
                case NOT:
                 //   System.out.println("EVAL:NOT: " + ins.getOp1().getValue());    
                //    System.out.println(ins.getOp1().getType().toString());
                    //IData zero = DataTypeHelper.getInstance().getFactoryByDatatype(ins.getOp1().getType().toString()).getNewInstance("0");
                    ins.getDst().setValue(compare(ins.op1.getValue(), exprFalse) == Operators.EQUAL ? exprTrue : exprFalse); 
                 //   ins.printInstruction();                    
                    break;                    
                    
                case EVAL:
                  //  System.out.println("EVAL");
                   // ins.printInstruction();
                  //  System.out.println(isTrue(ins.getDst()));
                    return isTrue(ins.getDst());
                    
                default:
                    System.err.println("Unhandled type!");
            }
        }
        
        return true;
    }
    
    
    /**
     * 
     * @param oper - Operator
     * @param op1 - Operand 1
     * @param op2 - Operand 2
     * @return - Return destination of an operation;
     * @throws AnalyzerException 
     */
    public ConditionItem addStep(OpenStageConditionAnalyzer.Operations oper, ConditionItem op1, ConditionItem op2) throws AnalyzerException {
        ConditionItem newItm = new ConditionItem();
        newItm.setName(getNextTmpConstName());
        
        newItm.setType(StackSymbols.BOOL);
        symbols.put(newItm.getName(), newItm);
        if (op1.getType() != op2.getType()) {
            if ((op1.getType() != StackSymbols.NUMBER || op2.getType() != StackSymbols.BOOL) && 
                (op1.getType() != StackSymbols.BOOL || op2.getType() != StackSymbols.NUMBER)) {
                throw new AnalyzerException("Data types are different (" + op1.getType() + " " + oper +" " + op2.getType()+")!");
            }
        }
        Instruction i = new Instruction(oper, newItm, op1, op2);
        instructions.add(i); 
        i.printInstruction();        
        return newItm;
    }
    
    /**
     * 
     * @param oper - Operator (!, =)
     * @param itm - Operand
     * @return
     * @throws AnalyzerException 
     */
    public ConditionItem addStep(OpenStageConditionAnalyzer.Operations oper, ConditionItem itm) throws AnalyzerException {
        ConditionItem newItm = new ConditionItem();
        newItm.setName(getNextTmpConstName());

        if (itm.getType() == StackSymbols.VARIABLE) {            
            newItm.setType(getDataTypeForVariableName(itm.getName()));
        } else {
            newItm.setType(itm.getType());
        }
        symbols.put(newItm.getName(), newItm);
        Instruction i = new Instruction(oper, newItm, itm);
        instructions.add(i);
        i.printInstruction();
        return newItm;
    }  
    
    private StackSymbols getDataTypeForVariableName(String name) throws AnalyzerException {
        IDataTypeFactory factory = DataTypeHelper.getInstance().getFactory(name);
        if (factory instanceof IntegerFactory) {
            return StackSymbols.NUMBER;
        } else if (factory instanceof StringFactory) {
            return StackSymbols.STRING;
        } else if (factory instanceof DateTimeFactory) {
            return StackSymbols.DATE;
        }
        
        System.out.println(factory + " " + name);
        
        throw new AnalyzerException("Unknown data type!");
    }
    
    /**
     * 
     * @param itm - Item to add into a table of symbols.
     * @return 
     */
    public ConditionItem addSymbol(ConditionItem itm) {
        if (itm.getName().isEmpty()) {
            itm.setName(getNextTmpConstName());
        }
        return symbols.put(itm.getName(), itm);
    }
    
    public void addConstant(String name, ConditionItem itm) {
        symbols.put(name, itm);
        
    }
    
    public void setValues(Map<String, IData> values) {
        this.values = values;
    }

    /**
     * Adds last step (EVAL) of an expression into a list of instruction.
     * @param originalMessage 
     */
    void addLastStep(ConditionItem originalMessage) {
        Instruction i = new Instruction(OpenStageConditionAnalyzer.Operations.EVAL, originalMessage);
        instructions.add(i);       
    }

    /**
     * Loads value of variable from map
     * @param op1
     * @param parsedMessage
     * @throws InterpretException 
     */
    private void loadFromMap(ConditionItem op1, IParsedMessage parsedMessage) throws InterpretException {
        parsedMessage.getDataForKey(op1.getName());
        IData data = parsedMessage.getDataForKey(op1.getName());
        if (data == null) {
            throw new InterpretException("Unsupported message (" + op1.getName() + ")");
        }    
        
        op1.setValue(data);
    }

    /**
     * This method is used for comparison with true value according to data type.
     * @param op1
     * @return 
     */
    private boolean isTrue(ConditionItem op1) {
        if (op1.getType().isNumber()) {         
            return !(compare(op1.getValue(), exprFalse) == Operators.EQUAL || op1.getValue().toString().length() == 0);
        } else if (op1.getType().isString()) {
            return op1.getValue().toString().length() != 0;
        } else if (op1.getType().isBool()) {
            return (compare(op1.getValue(), exprTrue) == Operators.EQUAL);
        }
        
        System.err.println("Unsupported type: "+op1.getType());
        return op1.getValue().toString().length() != 0 || compare(op1.getValue(), exprTrue) == Operators.EQUAL;
    }

    @Override
    public void setKeyValue(Map<String, String> keyValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This class is used as item of instruction's list.
     */
    private class Instruction {
        private OpenStageConditionAnalyzer.Operations operation;
        ConditionItem op1;
        ConditionItem op2;
        ConditionItem dst;
        
        public Instruction(OpenStageConditionAnalyzer.Operations operation, ConditionItem dst, ConditionItem op1) {
            this.operation = operation;
            assert(dst != null);
            this.dst = dst;
            this.op1 = op1;
        }
        
        

        public OpenStageConditionAnalyzer.Operations getOperation() {
            return operation;
        }

        public void setOperation(OpenStageConditionAnalyzer.Operations operation) {
            this.operation = operation;
        }

        public ConditionItem getOp1() {
            return op1;
        }

        public void setOp1(ConditionItem op1) {
            this.op1 = op1;
        }

        public ConditionItem getOp2() {
            return op2;
        }

        public void setOp2(ConditionItem op2) {
            this.op2 = op2;
        }

        public ConditionItem getDst() {
            return dst;
        }

        public void setDst(ConditionItem dst) {
            this.dst = dst;
        }
        
        public Instruction(OpenStageConditionAnalyzer.Operations operation, ConditionItem dst, ConditionItem op1, ConditionItem op2) {
            this.operation = operation;
            this.op1 = op1;
            this.op2 = op2;
            this.dst = dst;
        } 
        
        public Instruction(OpenStageConditionAnalyzer.Operations operation, ConditionItem dst) {
            this.operation = operation;
            assert(dst != null);
            this.dst = dst;
        }        
        
        public void printInstruction() {
            System.out.println("Op: " + operation + "\nDST: " + dst.getType() + " Name: " + dst.getName() + " Value: " + dst.getValue());
            if (op1 != null) {
                System.out.println("OP1: " + op1.getType() + " Name: " + op1.getName() + " Value: " + op1.getValue());
            }
            if (op2 != null) {
                System.out.println("OP2: " + op2.getType() + " Name: " + op2.getName() + " Value: " + op2.getValue());
            }
            System.out.println();
        }
        
    }
    
    public void printInstructions() {
        for (Instruction i : instructions) {
            i.printInstruction();
        }
    }
    
}
