/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author 
 */
public class FATDate {
    
    private int timestamp;
    private int day, month, year, second, minute, hour;
    
/*The DOS date/time format is a bitmask:
**
**              24                16                 8                 0
**+-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+
**|Y|Y|Y|Y|Y|Y|Y|M| |M|M|M|D|D|D|D|D| |h|h|h|h|h|m|m|m| |m|m|m|s|s|s|s|s|
**+-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+
** \___________/\________/\_________/ \________/\____________/\_________/
**    year        month       day      hour       minute        second
**
**The year is stored as an offset from 1980. 
**Seconds are stored in two-second increments. 
**(So if the "second" value is 15, it actually represents 30 seconds.)    
    */
    
    public FATDate(int timestamp) {
        this.timestamp = timestamp;
        unpack();
    }
    
    public FATDate(Date dt) {
        pack(dt);
    }
    
    private void unpack() {
        int t_date = timestamp & 0xffff;
        int t_time = timestamp  / 0x10000;
        
        day = (t_date & 0x1f);
        t_date = t_date >> 5;
        month = (t_date & 0x0f) ;
        t_date = t_date >> 4;
        year = t_date + 1980;
        
        second = (t_time & 0x1f) * 2;
        t_time = t_time >> 5;
        minute = (t_time & 0x3f);
        t_time = t_time >> 6;    
        hour = t_time;
    }
    
    private void pack(Date dt) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        year = cal.get(Calendar.YEAR);
        int t_date = year - 1980;
        month = cal.get(Calendar.MONTH) + 1;
        t_date = (t_date << 4) + month;
        day = cal.get(Calendar.DAY_OF_MONTH) + 1;
        t_date = (t_date << 5) + day;
        
        hour = cal.get(Calendar.HOUR_OF_DAY);
        int t_time = hour;
        minute = cal.get(Calendar.MINUTE);
        t_time = (t_time << 6) + minute;
        second = cal.get(Calendar.SECOND);
        t_time = (t_time << 5) + second / 2;
        
        timestamp = (t_time * 0x10000) + t_date;
    }
    
    @Override
    public String toString() {
        return(String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second));
    }
    
    public Date toDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);        
        cal.set(Calendar.DAY_OF_MONTH, day-1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return(cal.getTime());
    }
        
    public int timestamp() {
        return(timestamp);
    }
}
