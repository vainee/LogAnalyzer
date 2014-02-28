package loganalyzer;

import java.io.File;
import java.io.FileNotFoundException;


/**
 *
 * @author cz2b10w5
 */
public class LogAnalyzer {
    
    public static void doMain(
            ILogReader reader,
            ILogParser parser,
            IModel model) {

        parser.registerCallback(model);
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
    public static void main(String[] args) throws FileNotFoundException {
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
        
        doMain(reader, parser, model);
    }

    private static void usage() {
//        System.out.println(System.getProperty("sun.java.command") + " inputFile");
    }
}
