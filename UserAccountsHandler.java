package Forms.System.Accounts;

import Dialogs.DialogHandler;
import Forms.AbstractHandler;
import Forms.CustomTableModel;
import Forms.TableData;
import Forms.TableForm;
import Forms.TableRow;
import Misc.SID;
import registry.RegistryHive;
import Paths.Paths;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueList;
import registry.Nodes.ValueNode;
import registry.RegistryException;
import registry.value.RegDataType;

/**
 *
 * Handler for the 'User Accounts' menu item
 * 
 */
public class UserAccountsHandler extends AbstractHandler implements KeyListener {
    
    private RegistryHive regSAM;            // registry hive SAM
    private RegistryHive regSoftware;       // registry hive SOFTWARE
    private String pathSAM;                 // path to SAM hive
    private String pathSoftware;            // path to SOFTWARE hive
    private ArrayList<UserRecord> users;    // list of user account records
    private TableForm form;                 // table form
    private CustomTableModel model;         // table model
    
    /**
     * Constructor
     * 
     * @param dialogHandler 
     */
    public UserAccountsHandler(DialogHandler dialogHandler) {
        super(dialogHandler);
    }
    
    /**
     * Implement the show method
     * This method will be called by the parent menu item handler
     * 
     * @return
     * @throws RegistryException 
     */
    @Override
    public boolean show() throws RegistryException {
        // gets the selected root path
        if (getRoot()) {
            frame().showInfo("Opening hives...");
            // open the hives
            if (processHives()) {
                frame().showInfo("Successfully opened hives SAM and SOFTWARE");
                // create a new table form
                form = new TableForm("User Accounts", this);
                // add form to parent frame
                frame().getDesktopPane().add(form);
                form.setVisible(true);
                // build table of user accounts
                buildTable();
                // set the table model with user account data
                form.getTable().setModel(model);
                form.getTable().addKeyListener(this);
                return (true);
            }
        }
        return (false);
    }
    
    /**
     * Process hive keys
     * @return
     * @throws RegistryException 
     */
    private boolean processHives() throws RegistryException {
        return (openHives() && getRIDs() && getFVKeys() && buildProfiles());
    }
    
    /**
     * Populates the table model
     */
    private void buildTable() {
        TableData data = new TableData();
        String[] columns = {"RID", "User", "Full Name", "SID", "Profile Path"};
        // iterate user records and add to table model
        for (UserRecord u : users) {
            TableRow row = new TableRow();
            row.add("" + u.rid());
            row.add(u.name());
            row.add(u.fullName());
            row.add(u.sid() == null ? "" : u.sid().toString());
            if (u.profilePath() != null) {
                row.add(u.profilePath());
            } else {
                row.add("(not set)");
            }
            data.add(row);
        }
        model = new CustomTableModel(data, columns);
    }
    
    /**
     * Gets the selected root path
     * 
     * @return 
     */
    @Override
    public boolean getRoot() {
        // call superclass getRoot() method
        if (super.getRoot()) {
            // construct paths
            pathSAM = root + Paths.Hives + "SAM";
            pathSoftware = root + Paths.Hives + "SOFTWARE";
            return (true);
        }
        return (false);
    }
    
    /**
     * Open registry hives
     * 
     * @return
     * @throws RegistryException 
     */
    private boolean openHives() throws RegistryException {
        // open SAM
        regSAM = new RegistryHive(pathSAM);
        if (!regSAM.open()) {
            frame().showErrorDialog("Could not open hive at " + pathSAM);
            return (false);
        }
        regSoftware = new RegistryHive(pathSoftware);
        if (!regSoftware.open()) {
            frame().showErrorDialog("Could not open hive at " + pathSoftware);
            return (false);
        }
        return (true);
    }

    /**
     * Close form
     * 
     * @return
     */
    @Override
    public boolean close() {
        if (regSAM != null) {
            regSAM.close();
        }
        if (regSoftware != null) {
            regSoftware.close();
        }
        return (true);
    }
    
    /**
     * Implement the changed() method
     * Checks if hives have been modified
     * 
     * @return 
     */
    @Override
    public boolean changed() {
        return ((regSAM != null) && (regSAM.hiveChanged())
                || (regSoftware != null) && (regSoftware.hiveChanged()));
    }
    
    /**
     * Implement the save() method
     * 
     */
    @Override
    public void save() {
        regSAM.save();
        regSoftware.save();
    }
    
