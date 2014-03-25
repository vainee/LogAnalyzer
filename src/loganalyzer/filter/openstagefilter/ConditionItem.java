/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.openstagefilter;

import java.util.HashMap;
import java.util.Map;
import loganalyzer.filter.openstagefilter.OpenStageConditionAnalyzer.StackSymbols;

/**
 *
 * @author kj000027
 */
public class ConditionItem /*implements IConditionItem*/ {

    protected Map<String, String> items = new HashMap<>();
    private StackSymbols type;
    private String value = ""; 
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StackSymbols getType() {
        return type;
    }

    public void setType(StackSymbols type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
