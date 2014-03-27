/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import loganalyzer.parsers.IParsedMessage;
import java.util.Iterator;

/**
 *
 * @author kj000027
 */
public interface IModel extends ICallbackInterface<IParsedMessage>, Iterator<ModelItem> {
    int getItemsCount();
    ModelItem getItemAtIndex(int index);
    
    
}
