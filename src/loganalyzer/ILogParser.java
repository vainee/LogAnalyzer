/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

/**
 *
 * @author cz2b10w5
 * @param <I>
 * @param <E>
 */
public interface ILogParser<I extends ILogMessage, E extends IParsedMessage> {
    void registerCallback(ICallbackInterface<E> callback);
    void parseMessage(I message);
}
