package loganalyzer;


/**
 *
 * @author cz2b10w5
 */
public class LogAnalyzer {
    
    public static void doMain(
            ILogReader<ILogMessage> reader,
            ILogParser<ILogMessage, IParsedMessage> parser,
            IModel<IParsedMessage> model) {

        parser.registerCallback(model);
        while (reader.hasNext())
        {
            parser.parseMessage(reader.next());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ILogReader<ILogMessage> reader = null;
        ILogParser<ILogMessage, IParsedMessage> parser = null;
        IModel<IParsedMessage> model = null;

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
}
