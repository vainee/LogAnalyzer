/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.interfaces;

import loganalyzer.filter.exceptions.AnalyzerException;
import loganalyzer.filter.exceptions.LexicalException;

/**
 *
 * @author kj000027
 * @param <E>
 */
public interface IParseCallback<E> {
    void runCallback(E event) throws AnalyzerException, LexicalException;
}
