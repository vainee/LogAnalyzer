/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import loganalyzer.datatypes.DataString;
import loganalyzer.datatypes.IData;
import loganalyzer.filter.exceptions.InterpretException;
import loganalyzer.filter.interfaces.ICompiledCondition;
import loganalyzer.parsers.IParsedMessage;


public class OpenStageView implements IView {

    private final List<ICompiledCondition> conditions = new ArrayList<>();
    private final OpenStageModel model;
    private IParsedMessage currentMsg = null;
    private int index = 0;
    private Set<DataString> components = new HashSet<>();
    
    
    public OpenStageView(OpenStageModel model) {
        this.model = model;
        while (model.hasNext()) {
            components.add((DataString)model.next().getDataForKey("Component"));
        }
        
        for (IData data : components) {
            System.out.println(data.toString());
        }
    }
    
    @Override
    public boolean hasNext() {
        if (currentMsg == null) {
            do {
                if (index >= model.getItemsCount()) {
                    index = 0;
                    currentMsg = null;
                    return false;
                }
                currentMsg = model.getItemAtIndex(index++);    
            } while(!IsPassed(currentMsg));
        }
        
        return true;
    }

    @Override
    public IParsedMessage next() {
        IParsedMessage tmpMsg = currentMsg;
        currentMsg = null;
        return tmpMsg;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void addCondition(ICompiledCondition condition) {
        conditions.add(condition);
    }
    
    private boolean IsPassed(IParsedMessage msg) {
        for (ICompiledCondition cc : conditions) {
            try {
                if (!cc.eval(msg)) {
                    return false;
                }
            } catch (InterpretException ex) {
                Logger.getLogger(OpenStageView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return true;
    }
    
}
