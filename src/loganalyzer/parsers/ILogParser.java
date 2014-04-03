/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.parsers;

import loganalyzer.ICallbackInterface;
import loganalyzer.ILogMessage;


import loganalyzer.datatypes.DataTypeHelper;

/**
 *
 * @author cz2b10w5
 */
public interface ILogParser {
    void registerCallback(ICallbackInterface<IParsedMessage> callback);
    void setDataHelper(DataTypeHelper typeHelper);
    void parseMessage(ILogMessage message);
    void finishParsing();
}
