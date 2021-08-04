/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import BinBuffer.BinBuffer;
import java.io.IOException;
import java.util.ArrayList;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;

/**
 *
 * @author 
 */
public class DriveRecordList extends ArrayList<DriveRecord> {

    private static final long serialVersionUID = 5159218358193021598L;

    public DriveRecordList() {
        super();
    }

    // strip text after last '&'
    private String truncate(String serial) {
        for (int i = serial.length() - 1; i >= 0; i--) {
            if (serial.charAt(i) == '&') {
                if (i > 0) {
                    return (serial.substring(0, i));
                }
            }
        }
        return (serial);
    }

    public void processUSBSTOR(String name, KeyNode USBSTORNode) throws RegistryException {
        for (KeyNode diskEntry : USBSTORNode.children()) {
            // subnodes are serial numbers
            for (KeyNode serialEntry : diskEntry.children()) {
                // see if this disk is already in the list
                DriveRecord dr = findRecordBySerial(serialEntry.name());
                if (dr == null) {
                    dr = addRecord(diskEntry.name(), truncate(serialEntry.name()));
                }
                dr.addKeyEntry(name + serialEntry.path(), diskEntry);
            }
        }
    }

    public void processUSB(String name, KeyNode USBNode) throws RegistryException {
        // USB mass storage are stored as follows:
        // USB/VID_XXX
        //            /serial
        //
        // The serial will have the suffix (&n) removed
        for (KeyNode vidEntry : USBNode.children()) {
            // subnodes are serial numbers
            for (KeyNode serialEntry : vidEntry.children()) {
                // see if there is a matching record for this serial
                DriveRecord dr = findRecordBySerial(serialEntry.name());
                if (dr != null) {
                    dr.vid(vidEntry.name());
                    dr.addKeyEntry(name + serialEntry.path(), vidEntry);
                }
            }
        }
    }

    // processes a key with name of the form (prefix)#(name)#(serial)
    public void processKeyNameAndSerial(String name, String prefix, KeyNode devNode) throws RegistryException {
        for (KeyNode usbEntry : devNode.children()) {
            if (usbEntry.name().startsWith(prefix)) {
                String s = usbEntry.name().substring(prefix.length());
                String[] pieces = s.split("#");
                if (pieces.length >= 2) {
                    DriveRecord dr = findRecordBySerial(pieces[1]);
                    if (dr != null) {
                        dr.addKeyEntry(name + "/" + devNode.path() + usbEntry.name(), usbEntry);
                    } else {
                        // found a record, but it doesn't exist in the list
                        // this may be an orphaned entry
                        // add this name and serial number ot the list
                        dr = new DriveRecord(pieces[0], pieces[1]);
                        dr.addKeyEntry(name + "/" + devNode.path() + usbEntry.name(), usbEntry);
                        add(dr);
                    }
                }
            }
        }
    }

    // processes a key with name of the form (prefix)#(vid)#(serial)
    public void processKeyVIDAndSerial(String name, String prefix, KeyNode devNode) throws RegistryException {
        for (KeyNode usbEntry : devNode.children()) {
            if (usbEntry.name().startsWith(prefix)) {
                String s = usbEntry.name().substring(prefix.length());
                String[] pieces = s.split("#");
                if (pieces.length >= 2) {
                    DriveRecord dr = findRecordBySerial(pieces[1]);
                    if (dr != null) {
                        dr.addKeyEntry(name + "/" + devNode.path() + usbEntry.name(), usbEntry);
                    } else {
                        // found a record, but it doesn't exist in the list
                        // this may be an orphaned entry
                        // add this name and serial number ot the list
                        dr = new DriveRecord("(unknown)", pieces[1]);
                        dr.vid(pieces[0]);
                        dr.addKeyEntry(name + "/" + devNode.path() + usbEntry.name(), usbEntry);
                        add(dr);
                    }
                }
            }
        }
    }

