/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.util.Iterator;

/**
 *
 * @author kj000027
 */
public interface IModel extends ICallbackInterface<IParsedMessage>, Iterator<IParsedMessage> {
    int getItemsCount();
    IParsedMessage getItemAtIndex(int index);
    
    
}
