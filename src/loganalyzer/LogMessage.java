/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

/**
 *
 * @author cz2b10w5
 */
public class LogMessage implements ILogMessage {
    private String message;
    private Integer line;
    
    public LogMessage(String message, Integer line) {
        this.message = message;
        this.line = line;
    }

    @Override
    public ILogMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public ILogMessage setLine(Integer line) {
        this.line = line;
        return this;
    }


    
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getLine() {
        return line;
    }
    
    
}
