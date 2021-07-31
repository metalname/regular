/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author 
 */
public class WindowsDate {

    private long filetime; // Windows FILETIME timestamp
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final long offset = 11644455600000L;

    // create from Windows FILETIME
    public WindowsDate(long filetime) {
        this.filetime = filetime;
    }

    // create from Java Date
    public WindowsDate(Date dt) {
        this.filetime = nativeToFiletime(dt.getTime());
    }

    // create from string
    public WindowsDate(String s) throws ParseException {
        this(fmt.parse(s));
    }

    @Override
    public String toString() {
        return (fmt.format(toDate()));
    }

    private static long filetimeToNative(long filetime) {
        return (filetime / 10000 - offset);
    }

    private static long nativeToFiletime(long timestamp) {
        return ((timestamp + offset) * 10000);
    }

    public Date toDate() {
        return (new Date(filetimeToNative(filetime)));
    }

    public long timestamp() {
        return (filetime);
    }

    public void timestamp(long filetime) {
        this.filetime = filetime;
    }

    public void timestamp(Date dt) {
        this.filetime = nativeToFiletime(dt.getTime());
    }

}
