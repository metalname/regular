/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import Dialogs.DialogHandler;
import Forms.AbstractHandler;
import Forms.AbstractKeyListener;
import Forms.CustomTableModel;
import Forms.TableData;
import Forms.TableRow;
import Paths.Paths;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import registry.Nodes.KeyNode;
import registry.RegistryException;
import registry.RegistryHive;

/**
 *
 * @author 
 */
public class SystemUSBDrivesHandler extends AbstractHandler implements AbstractKeyListener {
    
    private RegistryHive regSystem;
    private RegistryHive regSoftware;
    private String pathSystem;
    private String pathSoftware;
    private String pathInf;
    private DriveForm form;
    private ArrayList<ControlSet> controlSets;
    private final DriveRecordList driveRecordList = new DriveRecordList();
    private ArrayList<UserHive> userHives;
    private CustomTableModel model;
    private final String title = "USB Drives";
    private LogFile logFile;
    
    private class ControlSet {
        
        public String name;
        public KeyNode keyNode;
        
        public ControlSet(String name, KeyNode keyNode) {
            this.name = name;
            this.keyNode = keyNode;
        }
    }
    
    private class UserHive {
        
        public String name;
        public RegistryHive hive;
        
        public UserHive(String name, RegistryHive hive) {
            this.name = name;
            this.hive = hive;
        }
    }

    public SystemUSBDrivesHandler(DialogHandler dialogHandler) {
        super(dialogHandler);
    }
    
    @Override
    public boolean changed() {
        if (hiveChanged(regSystem)) {
            return (true);
        }
        if (hiveChanged(regSoftware)) {
            return (true);
        }
        if (userHives != null) {
            for (UserHive uh : userHives) {
                if (hiveChanged(uh.hive)) {
                    return (true);
                }
            }
        }
        if ((logFile != null) && (logFile.changed())) {
            return (true);
        }
        return (false);
    }
    
    @Override
    public void save() {
        saveHive(regSystem);
        saveHive(regSoftware);
        if (userHives != null) {
            for (UserHive uh : userHives) {
                saveHive(uh.hive);
            }
        }
        try {
            if ((logFile != null) && (logFile.changed())) {
                logFile.save();
            }
        } catch (IOException e) {
            frame().showErrorDialog(e.getMessage());
        }
        showInfo();
    }
    
    @Override
    public boolean close() {
        closeHive(regSystem);
        closeHive(regSoftware);
        if (userHives != null) {
            for (UserHive uh : userHives) {
                closeHive(uh.hive);
            }
        }
        return (true);
    }
    
    private void closeHive(RegistryHive hive) {
        if (hive != null) {
            hive.close();
        }
    }
    
    private boolean hiveChanged(RegistryHive hive) {
        return ((hive != null) && (hive.hiveChanged()));
    }
    
    private void saveHive(RegistryHive hive) {
        if (hiveChanged(hive)) {
            hive.save();
        }
    }
    
    @Override
    public boolean show() throws RegistryException {
        if (getRoot() && openHives() && openUserHives() && getControlSets()) {
            processControlSets();
            processMountedDevices();
            processUsers();
            processSoftware();
            processLogFile();
            form = new DriveForm(title, this);
            frame().getDesktopPane().add(form);
            form.setVisible(true);
            buildTable();
            return (true);
        }
        return (false);
    }
    
    private final String[] columns = {"Name", "Serial", "Volume GUID", "VID", "Last Mounted Drive Letter",
        "Used By", "Key Entries", "Value Entries", "Logfile Entries"};
    
    private void buildTable() {
        TableData data = new TableData();
        for (DriveRecord dr : driveRecordList) {
            data.add(makeTableRow(dr));
        }
        model = new CustomTableModel(data, columns);
        form.getTable().setModel(model);
        form.getTable().addKeyListener(this);
        form.getTable().setCellSelectionEnabled(true);
    }
    
