/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loganalyzer.datatypes;

import java.security.InvalidParameterException;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class DataInteger implements IDataNumber<Integer> {

    private Integer value;

    DataInteger() {
    }

    DataInteger(Integer value) {
        this.value = value;
    }

    DataInteger(String param) {
        this.value = Integer.valueOf(param);
    }

/*    @Override
    public int compareTo(Object o) throws InvalidParameterException {
        if (o instanceof DataInteger) {
            Integer compareValue = ((DataInteger) o).getValue();
            return this.value.compareTo(compareValue);
        }
        else {
            throw new InvalidParameterException("Different types for comparison.");
        }
    }*/

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int compareTo(IData<Integer> o) {
        if (o instanceof DataInteger) {
            Integer compareValue = ((DataInteger) o).getValue();
            return this.value.compareTo(compareValue);
        } else {
            throw new InvalidParameterException("Different types for comparison.");
        }
    }
}
