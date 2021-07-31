package Forms;

import RegTableHandler.RegTableHandler;
import TreeHandler.TreeHandler;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import registry.ErrorListener;
import registry.RegistryException;
import registry.RegistryHive;
import registry.Verify.RegistryChecker;

/**
 * This class handles actions launched from the main form (Menu selections,
 * Toolbar buttons etc.)
 *
 */
public class FormController implements ErrorListener {

    private static final String defaultFormatString = "yyyyMMdd_HHmmss";
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat(defaultFormatString);
    
    private final MainFrame frame;              // this is the eclosing frame
    private String filename;                    // path of selected hive
    private RegistryHive hive;                  // registry hive object
    private final JFileChooser fc = new JFileChooser();
    private TreeHandler treeHandler;            // handler for tree view (left-hand pane)
    private RegTableHandler regTableHandler;    // handler for table view (right-hand pane)
    private final DeleteOptions deleteOptions;  // handler object for shared delete dialog
    private String backupDir;                   // directory to place backup registry files

    /**
     * Constructor
     *
     * @param frame
     */
    public FormController(MainFrame frame) {
        this.frame = frame;
        deleteOptions = new DeleteOptions(frame);        
        createBackupDir();
    }
    
    /**
     * Create the registry backup folder
     */
    private void createBackupDir() {
        backupDir = System.getProperty("user.home") + "/regBackup";
        File dir = new File(backupDir);
        dir.mkdir();
    }

    private void backupHive() {
        String suffix = "_" + defaultDateFormat.format(new Date());
        Path path = Paths.get(hive.getFilename());
        String backupPath = backupDir + '/' + path.getFileName() + suffix;
        hive.saveAs(backupPath);
        System.out.println("Hive backup placed in " + backupPath);
    }
    
    /**
     * Update the main form title
     */
    public void showTitle() {
        if (filename != null) {
            // a hive is open - show filename and modified indicator
            frame.setTitle("Regular - " + filename + (hive.hiveChanged() ? " (modified)" : ""));
        } else {
            // no hive selected
            frame.setTitle("Regular");
        }
    }

    /**
     * Implement the notify method of the ErrorListener interface
     *
     * @param message
     */
    @Override
    public void notify(String message) {
        frame.showErrorDialog(message);
    }

