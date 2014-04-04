/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import loganalyzer.datatypes.DataTypeHelper;
import loganalyzer.datatypes.DateTimeFactory;
import loganalyzer.datatypes.IDataTypeFactory;
import loganalyzer.datatypes.IntegerFactory;
import loganalyzer.datatypes.StringFactory;
import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.LexicalException;
import loganalyzer.filter.interfaces.IConditionAnalyzer;
import loganalyzer.filter.openstagefilter.OpenStageConditionAnalyzer;
import loganalyzer.parsers.ILogParser;
import loganalyzer.parsers.OpenStageLogParser;

/**
 *
 * @author kj000027
 */
public class OpenStageModule implements IModule {

    private DataTypeHelper testHelper;
    
    public OpenStageModule() {
        initDataTypes();
    }
    
    @Override
    public String getModuleName() {
        return "openstage";
    }

    @Override
    public String getHelp() {        
        StringBuilder sb = new StringBuilder("Help for openstage module:\n");
        sb.append("-h Shows this help.\n");
        sb.append("-lc prints list of components include in trace file.\n");
        sb.append("-lp prints list of PID include in trace file.\n");
        return sb.toString();
    }

    @Override
    public void perform(String[] args) {
        System.out.println("Param: " + args[0]);
        String file = "";
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].compareTo("-h") == 0) {
                System.out.println(getHelp());
            } else if (args[i].compareTo("-lc") == 0) {
                System.out.println(getHelp());
            } else if (i == args.length - 1) {
                file = args[i];
            }
        }
        try {
            
            ILogReader reader = new FileLogReader(file);
            ILogParser parser = new OpenStageLogParser();
            IModel model = new OpenStageModel();
            
            doMain(reader, parser, model, testHelper);
            IConditionAnalyzer analyzer = new OpenStageConditionAnalyzer();
            OpenStageView view = new OpenStageView((OpenStageModel)model);
          /*  try {
                view.addCondition(analyzer.getCompiledCondition("Pid == 2732"));
                while(view.hasNext()) {
                    //view.next();
                    System.out.println(view.next().getDataForKey("TraceMessage").toString());
                }
            } catch (LexicalException | AnalyzerException ex) {
                Logger.getLogger(LogAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenStageModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initDataTypes() {
	testHelper = DataTypeHelper.getInstance();
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
    }
    
    private void doMain(
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

    private void printHelp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
