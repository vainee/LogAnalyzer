/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package loganalyzer.datatypes;

/**
* IData is a generic interface for general data type.
* It could contain value of any supported/defined type
* and it is primarily intended to be used in a heterogenous
* collection.
* @author Pavel Vejnarek <vejnarek at gmail.com>
* @param <T> the real type of the internal value
*/
// TODO: how to force the Comparable interface to all
// implementations with specifying the Comparable generic type?
public interface IData<T> extends Comparable<IData<T>> {

    /**
* Default constructor without params
*/
    //void IData();

    /**
* Constructor with initial value
* @param value
*/
    //void IData(T value);
    
    /**
* Getter for the internal value
* @return internal value
*/
    T getValue();

    /**
* Setter for the internal value
* @param value value to be internally set to the type
*/
    void setValue(T value);
}