    private TableRow makeTableRow(DriveRecord dr) {
        TableRow row = new TableRow();
        row.add(checkNull(dr.name()));
        row.add(checkNull(dr.serial()));
        row.add(checkNull(dr.guid()));
        row.add(checkNull(dr.vid()));
        row.add(checkNull(dr.lastMountedAs()));
        row.add(checkNull(dr.userList()));
        row.add(dr.keyEntries().size());
        row.add(dr.valueEntries().size());
        row.add(dr.ranges().size());
        return (row);
    }
    
    private String checkNull(String s) {
        return (s == null ? "(not found)" : s);
    }
    
    @Override
    public boolean getRoot() {
        if (super.getRoot()) {
            pathSystem = root + Paths.Hives + "SYSTEM";
            pathSoftware = root + Paths.Hives + "SOFTWARE";
            pathInf = root + Paths.Inf;
            return (true);
        }
        return (false);
    }
    
    public boolean openHives() throws RegistryException {        
        // open SYSTEM
        frame().showInfo("Opening SYSTEM hive...");        
        regSystem = new RegistryHive(pathSystem);
        if (!regSystem.open()) {
            frame().showErrorDialog("Could not open hive at " + pathSystem);
            return (false);
        }
        // open SOFTWARE
        frame().showInfo("Opening SOFTWARE hive...");        
        regSoftware = new RegistryHive(pathSoftware);
        if (!regSoftware.open()) {
            frame().showErrorDialog("Could not open hive at " + pathSoftware);
            return (false);
        }
        return (true);
    }
    
    private boolean getControlSets() {
        // look under SYSTEM root for keys like ControlSetXXX
        controlSets = new ArrayList<>();
        Pattern p = Pattern.compile("ControlSet[0-9]{3}");
        try {
            for (KeyNode node : regSystem.getRootNode().children()) {
                Matcher m = p.matcher(node.name());
                if (m.matches()) {
                    controlSets.add(new ControlSet(node.name(), node));
                }
            }
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
        }
        return (controlSets.size() > 0);
    }

