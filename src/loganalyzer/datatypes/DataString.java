/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.datatypes;

import java.security.InvalidParameterException;
import java.util.Objects;
import loganalyzer.datatypes.Comparator.Operators;
import static loganalyzer.datatypes.Comparator.compare;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class DataString implements IData<String>, IDataString<String>{

    private String value;

    DataString() {
    }

    DataString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public int compareTo(IData<String> o) {
        if (o instanceof DataString) {
            String compareValue = ((DataString) o).getValue();
            return this.value.compareTo(compareValue);
        } else {
            throw new InvalidParameterException("Different types for comparison.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataString) {
            return ((DataString) o).compareTo(this) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
