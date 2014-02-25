/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

/**
 *
 * @author kj000027
 */
public class OpenStageModel implements IModel{

    @Override
    public void runCallback(IParsedMessage event) {
        System.out.println(event.getKeyValues().values().toArray()[0]);
    }
    
}
