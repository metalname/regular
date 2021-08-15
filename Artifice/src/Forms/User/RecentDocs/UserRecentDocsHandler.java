package Forms.User.RecentDocs;

import Dialogs.DialogHandler;
import Forms.AbstractHandler;
import Forms.AbstractKeyListener;
import Forms.CustomTableModel;
import Forms.TableData;
import Forms.TableMouseListener;
import InputHelpers.InputHandler;
import Paths.Paths;
import Properties.MessageHandler;
import Properties.PropertiesHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import registry.Nodes.KeyNode;
import registry.RegistryException;
import registry.RegistryHive;

/**
 *
 * Handler for "Recent Docs" (from Registry)
 */
public class UserRecentDocsHandler extends AbstractHandler
        implements AbstractKeyListener, TableMouseListener, ActionListener, MessageHandler {

    private String pathUserDat;
    private RegistryHive regUserDat;
    private final ComboTableForm form;
    private CustomTableModel model;
    private final InputHandler inputHandler;
    private ArrayList<GroupRecord> groups;
    private final String title = "Recent Docs";
    private GroupRecord currentGroup;

    /**
     * Constructor
     * 
     * @param dialogHandler 
     */
    public UserRecentDocsHandler(DialogHandler dialogHandler) {
        super(dialogHandler);
        inputHandler = new InputHandler(dialogHandler.frame());
        form = new ComboTableForm(title, this);
    }

    @Override
    public boolean show() throws RegistryException  {
        if (getRoot() && getUser() && openHives()) {
            dialogHandler.frame().showInfo("Opening hives...");
            if (loadGroups()) {
                dialogHandler.frame().showInfo("Successfully opened hive NTUSER.DAT");
                dialogHandler.frame().getDesktopPane().add(form);
                form.setVisible(true);
                form.getTable().setModel(new DefaultTableModel());
                form.getTable().addKeyListener(this);
                form.getTable().addMouseListener(this);
                form.comboBox().addActionListener(this);
                form.comboBox().setSelectedIndex(0);
                return (true);
            }
        }
        return (false);
    }

    @Override
    public void showInfo(String message) {
        dialogHandler.frame().showInfo(message);
    }

    @Override
    public boolean close() {
        if (regUserDat != null) {
            regUserDat.close();
            regUserDat = null;
        }
        return (true);
    }

    @Override
    public boolean changed() {
        return ((regUserDat != null) && (regUserDat.hiveChanged()));
    }

    @Override
    public void save() {
        regUserDat.save();
        showTitle();
    }

    public void showTitle() {
        if (regUserDat.hiveChanged()) {
            form.btSave().setEnabled(true);
            form.title(title + " (not saved)");
        } else {
            form.btSave().setEnabled(false);
            form.title(title);
        }
    }

    private boolean openHives() throws RegistryException  {
        // open NTUSER.DAT
        regUserDat = new RegistryHive(pathUserDat);
        if (!regUserDat.open()) {
            dialogHandler.frame().showErrorDialog("Could not open hive at " + pathUserDat);
            return (false);
        }
        return (true);
    }

    @Override
    public boolean getUser() {
        if (super.getUser()) {
            pathUserDat = root + Paths.Users + user + "/NTUSER.DAT";            
            return(true);
        } else {
            return(false);
        }
    }

    private boolean loadGroups() {
        try {
            KeyNode node = regUserDat.loadKeyFromPath("Software/Microsoft/Windows/CurrentVersion/Explorer/RecentDocs");
            groups = new ArrayList<>();
            groups.add(new GroupRecord(node));
            for (KeyNode childNode : node.children()) {
                groups.add(new GroupRecord(childNode));
            }
            GroupRecord[] garray = new GroupRecord[groups.size()];
            groups.toArray(garray);
            form.comboBox().setModel(new DefaultComboBoxModel(garray));
            return (true);
        } catch (RegistryException e) {
            dialogHandler.frame().showErrorDialog(e.getMessage());
            return (false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        currentGroup = (GroupRecord) form.comboBox().getSelectedItem();
        loadTable(currentGroup);
    }

    private void loadTable(GroupRecord gr) {
        TableData data = new TableData();
        // records are stored in reverse MRU
        // read list in reverse
        try {
            for (int i = gr.records().size() - 1; i >= 0; i--) {
                data.add(gr.records().get(i).makeRow());
            }
            model = new CustomTableModel(data, RecentRecord.columns);
            form.getTable().setModel(model);
        } catch (RegistryException e) {
            dialogHandler.frame().showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentRecord();
        }
    }

    private void deleteCurrentRecord() {
        int row = form.getTable().getSelectedRow();
        try {
            if (row >= 0) {
                currentGroup.deleteKey((Integer) form.getTable().getValueAt(row, 0));
                model.removeRow(row);
                loadTable(currentGroup);
                showTitle();
            }
        } catch (RegistryException e) {
            dialogHandler.frame().showErrorDialog(e.getMessage());
        }
    }

    public void showProperties(ActionEvent evt) {
        int row = form.comboBox().getSelectedIndex();
        if (row >= 0) {
            PropertiesHandler propertiesHandler = new PropertiesHandler(this, groups.get(row).keyNode().nkCell());            
        }
    }
    
    @Override
    public void updateCurrentCell() {
        int row = form.comboBox().getSelectedIndex();
        if (row >= 0) {
            groups.get(row).keyNode().updateCell();
            showTitle();
        }        
    }
}
