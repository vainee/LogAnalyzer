/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.datatypes;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class DataTypeHelper {
    
    private final Map<String, IDataTypeFactory> dataCollection = new HashMap<>();
    private Map<String, IDataTypeFactory> dataTypePatterns = new HashMap<>();
    
    /**
     *
     * @param key
     * @param factory
     */
    public void insert(String key, IDataTypeFactory factory)
    {
        dataCollection.put(key, factory);
        
    }
    
    /**
     *
     * @param key
     * @param factoryPattern
     */
    public void insertWithPattern(String key, String factoryPattern)
    {
        try {
            dataCollection.put(key, dataTypePatterns.get(factoryPattern));
        }
        catch (Exception e)
        {
            System.err.println("The type pattern with name '" + factoryPattern + "' does not exist");
        }
    }

    /**
     *
     * @param type
     * @return
     */
    public IDataTypeFactory getFactory(String type)
    {
        return dataCollection.get(type);
    }

    /**
     *
     * @param typeName
     * @param factoryPattern
     */
    public void addFactoryPattern(String typeName, IDataTypeFactory factoryPattern) {
        dataTypePatterns.put(typeName, factoryPattern);
    }
    
}
