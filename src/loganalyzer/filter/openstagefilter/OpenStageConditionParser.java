/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.openstagefilter;

import java.util.ArrayList;
import java.util.List;
import loganalyzer.datatypes.DataString;
import loganalyzer.datatypes.DataTypeHelper;
import loganalyzer.datatypes.IData;
import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.LexicalException;
import loganalyzer.filter.interfaces.IParseCallback;
import loganalyzer.utils.Pair;


public class OpenStageConditionParser {

    private int index = 0;
    private String expression;
    private States state = States.INIT;
    private final List<IParseCallback<Pair<States, IData>>> callbacks = new ArrayList<>();
    
    public enum States {
        INIT,
        STRING,
        L_BRACKET,
        R_BRACKET,
        GREATER,
        GREATER_EQUAL,
        LESSER,
        LESSER_EQUAL,
        AND,
        ESCAPE,
        OR,
        EQUAL,
        VARIABLE,
        EOF, 
        NUMBER,
        REGEX,
        CONTAINS,
        NOT_EQUAL,
        NOT,
        ERROR,
        DOLAR
    };
    
    public void registerCallback(IParseCallback<Pair<States, IData>> callback) {
        callbacks.add(callback);
    }
    
    private void sendToken(States symbol) throws AnalyzerException, LexicalException {
        sendToken(symbol, null);
    }
    
    private void sendToken(States symbol, IData value) throws AnalyzerException, LexicalException {
        state = States.INIT;
        System.out.println(symbol.toString());
        Pair<States, IData> pair = new Pair<>(symbol, value);
        for (IParseCallback<Pair<States, IData>> itm : callbacks) {
            itm.runCallback(pair);
        }
    }

    public void parse(String expr) throws AnalyzerException, LexicalException {
        this.expression = expr;
        Character c;
        StringBuilder str = null;
     
        while (true) {

            c = getNext();
            
           /*if (c == null && (state == States.STRING || state == States.AND || state == States.OR || state == States.REGEX || state == States.NOT || state == States.REGEX)) {               
               sendToken(States.ERROR, "Unexcepted end of file");
               return;
            }*/
            switch (state) {
                case INIT:
                    if (c == null) {
                        sendToken(States.DOLAR);
                        return;
                    }
                    if (Character.isWhitespace(c)) {
                        continue;
                    }
                    str = new StringBuilder();
                    switch (c) {
                        case '(':
                            sendToken(States.L_BRACKET);
                            break;
                        case ')':
                            sendToken(States.R_BRACKET);
                            break;
                        case '=':
                            state = States.EQUAL;
                            break;
                        case '>':
                            state = States.GREATER;
                            break;
                        case '<':
                            state = States.LESSER;
                            break;     
                        case '|':
                            state = States.OR;
                            break;
                        case '&':
                            state = States.AND;
                            break;
                        case '"':
                            state = States.STRING;
                            break;   
                        case '/':
                            state = States.REGEX;
                            break;
                        case '~':
                            sendToken(States.CONTAINS);
                            break;   
                        case '!':
                            state = States.NOT;
                            break;    
                    }
                    
                    if (Character.isAlphabetic(c)) {
                        state = States.VARIABLE;
                        str.append(c);
                    } else if (Character.isDigit(c)) {
                        state = States.NUMBER;
                        str.append(c);
                    } else {                    
                        //throw new LexicalException("Unexcepted symbol: "+c);
                    }
                    
                    break;
                    
                case VARIABLE:
                    if (c != null && Character.isAlphabetic(c)) {
                        str.append(c);
                    } else {
                        sendToken(States.VARIABLE, DataTypeHelper.getInstance().getFactoryByDatatype("String").getNewInstance(str.toString()));
                        if (c != null) {
                            ungetc();
                        }
                    }
                    break;
                    
                case REGEX:
                    if (c != null) {
                        if (c == '\\') {
                            state = States.ESCAPE;
                        } else if (c == '/') {
                            sendToken(States.REGEX, DataTypeHelper.getInstance().getFactoryByDatatype("String").getNewInstance(str.toString()));
                        } else {
                            str.append(c);
                        }
                    } else {
                        throw new LexicalException("Unexcepted symbol: ");
                    }
                    break;    
                    
                case NOT:
                    if (c != null && c == '=') {
                        sendToken(States.NOT_EQUAL);   
                        break;
                    } else {
                        sendToken(States.NOT);
                        if (c != null) {
                            ungetc();
                        }                        
                        break;                        
                    }
                    
                    //throw new LexicalException("Symbol is supported! !" + c);                    
                    
                case NUMBER:
                    if (c != null && Character.isDigit(c)) {
                        str.append(c);
                    } else {
                        sendToken(States.NUMBER, DataTypeHelper.getInstance().getFactoryByDatatype("Number").getNewInstance(str.toString()));                         
                        if (c != null) {
                            ungetc();
                        } 
                    }
                    break;                    
                    
                case EQUAL:
                    if (c != null && c == '=') {
                        sendToken(States.EQUAL);
                    } else {
                        throw new LexicalException("Unexcepted symbol: ");
                    }
                    break;
                    
                case GREATER:
                    if (c != null && c == '=') {
                        sendToken(States.GREATER_EQUAL);
                    } else {
                        sendToken(States.GREATER);
                        if (c != null) {
                            ungetc();
                        } 
                    }
                    break;
                    
                case LESSER:
                    if (c != null && c == '=') {
                        sendToken(States.LESSER_EQUAL);
                    } else {
                        sendToken(States.LESSER);
                        if (c != null) {
                            ungetc();
                        } 
                    }
                    break;  
                    
                case OR:
                    if (c != null && c == '|') {
                        sendToken(States.OR);
                    } else {
                        throw new LexicalException("Unexcepted symbol: "); 
                    }
                    break; 
                    
                case AND:
                    if (c != null && c == '&') {
                        sendToken(States.AND);
                    } else {
                        throw new LexicalException("Unexcepted symbol: "); 
                    }
                    break;  
                    
                case ESCAPE:
                    if (c != null) {
                        if (c == '"') {
                            str.append(c);
                            state = States.STRING;
                        } else if (c == '/') {
                            str.append(c);
                            state = States.REGEX;
                        } else if (c == '\\') {
                            str.append(c);
                            state = States.STRING;
                        } else {
                            sendToken(States.ERROR);
                            return;
                        }
                        break;
                    }
                    throw new LexicalException("Unexcepted symbol: ");
                    
                case STRING:
                    if (c != null) {
                        if (c == '\\') {
                            state = States.ESCAPE;
                        } else if (c == '"') {
                            sendToken(States.STRING, DataTypeHelper.getInstance().getFactoryByDatatype("String").getNewInstance(str.toString()));
                        } else {
                            str.append(c);
                        }
                        
                        break;
                    }
                    
                    throw new LexicalException("Unexcepted end of the expression ");                  
            }
        }
        
    }
    
    private Character getNext() {
        return ((index == expression.length()) ? null : expression.charAt(index++));
    }
    
    private void ungetc() {
        if (index > 1) {
            index--;
        }
    }
    
}
