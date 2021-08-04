/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

/**
 *
 * @author 
 */
public class Section {
    
    public static final int F_HEADER_USBSTOR = 0x01;   // header contains "Device Install (Hardware initiated) - USBSTOR"
    public static final int F_HEADER_USB     = 0x02;   // header contains "Device Install (Hardware initiated) - USB"
    public static final int F_DEV_USBSTOR    = 0x04;   // section contains the line "DevDesc" and "USB Mass Storage Device"

    private int start, end;
    private String date;
    private int flags;

    public Section() {
    }
    
    public int start() {
        return(start);
    }
    
    public void start(int start) {
        this.start = start;
    }
    
    public int end() {
        return(end);        
    }
    
    public void end(int end) {
        this.end = end;
    }
    
    public int flags() {
        return(flags);
    }
    
    public String date() {
        return(date);
    }
    
    public void scanLine(String line) {
        if (line.contains("Device Install (Hardware initiated) - USBSTOR")) {
            flags = flags | F_HEADER_USBSTOR;
        } else if (line.contains("Device Install (Hardware initiated) - USB")) {
            flags = flags | F_HEADER_USB;
        } else if (line.contains("DevDesc") && line.contains("USB Mass Storage Device")) {
            flags = flags | F_DEV_USBSTOR;
        } else if (line.contains(">>>  Section start")) {
            date = line.substring(20);
        }
    }
    
    public boolean hasFlag(int flag) {
        return((flags & flag) != 0);
    }
}
