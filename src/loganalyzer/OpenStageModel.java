/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import loganalyzer.parsers.IParsedMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kj000027
 */
public class OpenStageModel implements IModel {
    private final List<IParsedMessage> items = new ArrayList<>();
    private int index = 0;

    @Override
    public void runCallback(IParsedMessage event) {
        if (!event.getKeyValues().isEmpty()) {
            //System.out.println(event.getKeyValues().values().toArray()[0]);
            //System.out.println(event.getKeyValues().toString() + "\n----------------");
            items.add(event);
        }
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = index < items.size();
        if (!hasNext) {
            index = 0;
        }
        return hasNext;
    }

    @Override
    public IParsedMessage next() {
        return items.get(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public IParsedMessage getItemAtIndex(int index) {
        return items.get(index);
    }
    
}