    /**
     * Get the Relative Identifiers (RID) from the SAM hive
     * 
     * @return 
     */
    private boolean getRIDs() {
        // RIDs are stored in the 'type' field of the vk cells under 
        // SAM/Domains/Account/Users/Names
        // No, I don't know why either
        try {
            KeyNode node = regSAM.loadKeyFromPath("SAM/Domains/Account/Users/Names");
            users = new ArrayList<>();
            for (KeyNode userNode : node.children()) {
                String name = userNode.name();
                ValueList vl = userNode.values();
                int RID = -1;
                if (vl.size() > 0) {
                    RID = vl.get(0).vkCell().getDataType();
                }
                UserRecord ur = new UserRecord(RID, name);
                ur.nameNode(userNode);
                users.add(ur);
            }
            return (true);
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
            return (false);
        }
    }
    
    /**
     * Get F and V keys from SAM hive
     * 
     * @return 
     */
    private boolean getFVKeys() {
        // F and V values are stored under
        // SAM/Domains/Accounts/Users/${RID}
        try {
            KeyNode node = regSAM.loadKeyFromPath("SAM/Domains/Account/Users");
            for (KeyNode userNode : node.children()) {
                if (isHex(userNode.name())) {
                    int i = findRIDIndex(userNode.name());
                    if (i == -1) {
                        // RID could not be matched - add dummy record
                        users.add(new UserRecord(-1, "(unknown)"));
                        i = users.size() - 1;
                    }
                    for (ValueNode vn : userNode.values()) {
                        if ("F".equals(vn.name()) && (vn.type() == RegDataType.REG_BINARY)) {
                            users.get(i).setF(vn.getData());
                        }
                        if ("V".equals(vn.name()) && (vn.type() == RegDataType.REG_BINARY)) {
                            users.get(i).setV(vn.getData());
                        }
                    }
                    users.get(i).userNode(userNode);
                    //System.out.println(userNode.name());
                    //users.get(i).dumpV();
                }
            }
            return (true);
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
            return (false);
        }
    }

    /**
     * Get Security ID (SID) from SOFTWARE hive
     * 
     * Process profile paths from SOFTWARE/Microsoft/Windows NT/CurrentVersion/ProfileList/{SID}
     * User home directory will be in the ProfileImagePath value
     * 
     * @return 
     */
    private boolean buildProfiles() {
        try {
            KeyNode node = regSoftware.loadKeyFromPath("Microsoft/Windows NT/CurrentVersion/ProfileList");
            for (KeyNode userNode : node.children()) {
                // get Sid value
                UserRecord ur = null;
                String path = null;
                for (ValueNode vn : userNode.values()) {
                    if (vn.name().equals("Sid")) {
                        SID sid = SID.makeSID(vn.getData());
                        // skip entries with 'short' SIDs
                        // these are the service accounts - don't need them
                        if (sid.blocks() > 1) {
                            ur = findSID(sid);
                            if (ur == null) {
                                // this is an orphaned user - record in SOFTWARE but not SAM
                                ur = new UserRecord(sid.rid(), "(" + sid.rid() + ")");
                                ur.sid(sid);
                                ur.hasSAMRecord(false);
                                users.add(ur);
                            }
                        }
                    }
                    if (vn.name().equals("ProfileImagePath")) {
                        path = vn.toString();
                    }
                }
                if (ur != null) {
                    ur.profilePath(path);
                    ur.profileNode(userNode);
                }
            }
            return (true);
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
            return (false);
        }
    }

    /**
     ** Search user list for given SID
     */
    private UserRecord findSID(SID sid) {
        for (UserRecord ur : users) {
            if ((ur.sid() != null) && (ur.sid().equals(sid))) {
                return (ur);
            }
        }
        return (null);
    }
    
    private static final String hexChars = "0123456789ABCDEF";
    
    /**
     * Check if name is a hex value
     * 
     * @param name
     * @return 
     */
    private boolean isHex(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (hexChars.indexOf(name.charAt(i)) == -1) {
                return (false);
            }
        }
        return (true);
    }

    /**
    ** Search for given RID in arraylist and return index
    */
    private int findRIDIndex(String RIDString) {
        int rid;
        // RID should be a hex string
        rid = Integer.parseInt(RIDString, 16);
        int i = 0;
        for (UserRecord ur : users) {
            if (ur.rid() == rid) {
                return (i);
            }
            i++;
        }
        return (-1);
    }
    
    /**
     * Handle the delete key
     * 
     * @param e 
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentRow();
                break;
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    /**
     * Delete the selected user
     */
    private void deleteCurrentRow() {
        try {
            int index = form.getTable().getSelectedRow();
            if (index != -1) {
                users.get(index).deletekeys();      // delete registry keys
                users.remove(index);
                model.removeRow(index);
                form.getTable().addNotify();
            }
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
        }
    }
}