    public void processMountedDevices(KeyNode mountedDevicesNode) throws RegistryException {
        for (ValueNode volumeNode : mountedDevicesNode.values()) {
            // for USB drives, the binary value is interpreted as a string
            // in the format _??_USBSTOR#name#serial#{guid}
            BinBuffer buffer = new BinBuffer(volumeNode.getData());
            // check if first two bytes are the UNICODE character underscore
            if (buffer.getChar() == '_') {
                // convert the rest of the data field to a string
                buffer.position(0);
                String vs = buffer.getUnicodeStringL(buffer.size() / 2);
                // sanity check - start of string should be "_??_USBSTOR"
                if (vs.startsWith("_??_USBSTOR")) {
                    // split the string at #
                    String[] pieces = vs.split("#");
                    // should be at least four pieces
                    if (pieces.length >= 4) {
                        // look for a matching record
                        DriveRecord dr = findRecordBySerial(pieces[2]);
                        // check the value key name to determine if this is a volumne guid or drive mount
                        // volume guid = \??\Volume{guid}
                        // drive mount = \DosDevices\N:
                        if (volumeNode.name().startsWith("\\??\\Volume")) {
                            // extract guid
                            dr.guid(volumeNode.name().substring(10));
                            dr.addValueEntry("SYSTEM/" + volumeNode.path(), volumeNode);
                        } else if (volumeNode.name().startsWith("\\DosDevices\\")) {
                            // extract drive
                            dr.lastMountedAs(volumeNode.name().substring(12));
                            dr.addValueEntry("SYSTEM/" + volumeNode.path(), volumeNode);
                        }
                    }
                }
            }
        }
    }

    public void processUser(String name, KeyNode keyNode) throws RegistryException {
        for (KeyNode deviceNode : keyNode.children()) {
            // try to match this guid with a drive
            DriveRecord dr = findRecordByGUID(deviceNode.name());
            if (dr != null) {
                String userList = dr.userList();
                dr.addKeyEntry("NTUSER.DAT(" + name + ")/" + deviceNode.path(), deviceNode);
                if (userList == null) {
                    dr.userList(name);
                } else {
                    dr.userList(userList + ";" + name);
                }
            }
        }
    }

    public void processLogFile(LogFile logFile) throws IOException {
        for (Section section: logFile.sections()) {
            if (section.hasFlag(Section.F_HEADER_USB) && section.hasFlag(Section.F_DEV_USBSTOR)) {
                // get vid and serial
                String[] pieces = logFile.get(section.start()).split("\\\\");                
                String serial = pieces[2].replace("]", "");
                if (pieces.length >= 3) {
                    DriveRecord dr = findRecordBySerial(serial);
                    if (dr == null) {
                        dr = new DriveRecord("(not found)", serial);
                        add(dr);
                    }
                    dr.vid(pieces[1]);
                    dr.addSection(section);
                }
            } else if (section.hasFlag(Section.F_HEADER_USBSTOR)) {
                // get name and serial
                String[] pieces = logFile.get(section.start()).split("\\\\");                
                String serial = pieces[2].replace("]", "");
                if (pieces.length >= 3) {
                    DriveRecord dr = findRecordBySerial(serial);
                    if (dr == null) {
                        dr = new DriveRecord(pieces[1], serial);
                        add(dr);
                    }
                    dr.name(pieces[1]);
                    dr.addSection(section);
                }
            }
        }
    }

    private int nextLine(StringBuilder sb, LogFile logFile, int line) {
        line++;
        sb.setLength(0);
        sb.append(logFile.get(line));
        return (line);
    }

    public DriveRecord findRecordBySerial(String serial) {
        for (DriveRecord dr : this) {
            if (dr.serial().toUpperCase().equals(truncate(serial.toUpperCase()))) {
                return (dr);
            }
        }
        return (null);
    }

    public DriveRecord findRecordByGUID(String guid) {
        for (DriveRecord dr : this) {
            if ((dr.guid() != null) && (dr.guid().equals(guid))) {
                return (dr);
            }
        }
        return (null);
    }

    public DriveRecord addRecord(String name, String serial) {
        DriveRecord dr = new DriveRecord(name, serial);
        add(dr);
        return (dr);
    }
}
