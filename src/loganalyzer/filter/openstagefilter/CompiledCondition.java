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
import loganalyzer.filter.interfaces.ICompiledCondition;
import loganalyzer.filter.openstagefilter.OpenStageConditionAnalyzer.StackSymbols;

/**
 *
 * @author kj000027
 */
public class CompiledCondition implements ICompiledCondition {
    private Map<String, ConditionItem> symbols = new HashMap<>();
    private Map<String, String> values;
    private List<Instruction> instructions = new LinkedList<>();
    private int tmpConstCounter = 0;
    
    private String getNextTmpConstName() {
        return "$"+String.valueOf(tmpConstCounter++);
    }
    
    @Override
    public boolean eval() {
        for (Instruction ins : instructions) {
            //ins.printInstruction();
            switch (ins.getOperation()) {
                case ASSIGN:
                    System.out.println("EVAL:ASSING");
                    ins.printInstruction();
                    if (ins.getOp1().getType() == StackSymbols.VARIABLE) {
                        loadFromMap(ins.getOp1());                        
                    } else {
                        ins.getDst().setType(ins.getOp1().getType());
                    }
                    ins.getDst().setValue(ins.getOp1().getValue());

                break;
                    
                case GREATER:
                    System.out.println("EVAL:GREATER");
                    if (compare(ins.getOp1(), ins.getOp2()) > 0) {
                        ins.getDst().setValue("1");
                    } else {
                        ins.getDst().setValue("0");                    
                    }
                    ins.printInstruction();                     
                    break;
                    
                case GREATER_EQUAL:
                    System.out.println("EVAL:GREATER_EQUAL");
                    if (compare(ins.getOp1(), ins.getOp2()) >= 0) {
                        ins.getDst().setValue("1");
                    } else {
                        ins.getDst().setValue("0");                    
                    }
                    ins.printInstruction();                      
                    break;
                    
                case LESSER:
                    System.out.println("EVAL:LESSER");
                    if (compare(ins.getOp1(), ins.getOp2()) < 0) {
                        ins.getDst().setValue("1");
                    } else {
                        ins.getDst().setValue("0");                    
                    }
                    ins.printInstruction();                       
                    break;
                    
                case LESSER_EQUAL:
                    System.out.println("EVAL:LESSER_EQUAL");
                    if (compare(ins.getOp1(), ins.getOp2()) <= 0) {
                        ins.getDst().setValue("1");
                    } else {
                        ins.getDst().setValue("0");                    
                    }
                    ins.printInstruction();                                                             
                    break;
                
                case OR:
                    System.out.println("EVAL:OR");
                    if (isTrue(ins.getOp1()) || isTrue(ins.getOp2())) {
                        ins.getDst().setValue("1");
                    } else {
                        ins.getDst().setValue("0");                    
                    }
                    ins.printInstruction();                     
                    break;
                    
                case AND:
                    System.out.println("EVAL:AND");
                    if (isTrue(ins.getOp1()) && isTrue(ins.getOp2())) {
                        ins.getDst().setValue("1");
                    } else {
                        ins.getDst().setValue("0");                    
                    }
                    ins.printInstruction();                    
                    break;
                        
                case EQUAL:
                    System.out.println("EVAL:EQUAL");
                    ins.getDst().setValue(String.valueOf(equal(ins.getOp1(), ins.getOp2())));                    
                    ins.printInstruction();
                    break;
                    
                case NOT_EQUAL:
                    System.out.println("EVAL:NOT_EQUAL");
                    ins.getDst().setValue(String.valueOf(!equal(ins.getOp1(), ins.getOp2())));                    
                    ins.printInstruction();                    
                    break;
                    
                case NOT:
                    System.out.println("EVAL:NOT: " + ins.getOp1().getValue());                  
                    ins.getDst().setValue(ins.getOp1().getValue().compareTo("0") == 0 ? "1" : "0");                    
                    ins.printInstruction();                    
                    break;                    
                    
                case EVAL:
                    System.out.println("EVAL");
                    ins.printInstruction();
                    break;
            }
        }
        
        return true;
    }
    
    public ConditionItem addStep(OpenStageConditionAnalyzer.Operations oper, ConditionItem op1, ConditionItem op2) {
        ConditionItem newItm = new ConditionItem();
        newItm.setName(getNextTmpConstName());
        
        newItm.setType(StackSymbols.BOOL);
        symbols.put(newItm.getValue(), newItm);
        Instruction i = new Instruction(oper, newItm, op1, op2);
        instructions.add(i); 
        i.printInstruction();
        return newItm;
    }
    
    public ConditionItem addStep(OpenStageConditionAnalyzer.Operations oper, ConditionItem itm) {
        ConditionItem newItm = new ConditionItem();
        newItm.setName(getNextTmpConstName());
        //newItm.setType(itm.getType());
        
        newItm.setType(StackSymbols.CONST);
        symbols.put(newItm.getValue(), newItm);
        Instruction i = new Instruction(oper, newItm, itm);
        instructions.add(i);
        i.printInstruction();
        return newItm;
    }  
    
    public ConditionItem addSymbol(ConditionItem itm) {
        if (itm.getName().isEmpty()) {
            itm.setName(getNextTmpConstName());
        }
        return symbols.put(itm.getName(), itm);
    }
    
    public void addConstant(String name, ConditionItem itm) {
        symbols.put(name, itm);
        
    }
    
    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    void addLastStep(ConditionItem originalMessage) {
        Instruction i = new Instruction(OpenStageConditionAnalyzer.Operations.EVAL, originalMessage);
        instructions.add(i);
        //i.printInstruction();        
    }

    private void loadFromMap(ConditionItem op1) {
        if (op1.getName().compareTo("A") == 0) {
            op1.setValue("A");
            return;
        } else if (op1.getName().compareTo("B") == 0) {
            op1.setValue("1");
            return;
        }      
    }

    private int compare(ConditionItem op1, ConditionItem op2) {
        if (op1.getType().isNumber()) {
            Integer o1 = Integer.valueOf(op1.getValue());
            Integer o2 = Integer.valueOf(op2.getValue());
            return o1 - o2;
        }
        return op1.getValue().compareTo(op2.getValue());
    }

    private boolean equal(ConditionItem op1, ConditionItem op2) {        
        return op1.getValue().compareTo(op2.getValue()) == 0;
    }

    private boolean isTrue(ConditionItem op1) {
        if (op1.getType().isNumber()) {
            return !(op1.getValue().compareTo("0") == 0 || op1.getValue().isEmpty());
        } else if (op1.getType().isString()) {
            return !op1.getValue().isEmpty();
        } else if (op1.getType().isBool()) {
            return (op1.getValue().compareTo("true") == 0);
        }
        
        System.err.println("Unsupported type: "+op1.getType());
        System.exit(5);
        return !op1.getValue().isEmpty() || op1.getValue().compareTo("true") == 0;
    }

    @Override
    public void setKeyValue(Map<String, String> keyValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
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
