/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.parsers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import loganalyzer.ICallbackInterface;
import loganalyzer.ILogMessage;
import loganalyzer.datatypes.DataTypeHelper;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class OpenStageLogParser implements ILogParser {

    private List<ICallbackInterface<IParsedMessage>> callbacks = new ArrayList<>();
    private List<ILogMessage> messageBuffer;
    private Integer errorsCount = 0;
    private DataTypeHelper dataHelper;

    public DataTypeHelper getDataHelper() {
        return dataHelper;
    }

    /**
     *
     * @param helper
     */
    @Override
    public void setDataHelper(DataTypeHelper helper) {
        this.dataHelper = helper;
    }

    
    // ************* regular expression strings *************

    private final String NEW_LINE_REGEX = "^(___TRACE:___|---INFO:---|___LOG:___|~~~DEBUG:~~~|\\+\\+\\+WARNING:\\+\\+\\+|\\*\\*\\*ERROR:\\*\\*\\*).*";

    private final String FILE_HEADER_REGEX = "^(?<Header>Trace file)";
    
    // EXAMPLE:
    // ___TRACE:___   Thu Aug  4 16:35:47 2011.677
    private final String FIRST_LINE_REGEX = 
        "(?<TraceLevel>___TRACE:___|---INFO:---|___LOG:___|~~~DEBUG:~~~|\\+\\+\\+WARNING:\\+\\+\\+|\\*\\*\\*ERROR:\\*\\*\\*)" +
        "\\s*" +
        "(?<DateTime>" +
        "(?<DayOfWeek>Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\s*" +
        "(?<Month>Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*" +
        "(?<Day>\\d*)\\s*" +
        "(?<Hour>\\d*):(?<Min>\\d*):(?<Sec>\\d*)\\s*(?<Year>\\d*).(?<MSec>\\d*))\\s*";
    
    // EXAMPLE:
    // DisplayAPI::QtMessageHandler(2283): ./src/QtMessageHandler.cpp:19 QDebug: Keypad Key Pressed (Handled by IdleScreen):  49 
    private final String SECOND_LINE_REGEX = 
        "^(?<Component>[^\\(]+)" +
        "\\((?<Pid>\\d+)\\): "+
        "(?<SrcFile>[^:]+):" +
        "(?<SrcLine>[\\d]+) " +
        "(?<Line2Message>.*)";

    
    // ************* regular expression patterns *************
    private final Pattern newLineRegex;
    private final Pattern fileHeaderRegex;
    private final Pattern firstLineRegex;
    private final Pattern secondLineRegex;

    /**
     * Constructor
     */
    public OpenStageLogParser() {
        // initialize the regex patterns
        this.newLineRegex = Pattern.compile(NEW_LINE_REGEX, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.fileHeaderRegex = Pattern.compile(FILE_HEADER_REGEX, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.firstLineRegex = Pattern.compile(FIRST_LINE_REGEX, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.secondLineRegex = Pattern.compile(SECOND_LINE_REGEX, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        
        this.messageBuffer = new LinkedList<>();
    }

    @Override
    public void registerCallback(ICallbackInterface<IParsedMessage> callback) {
        callbacks.add(callback);
    }
    
    private void runCallbacks(ParsedMessage msg) {
        for (ICallbackInterface<IParsedMessage> c : callbacks) {
            c.runCallback(msg);
        }
    }

    @Override
    public void parseMessage(ILogMessage message) {
        //System.out.println(message.getMessage());

        boolean isNewLog = isNewMessage(message.getMessage());
        // we have found new log and the previous one was not parsed yet
        if ((isNewLog) && (!messageBuffer.isEmpty())) {
            ParsedMessage singleMessage = parseSingleMessage(messageBuffer);
            // clear the buffer
            messageBuffer.clear();
            runCallbacks(singleMessage);
        }

        // add a new data to the buffer
        //System.out.println("Adding line2buffer: "+data); // ??????
        messageBuffer.add(message);
    }

    private boolean isNewMessage(String message) {
        Matcher m = newLineRegex.matcher(message);
        return m.matches();  /* TODO: consider to use find() */
    }
    
    private boolean checkHeader(ILogMessage msg, ParsedMessage output) {
        Matcher m = fileHeaderRegex.matcher(msg.getMessage());
        if (m.matches()) {
            //output.addKeyValue("Type", ParsedMessageType.HEADER.toString());
            output.addKeyValue("Type", dataHelper.getFactory("Type").getNewInstance(ParsedMessageType.HEADER.toString()));
            //output.addKeyValue("Header", m.group("Header"));
            output.addKeyValue("Header", dataHelper.getFactory("Header").getNewInstance(m.group("Header")));
            return true;
        }
        
        return false;
    }

    private ParsedMessage parseSingleMessage(List<ILogMessage> buffer) {
        ParsedMessage output = new ParsedMessage();
        StringBuilder textMessage = new StringBuilder();
        StringBuilder originalMessage = new StringBuilder();
        Integer line = 0;
        
        // iterate the buffer items
        for (ILogMessage msg : buffer) {
            line++;
            originalMessage.append(msg.getMessage()+"\n");
            
            // handle the file header as a special kind of input
            if (checkHeader(msg, output)) {
                // the file header has been found
                // ignore the remaining bufferred items and quit parsing
                return output;
            }
            
            // handle the other regular logs
            switch (line) {
                case 1:
                    // parse the first line
                    // example:
                    // ___TRACE:___   Thu Aug  4 16:35:54 2011.662
                    Matcher m1 = firstLineRegex.matcher(msg.getMessage());
                    if (m1.matches()) {
                        //output.addKeyValue("Type", ParsedMessageType.LOG.toString());
                        output.addKeyValue("Type", dataHelper.getFactory("Type").getNewInstance(ParsedMessageType.LOG.toString()));
                        
                        String traceLevel;
                        switch (m1.group("TraceLevel")) {
                            case "___TRACE:___":
                                traceLevel = "trace";
                                break;
                            case "---INFO:---":
                                traceLevel = "info";
                                break;
                            case "___LOG:___":
                                traceLevel = "log";
                                break;
                            case "~~~DEBUG:~~~":
                                traceLevel = "debug";
                                break;
                            case "+++WARNING:+++":
                                traceLevel = "warning";
                                break;
                            case "***ERROR:***":
                                traceLevel = "error";
                                break;
                            default:
                                System.err.println("Invalid trace level: " + m1.group("TraceLevel"));
                                traceLevel = "INVALID";
                                break;
                        }
                        //output.addKeyValue("TraceLevel", traceLevel);
                        output.addKeyValue("TraceLevel", dataHelper.getFactory("TraceLevel").getNewInstance(traceLevel));
                        
                        // Date/Time part
                        //DataDateTime date = new DataDateTime(m1.group("DateTime"));
                        output.addKeyValue("DateTime", dataHelper.getFactory("DateTime").getNewInstance(m1.group("DateTime")));
                        /*
                        output.addKeyValue("DayOfWeek", m1.group("DayOfWeek"));
                        output.addKeyValue("Month", m1.group("Month"));
                        output.addKeyValue("Day", m1.group("Day"));
                        output.addKeyValue("Hour", m1.group("Hour"));
                        output.addKeyValue("Minute", m1.group("Min"));
                        output.addKeyValue("Sec", m1.group("Sec"));
                        output.addKeyValue("Year", m1.group("Year"));
                        output.addKeyValue("MSec", m1.group("MSec"));
                        */
                        
                        
                    } else {
                        // report a problem
                        this.errorsCount++;
                        System.err.println("Unsupported message on line 1: " + msg.getMessage());
                    }
                    break;
                case 2:
                    // parse the second line
                    // example:
                    // SIP Signalling(2596): ./src/sg_engine.cpp:2325 CSipEngine::InternalAcceptA()
                    // regexp:
                    // <component>(<pid>): <srcFilename>:<srcLine> <logMessage>

                    Matcher m2 = secondLineRegex.matcher(msg.getMessage());
                    if (m2.matches()) {
                        //output.addKeyValue("Component", m2.group("Component"));
                        output.addKeyValue("Component", dataHelper.getFactory("Component").getNewInstance(m2.group("Component")));
                        //output.addKeyValue("Pid", m2.group("Pid"));
                        output.addKeyValue("Pid", dataHelper.getFactory("Pid").getNewInstance(m2.group("Pid")));
                        //output.addKeyValue("SrcFile", m2.group("SrcFile"));
                        output.addKeyValue("SrcFile", dataHelper.getFactory("SrcFile").getNewInstance(m2.group("SrcFile")));
                        //output.addKeyValue("SrcLine", m2.group("SrcLine"));
                        output.addKeyValue("SrcLine", dataHelper.getFactory("SrcLine").getNewInstance(m2.group("SrcLine")));
                        textMessage.append(m2.group("Line2Message"));
                    } else {
                        // report a problem
                        this.errorsCount++;
                        System.err.println("Unsupported message on line 2: " + msg.getMessage());
                    }
                    break;
                default:
                    // only append subsequent lines
                    textMessage.append("\n");
                    textMessage.append(msg.getMessage());
                    break;
            } //switch

        } //for
        
        if (textMessage.length() > 0) {
            //output.addKeyValue("TraceMessage", textMessage.toString());
            output.addKeyValue("TraceMessage", dataHelper.getFactory("TraceMessage").getNewInstance(textMessage.toString()));
        }
        if (originalMessage.length() > 0)
        {
            output.addKeyValue("OriginalMessage", dataHelper.getFactory("OriginalMessage").getNewInstance(originalMessage.toString()));
        }

        return output;
    }

    @Override
    public void finishParsing() {
        // flush/parse the rest of buffer
        if (!messageBuffer.isEmpty()) {
            ParsedMessage singleMessage = parseSingleMessage(messageBuffer);
            // clear the buffer
            messageBuffer.clear();
            runCallbacks(singleMessage);
        }

        // report the status
        // TODO: remove/make it public
        if (errorsCount > 0) {
            System.err.println("Parsing has been finished with " + errorsCount + " errors.");
        }
        else {
            System.out.println("Parsing has been finished without errors");
        }
    }
    
}
