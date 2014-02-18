/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.util.Iterator;

/**
 *
 * @author cz2b10w5
 * @param <T>
 */
public interface ILogReader<T extends ILogMessage> extends Iterator<T> {
    
}
