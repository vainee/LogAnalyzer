package loganalyzer;

import java.io.FileNotFoundException;
import loganalyzer.datatypes.DataTypeHelper;
import loganalyzer.datatypes.DateTimeFactory;
import loganalyzer.datatypes.IDataTypeFactory;
import loganalyzer.datatypes.IntegerFactory;
import loganalyzer.datatypes.StringFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import loganalyzer.datatypes.IData;
import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.InterpretException;
import loganalyzer.filter.exceptions.LexicalException;
import loganalyzer.filter.interfaces.IConditionAnalyzer;
import loganalyzer.filter.openstagefilter.OpenStageConditionAnalyzer;


/**
 *
 * @author cz2b10w5
 */
public class LogAnalyzer {
    
    public static void doMain(
            ILogReader reader,
            ILogParser parser,
            IModel model,
            DataTypeHelper helper
            ) {
        parser.registerCallback(model);
        parser.setDataHelper(helper);
        while (reader.hasNext())
        {
            parser.parseMessage(reader.next());
        }
        parser.finishParsing();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, InterpretException {
        // TEST - REMOVE
        
//        IDataItem a = new IDataItem<string>
        
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
        //testHelper.insertWithPattern("", "");
        
        
                
        
/*        
        IData newNumber1 = helper.getFactory("PID").getNewInstance("123");
        IData newNumber2 = helper.getFactory("Cislo").getNewInstance("123");
       
        IData newString1 = helper.getFactory("Component").getNewInstance("SIP Signalling");
        IData newString2 = helper.getFactory("Component").getNewInstance("SIP Messages");
        IData newString3 = helper.getFactory("Retezec").getNewInstance("SIP Zoo");

        try {
            Integer comparisonInt1 = newNumber1.compareTo(newNumber2);
            System.out.println("Comparison result Int 1: " + comparisonInt1);
//            Integer comparisonInt2 = newNumber1.compareTo(newString1); // TODO: check
//            System.out.println("Comparison result Int 2: " + comparisonInt2);
            Integer comparisonStr1 = newString1.compareTo(newString2);
            System.out.println("Comparison result Str 1: " + comparisonStr1);
            Integer comparisonStr2 = newString1.compareTo(newString3);
            System.out.println("Comparison result Str 2: " + comparisonStr2);
        }
        catch (InvalidParameterException e) {
            System.err.println("A problem has occured while comparing the values.");
            System.exit(-1);
        }
        
        System.exit(0);
*/
        
        
        
        //IDataTypeFactory<> = new IntegerFactory<>();

        // TEST - REMOVE
        if (args.length != 1) {
            usage();
            System.exit(0);
        }     
        
        ILogReader reader = new FileLogReader(args[0]);
        ILogParser parser = new OpenStageLogParser();
        IModel model = new OpenStageModel();

        // TODO:
        //   - Parse options
        //   - Construct reader, parser and model using factories.

        /*
        reader = new LogFileReader();
        parser = new LogParser();
        model = new Model();
        */
        
        doMain(reader, parser, model, testHelper);
        IConditionAnalyzer analyzer = new OpenStageConditionAnalyzer();
        try {
            //analyzer.getCompiledCondition("(A == B) || A && (C == 5)").eval();
            //analyzer.getCompiledCondition("!(Pid == 2080)").eval(model.getItemAtIndex(1));
            analyzer.getCompiledCondition("Pid != 666 && Pid").eval(model.getItemAtIndex(1));
        } catch (LexicalException | AnalyzerException ex) {
            Logger.getLogger(LogAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void usage() {
//        System.out.println(System.getProperty("sun.java.command") + " inputFile");
    }
}
