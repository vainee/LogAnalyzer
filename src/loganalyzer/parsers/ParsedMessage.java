/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import loganalyzer.datatypes.IData;
import loganalyzer.utils.Pair;

/**
 *
 * @author kj000027
 */
public class ParsedMessage implements IParsedMessage{

    Map<String, IData> parsedMessages = new HashMap<>();
    
    @Override
    public List<Pair<Integer, String>> getOriginalMessage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, IData> getKeyValues() {
        return parsedMessages;
    }
    
    public void addKeyValue(String key, IData value) {
        parsedMessages.put(key, value);
    }

    @Override
    public IData getDataForKey(String key) {
        return parsedMessages.get(key);
    }
}
