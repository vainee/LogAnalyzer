/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.interfaces;

/**
 *
 * @author kj000027
 */
public interface ICondition {
    boolean isPassed();
    IConditionItem getNextToken();
}
