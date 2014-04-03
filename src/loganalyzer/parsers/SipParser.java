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
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class SipParser implements ILogParser {

    @Override
    public void registerCallback(ICallbackInterface<IParsedMessage> callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDataHelper(DataTypeHelper typeHelper) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void parseMessage(ILogMessage message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finishParsing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
