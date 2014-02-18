/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.util.List;
import java.util.Map;
import loganalyzer.utils.Pair;

/**
 *
 * @author kj000027
 */
public interface IParsedMessage {
    List<Pair<Integer, String>> getOriginalMessage();
    
    Map<String, String> getKeyValues();
}
