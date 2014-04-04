/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.openstagefilter;

import java.util.ArrayList;
import java.util.List;
import loganalyzer.parsers.IParsedMessage;
import loganalyzer.parsers.ParsedMessage;
import loganalyzer.datatypes.DataTypeHelper;
import loganalyzer.datatypes.DateTimeFactory;
import loganalyzer.datatypes.IDataTypeFactory;
import loganalyzer.datatypes.IntegerFactory;
import loganalyzer.datatypes.StringFactory;
import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.InterpretException;
import loganalyzer.filter.exceptions.LexicalException;
import loganalyzer.filter.interfaces.ICompiledCondition;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author kj000027
 */
//@RunWith(JUnit4.class)
public class OpenStageConditionAnalyzerTest {
    
    public OpenStageConditionAnalyzerTest() {
    }
    
    private final static List<IParsedMessage> values = new ArrayList<>();
    private final static ParsedMessage parsedMessage = new ParsedMessage();
    
    @BeforeClass
    public static void setUpClass() {
	DataTypeHelper testHelper = DataTypeHelper.getInstance();
        // factories
        IDataTypeFactory numberFactory = new IntegerFactory();
        IDataTypeFactory stringFactory = new StringFactory();
        IDataTypeFactory dateTimeFactory = new DateTimeFactory();
        // templates
        testHelper.addFactoryPattern("Number", numberFactory);
        testHelper.addFactoryPattern("String", stringFactory);
        testHelper.addFactoryPattern("DateTime", dateTimeFactory);
        // specific type mappings
        testHelper.insertWithPattern("Type", "String");
        testHelper.insertWithPattern("Header", "String");
        testHelper.insertWithPattern("TraceLevel", "String");
        testHelper.insertWithPattern("DateTime", "DateTime");
        testHelper.insertWithPattern("Component", "String");
        testHelper.insertWithPattern("Pid", "Number");
        testHelper.insertWithPattern("SrcFile", "String");
        testHelper.insertWithPattern("SrcLine", "Number");
        // ???
        testHelper.insertWithPattern("Line2Message", "String");
        testHelper.insertWithPattern("TraceMessage", "String");  
        
        //ParsedMessage pm = new ParsedMessage();
        parsedMessage.addKeyValue("Pid", testHelper.getFactory("Pid").getNewInstance("666"));
        parsedMessage.addKeyValue("Type", testHelper.getFactory("Type").getNewInstance("Type"));        
        values.add(parsedMessage);
    }
    
    @AfterClass
    public static void tearDownClass() {        
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
  
    @Test
    public void testEmptyExpression() throws AnalyzerException, LexicalException {
        System.out.println("testEmptyExpression");
        String expression = "";
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        ICompiledCondition result = instance.getCompiledCondition(expression);
        assertNotNull(result);        
    }
    
    @Test(expected=AnalyzerException.class)
    public void testWrongInput1() throws AnalyzerException, LexicalException {
        System.out.println("testWrongInput1");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Pid == ";        
        result = instance.getCompiledCondition(expression);
        assertNotNull(result); 
    } 
    
    @Test(expected=AnalyzerException.class)
    public void testWrongInput2() throws AnalyzerException, LexicalException {
        System.out.println("testWrongInput2");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Pid &&";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result);         
       // result.eval(values.get(0));
    } 
    
    @Test(expected=AnalyzerException.class)
    public void testWrongInput3() throws AnalyzerException, LexicalException {
        System.out.println("testWrongInput3");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "&&";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result);         
       // result.eval(values.get(0));
    }    
    
    @Test(expected=LexicalException.class)
    public void testWrongInput4() throws AnalyzerException, LexicalException {
        System.out.println("testWrongInput4");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Pid & D";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result);         
    }    
    
    @Test(expected=AnalyzerException.class)
    public void testWrongInputDataType1() throws AnalyzerException, LexicalException {
        System.out.println("testWrongInputDataType1");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Pid == \"666\"";     
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result);         
    }    
    
    @Test(expected=AnalyzerException.class)
    public void testWrongInputDataType2() throws AnalyzerException, LexicalException {
        System.out.println("testWrongInputDataType2");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Type == 666";     
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result);         
    }       
    
    @Test
    public void testCorrectEval() throws AnalyzerException, LexicalException, InterpretException {
        System.out.println("testCorrectEval");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Pid == 666 && Pid";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertTrue(result.eval(parsedMessage));
        expression = "Pid == 666 && Pid > 5";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertTrue(result.eval(parsedMessage));
        expression = "Pid == 666 || Pid < 5";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertTrue(result.eval(parsedMessage));
        expression = "Pid <= 666";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertTrue(result.eval(parsedMessage));
        expression = "Pid >= 666 && Type >= \"Type\" && Type <= \"Type\"";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertTrue(result.eval(parsedMessage));  
    }
    
    @Test
    public void testIncorrectEval() throws AnalyzerException, LexicalException, InterpretException {
        System.out.println("testIncorrectEval");
        ICompiledCondition result;
        OpenStageConditionAnalyzer instance = new OpenStageConditionAnalyzer();
        String expression = "Pid != 666 && Pid";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertFalse(result.eval(parsedMessage));
        expression = "Pid == 666 && Pid < 5";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertFalse(result.eval(parsedMessage));
        expression = "Pid == 666 && Pid < 5";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertFalse(result.eval(parsedMessage));
        expression = "Pid > 666";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertFalse(result.eval(parsedMessage));
        expression = "Pid > 666 || Type != \"Type\"";        
        result = instance.getCompiledCondition(expression);       
        assertNotNull(result); 
        assertFalse(result.eval(parsedMessage));        
    }    
}
