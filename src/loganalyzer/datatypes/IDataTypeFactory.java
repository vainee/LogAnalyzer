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
public interface IDataTypeFactory {
    IData getNewInstance(String param);
    
}
