/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer.filter.interfaces;

import java.util.Map;

/**
 *
 * @author kj000027
 */
public interface ICompiledCondition {
    boolean eval();
    void setKeyValue(Map<String, String> keyValue);
}