    // process USBSTOR key all control sets
    // if a drives exists in more than one control set, it will be added 
    // to driveRecordList jsut once, but all key nodes will be added to the DriveRecord
    private void processControlSets() throws RegistryException {
        for (ControlSet cs : controlSets) {
            
            String path = cs.keyNode.path() + "/Enum/USBSTOR";
            KeyNode USBSTORNode = regSystem.loadKeyFromPath(path);
            driveRecordList.processUSBSTOR("SYSTEM", USBSTORNode);
            
            path = cs.keyNode.path() + "/Enum/USB";
            KeyNode USBNode = regSystem.loadKeyFromPath(path);
            driveRecordList.processUSB("SYSTEM", USBNode);
            
            path = cs.keyNode.path() + "/Control/DeviceClasses/{53f56307-b6bf-11d0-94f2-00a0c91efb8b}";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "##?#USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Control/DeviceClasses/{53f5630d-b6bf-11d0-94f2-00a0c91efb8b}";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "##?#STORAGE#VOLUME#_??_USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Control/DeviceClasses/{6ac27878-a6fa-4155-ba85-f98f491d4f33}";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "##?#WpdBusEnumRoot#UMB#2&37c186b&1&STORAGE#VOLUME#_??_USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Control/DeviceClasses/{f33fdc04-d1ac-4e8e-9a30-19bbd4b108ae}";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "##?#WpdBusEnumRoot#UMB#2&37c186b&1&STORAGE#VOLUME#_??_USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Control/DeviceClasses/{10497b1b-ba51-44e5-8318-a65c837b6661}";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "##?#WpdBusEnumRoot#UMB#2&37c186b&1&STORAGE#VOLUME#_??_USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Enum/STORAGE/Volume";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "_??_USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Enum/WpdBusEnumRoot/UMB";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyNameAndSerial("SYSTEM", "2&37c186b&1&STORAGE#VOLUME#_??_USBSTOR#", devNode);
            } catch (RegistryException e) {
            }
            
            path = cs.keyNode.path() + "/Control/DeviceClasses/{a5dcbf10-6530-11d2-901f-00c04fb951ed}";
            try {
                KeyNode devNode = regSystem.loadKeyFromPath(path);
                driveRecordList.processKeyVIDAndSerial("SYSTEM", "##?#USB#", devNode);
            } catch (RegistryException e) {
            }
        }
    }
    
    private void processSoftware() {
        String path = "Microsoft/Windows Portable Devices/Devices";
        try {
            KeyNode devNode = regSoftware.loadKeyFromPath(path);
            driveRecordList.processKeyNameAndSerial("SOFTWARE", "WPDBUSENUMROOT#UMB#2&37C186B&1&STORAGE#VOLUME#_??_USBSTOR#", devNode);
        } catch (RegistryException e) {
        }
    }

    // process MountedDevices key to get volume guid and last mounted root
    private void processMountedDevices() throws RegistryException {
        // volumnes are stored as values under /MountedDevices
        KeyNode mdNode = regSystem.loadKeyFromPath("MountedDevices");
        driveRecordList.processMountedDevices(mdNode);
    }

    // process each user hive to get mounted devices
    private boolean openUserHives() throws RegistryException {
        frame().showInfo("Opening user hives...");        
        userHives = new ArrayList<>();
        for (String user : dialogHandler.getUsers()) {
            String pathUserDat = root + Paths.Users + user + "\\NTUSER.DAT";
            // see if hive exists
            File file = new File(pathUserDat);
            if (file.exists()) {
                RegistryHive hive = new RegistryHive(pathUserDat);
                if (hive.open()) {
                    userHives.add(new UserHive(user, hive));
                } else {
                    frame().showErrorDialog("Could not open hive at " + pathUserDat);
                }
            }
        }
        return (true);
    }
    
    private void processUsers() throws RegistryException {
        for (UserHive userHive : userHives) {
            processUser(userHive);
        }
    }
    
    private void processUser(UserHive userHive) {
        try {
            KeyNode mountNode = userHive.hive.loadKeyFromPath("Software/Microsoft/Windows/CurrentVersion/Explorer/MountPoints2");
            driveRecordList.processUser(userHive.name, mountNode);
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
        }
    }
    
    public void showKeyTimestamps(ActionEvent e) {
        int row = form.getTable().getSelectedRow();
        if (row >= 0) {
            if (driveRecordList.get(row).keyEntries().size() > 0) {
                KeysPopupHandler kph = new KeysPopupHandler(this, driveRecordList.get(row));
            }
        }
    }
    
    private void deleteCurrentRow() {
        try {
            int row = form.getTable().getSelectedRow();
            if (row >= 0) {
                DriveRecord dr = driveRecordList.get(row);
                dr.delete(logFile);
                model.removeRow(row);
                form.getTable().addNotify();
            }
        } catch (RegistryException | IOException e) {
            frame().showErrorDialog(e.getMessage());
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentRow();
                showInfo();
                break;
        }
    }
    
    public void showInfo() {
        if (changed()) {
            form.btSave().setEnabled(true);
            form.title(title + " (not saved)");
        } else {
            form.btSave().setEnabled(false);
            form.title(title);
        }
    }

    // scan setup log file for USBSTOR entries
    private void processLogFile() {
        logFile = new LogFile(pathInf + "/setupapi.dev.log");
        frame().showInfo("Processing log file " + logFile.name());        
        try {
            logFile.read();
        } catch (IOException e) {
            frame().showErrorDialog(e.getMessage());
        }
        if (logFile.exists()) {
            try {
                driveRecordList.processLogFile(logFile);
            } catch (IOException e) {
                frame().showErrorDialog(e.getMessage());
            }
        }
        frame().showInfo("Completed processing log file " + logFile.name());        
    }
    
    public void showLogEntries(ActionEvent e) {
        int row = form.getTable().getSelectedRow();
        if ((row >= 0) && (driveRecordList.get(row).ranges().size() > 0)) {
            LogPopupHandler logPopupHandler = new LogPopupHandler(this, driveRecordList.get(row), logFile);
        }
    }
}