    /**
     * Attempts to open a registry hive
     *
     * @return
     */
    protected boolean openHive() {
        closeHive();    // close hive if previously opened
        hive = new RegistryHive(filename);      // instantiate hive object
        try {
            if (hive.open(this)) {
                showTitle();    // update title
                if (regTableHandler == null) {
                    // create a new registry cell handler (right-hand side of pane)
                    regTableHandler = new RegTableHandler(frame, this);
                }
                if (treeHandler == null) {
                    // create a new key tree handler (right-hand side of pane)
                    treeHandler = new TreeHandler(frame, hive, this);
                } else {
                    // if handler already exists, just pass the new hive object
                    treeHandler.hive(hive);
                }
                // populate the key hierarchy tree (left-hand side of pane)
                if (treeHandler.populateTree()) {
                    frame.showInfo("Successfully opened hive " + filename);
                    backupHive();
                    return (true);
                }
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
        }
        frame.showInfo("Could not open hive " + filename);
        return (false);
    }

    /**
     * Save changes to hive
     *
     * @return
     */
    protected boolean saveHive() {
        if ((hive != null) && (hive.save())) {
            showTitle();
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * Getter for hive
     *
     * @return
     */
    public RegistryHive getHive() {
        return (hive);
    }

    /**
     * Invoke the open file dialog
     *
     * @return
     */
    protected boolean openFileDialog() {
        int retval = fc.showOpenDialog(frame);
        if (retval == JFileChooser.APPROVE_OPTION) {
            // store filename
            filename = fc.getSelectedFile().toString();
        }
        return (retval == JFileChooser.APPROVE_OPTION);
    }

    /**
     * Open a registry file (called from menu item or toolbar button)
     *
     */
    public void fileOpen() {
        if (confirmFileClose()) {
            if (openFileDialog()) {
                openHive();
            }
        }
    }

    /**
     * Close a previously opened hive
     *
     */
    private void closeHive() {
        if (hive != null) {
            hive.close();
            // empty the hive key tree (left-hand side of pane)
            frame.getTree().setModel(null);
            frame.getTable().setModel(new DefaultTableModel());
            showTitle();
        }
    }

    /**
     * Close the registry hive (called from menu item or toolbar button)
     *
     * @return
     */
    public boolean fileClose() {
        if (confirmFileClose()) {
            filename = null;
            closeHive();
            return (true);
        }
        return (false);
    }

    /**
     * Check if hive has been modified
     *
     * @return
     */
    private boolean hiveChanged() {
        if (hive == null) {
            return (false);
        } else {
            return (hive.hiveChanged());
        }
    }

    /**
     * Called on exit or open of new hive Checks if current hive has been
     * modified
     *
     * @return
     */
    public boolean confirmFileClose() {
        if (hiveChanged()) {
            // verify exit
            int retval = JOptionPane.showOptionDialog(frame,
                    "Hive has not been saved. Save now?",
                    "Confirm Hive Close",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    null, //the titles of buttons
                    null);  //default button title
            switch (retval) {
                case JOptionPane.YES_OPTION:
                    if (saveHive()) {
                        return (true);
                    } else {
                        return (false);
                    }
                case JOptionPane.NO_OPTION:
                    return (true);
                default:
                    return (false);
            }
        } else {
            return (true);
        }
    }

    /**
     * Show cell properties Called from menu item or toolbar button
     *
     */
    public void showProperties() {
        if (treeHandler != null) {
            treeHandler.showProperties();
        }
    }

    /**
     * Getter for regTablehandler
     *
     * @return
     */
    public RegTableHandler regTableHandler() {
        return (regTableHandler);
    }

    /**
     * Getter for treeHandler
     *
     * @return
     */
    public TreeHandler treeHandler() {
        return (treeHandler);
    }

    /**
     * Wipe all hive free space Called from menu item
     *
     */
    public void wipeHive() {
        if ((hive != null) && (hive.isOpen())) {
            frame.showInfo("Wiping hive free space...");
            int count = hive.wipeHiveFreeSpace();
            frame.showInfo("Wiped " + count + " cells");
            showTitle();
        }
    }

    /**
     * Scan hive for errors
     *
     */
    public void scanHive() {
        if ((hive != null) && (hive.isOpen())) {
            frame.showInfo("Checking hive...");
            RegistryChecker checker = new RegistryChecker(hive);
            checker.check();
            frame.showInfo("Completed checking hive");
            showTitle();
        }
    }

    /**
     * Handle delete cell toolbar button and menu item
     */
    public void deleteCell() {
        switch (frame.getFocusedPane()) {
            case FOCUS_LEFT:
                treeHandler.handleDeleteDialog();
                break;
            case FOCUS_RIGHT:
                regTableHandler.handleDeleteDialog();
                break;
        }
    }
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Inner class
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public class DeleteOptions {

        public boolean optWipe = true;              // boolean toggle for wipe key option
        private boolean optSuppress = false;         // boolean toggle for show delete options dialog
        private JCheckBox ckWipe;                    // check box - set if wipe deleted keys is on
        private JCheckBox ckSuppress;                // check box - set if 'don't show again' is selected on delete dialog
        private JPanel deleteDialogPanel;                   // delete dialog panel

        public DeleteOptions(MainFrame frame) {
            createDeleteDialogPanel();
        }

        /**
         * Create the delete dialog panel
         */
        private void createDeleteDialogPanel() {
            deleteDialogPanel = new JPanel();
            GridLayout layout = new GridLayout(0, 1);
            deleteDialogPanel.setLayout(layout);
            deleteDialogPanel.add(new JLabel("Really delete?"));
            ckWipe = new JCheckBox("Wipe cells", optWipe);
            ckSuppress = new JCheckBox("Don't ask again", optSuppress);
            deleteDialogPanel.add(ckWipe);
            deleteDialogPanel.add(ckSuppress);
        }

        /**
         * Show the delete dialog panel
         *
         * @return
         */
        public boolean showDeleteDialog() {
            if (optSuppress) {
                return (true);
            }
            // set the wipe and suppress check boxes
            ckWipe.setSelected(optWipe);
            ckSuppress.setSelected(optSuppress);            
            int ret = JOptionPane.showOptionDialog(frame, deleteDialogPanel,
                    "Confirm Delete", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (ret == JOptionPane.OK_OPTION) {
                optWipe = ckWipe.isSelected();
                optSuppress = ckSuppress.isSelected();
                return (true);
            } else {
                return (false);
            }
        }

    }    
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    public boolean showDeleteDialog() {
        return (deleteOptions.showDeleteDialog());
    }

    public DeleteOptions getDeleteOptions() {
        return(deleteOptions);
    }

}
