/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.datatypes;

import java.util.Date;

/**
 * IDataNumber The concrete number interface of general type
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 * @param <T> The numeric type of the internal value
 */
public interface IDataDateTime<T extends Date> extends IData<T>{

}
