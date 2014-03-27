/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loganalyzer.datatypes;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Pavel Vejnarek <vejnarek at gmail.com>
 */
public class DataDateTime implements IDataDateTime<Date> {

    private Date value;

    DataDateTime() {
    }

    DataDateTime(Date value) {
        this.value = value;
    }
    
    DataDateTime(int year, int month, int date, int hrs, int min, int sec, int msec) {
        
        // TODO:
        /*Calendar cal = new Gregori();
        this.value = Calendar
        Date(year, month, date, hrs, min, sec);
        this.value.
        */
    }

    DataDateTime(String param) {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM  d HH:mm:ss yyyy.SSS", Locale.US);
            // parse string in custom format into date object
            // Thu Jul 14 12:09:10 2011.076
            value = df.parse(param);
        }
        catch (ParseException pe) {
            System.err.println("DateTime parse exception: " + pe.getMessage());
        }
    }

    @Override
    public int compareTo(Object o) throws InvalidParameterException {
        if (o instanceof DataDateTime) {
            Date compareValue = ((DataDateTime) o).getValue();
            return this.value.compareTo(compareValue);
        }
        else {
            throw new InvalidParameterException("Different types for comparison.");
        }
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public void setValue(Date value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
