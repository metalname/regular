/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import java.io.IOException;
import java.util.ArrayList;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;

/**
 *
 * @author 
 */
public class DriveRecord {

    private String name;
    private String serial;
    private String vid;
    private String guid;
    private String lastMountedAs;
    // keyEntries will store all the registry key entries related to this drive
    // this will allow all entries to be deleted in one shot
    private final ArrayList<KeyEntry> keyEntries = new ArrayList<>();
    // valueEntries will store all the registry value entries related to this drive
    // this will allow all entries to be deleted in one shot    
    private final ArrayList<ValueEntry> valueEntries = new ArrayList<>();
    // if this drive record has an entry in the setup log file, it will be recorded in sections    
    private final ArrayList<Section> sections = new ArrayList();
    private String userList;

    /*
    ** Inner class KeyEntry
    ** Holds data about a registry key entry for this drive record
    */
    public class KeyEntry {

        public String path;
        public KeyNode keyNode;

        public KeyEntry(String path, KeyNode keyNode) {
            this.path = path;
            this.keyNode = keyNode;
        }

        @Override
        public String toString() {
            return (path);
        }

        public void delete() throws RegistryException {
            keyNode.delete(true);
        }
    }

    /*
    ** Inner class ValueEntry
    ** Holds data about a registry value entry for this drive record
    */    
    public class ValueEntry {

        public String id;
        public ValueNode valueNode;

        public ValueEntry(String id, ValueNode valueNode) {
            this.id = id;
            this.valueNode = valueNode;
        }

        @Override
        public String toString() {
            return (id);
        }

        public void delete() throws RegistryException {
            valueNode.delete(true);
        }
    }
    
    // strip text after last '&'
    private String truncate(String serial) {
        for (int i = serial.length() - 1; i >= 0; i--) {
            if (serial.charAt(i) == '&') {
                if (i > 0) {
                    return(serial.substring(0, i));
                }
            }
        }
        return(serial);
    }    

    public DriveRecord(String name, String serial) {
        this.name = name;
        this.serial = truncate(serial);
    }
    
    public String name() {
        return (name);
    }

    public void name(String name) {
        this.name = name;
    }

    public String serial() {
        return (serial);
    }

    public void serial(String serial) {
        this.serial = truncate(serial);
    }

    public String truncatedSerial() {
        String s = serial();
        // search for '&'
        int i = s.indexOf("&");
        if (i > 0) {
            s = s.substring(0, i);
        }
        return (s);
    }

    public String guid() {
        return (guid);
    }

    public void guid(String guid) {
        this.guid = guid;
    }

    public String lastMountedAs() {
        return (lastMountedAs);
    }

    public void lastMountedAs(String lastMountedAs) {
        this.lastMountedAs = lastMountedAs;
    }

    public String vid() {
        return (vid);
    }

    public void vid(String vid) {
        this.vid = vid;
    }

    public String userList() {
        return (userList);
    }

    public void userList(String userList) {
        this.userList = userList;
    }

    public void addKeyEntry(String path, KeyNode node) {
        keyEntries.add(new KeyEntry(path, node));
    }

    public void addValueEntry(String id, ValueNode node) {
        valueEntries.add(new ValueEntry(id, node));
    }

    public void delete(LogFile logFile) throws RegistryException, IOException {
        deleteKeys();
        deleteValues();
        deleteRanges(logFile);
    }

    private void deleteKeys() throws RegistryException {
        for (KeyEntry entry : keyEntries) {
            entry.delete();
        }
    }

    private void deleteValues() throws RegistryException {
        for (ValueEntry entry : valueEntries) {
            entry.delete();
        }
    }
    
    private void deleteRanges(LogFile logFile) {
        for (Section section: sections) {
            logFile.deleteSection(section);
        }
    }
        
    public void addSection(Section section) {
        sections.add(section);
    }
    
    public ArrayList<KeyEntry> keyEntries() {
        return (keyEntries);
    }

    public ArrayList<ValueEntry> valueEntries() {
        return(valueEntries);
    }
    
    public ArrayList<Section> ranges() {
        return(sections);
    }
    
}
