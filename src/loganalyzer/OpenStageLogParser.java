/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kj000027
 */
public class OpenStageLogParser implements ILogParser {

    List<ICallbackInterface<IParsedMessage>> callbacks = new ArrayList<>();
    
    @Override
    public void registerCallback(ICallbackInterface<IParsedMessage> callback) {
        callbacks.add(callback);
    }

    @Override
    public void parseMessage(ILogMessage message) {
        //System.out.println(message.getMessage());
        for (ICallbackInterface<IParsedMessage> c : callbacks) {
            ParsedMessage msg = new ParsedMessage();
            msg.addKeyValue(String.valueOf(message.getLine()), message.getMessage());
            c.runCallback(msg);
        }
    }


    
}
