/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package loganalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cz2b10w5
 */
public class FileLogReader implements ILogReader {

    
    private final String fileName;
    private final BufferedReader inputReader;
    private boolean hasNextReady;
    
    private String data;
    private Integer lineNumber;

    
    public FileLogReader(String fileName) throws FileNotFoundException {
        hasNextReady = false;
        this.fileName = fileName;
        lineNumber = new Integer(0);

        inputReader = new BufferedReader(new FileReader(fileName));
    }

    @Override
    public boolean hasNext() {
        if (hasNextReady) {
            return true;
        }
        readLine();
        
        return hasNextReady;
    }

    @Override
    public ILogMessage next() {        
        if (!hasNextReady)
        {
            readLine();
        }
        
        hasNextReady = false;
        
        return new LogMessage(data, lineNumber);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private void readLine()
    {
        assert hasNextReady == false: "Unread data in input reader while reading new line.";
        
        try {
            if ((data = inputReader.readLine()) != null) {
                hasNextReady = true;
                lineNumber++;
            }
            else {
                hasNextReady = false;
                // throw new IOException("End of file reached.")
            }
        } catch (IOException ex) {
            Logger.getLogger(FileLogReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
