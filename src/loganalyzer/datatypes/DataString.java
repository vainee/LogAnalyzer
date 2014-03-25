/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.datatypes;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class DataString implements IDataString<String>{

    private String value;

    DataString() {
    }

    DataString(String value) {
        this.value = value;
    }
    
    @Override
    public int compareTo(Object o) {
        String compareValue = ((DataString)o).getValue();
        return this.value.compareTo(compareValue);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
