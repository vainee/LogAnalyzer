/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.parsers;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class ParserList {
    private static ParserList instance;
    private Map<String, ILogParser> parsers = new HashMap<>();

    private ParserList() {
    }
    
    public static ParserList getInstance()
    {
        if (instance == null)
        {
            instance = new ParserList();
        }
        return instance;
    }
    
    /**
     *
     * @param parserID
     * @param parserObject
     */
    public void add(String parserID, ILogParser parserObject)
    {
        parsers.put(parserID, parserObject);
    }
    
    /**
     *
     * @param parserID
     * @return
     */
    public ILogParser get(String parserID)
    {
        return parsers.get(parserID);
    }
    
    
    
    
    
    
}
