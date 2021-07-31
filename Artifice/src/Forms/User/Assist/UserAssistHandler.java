/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.Assist;

import Dialogs.DialogHandler;
import Forms.AbstractHandler;
import Forms.AbstractKeyListener;
import Forms.CellRef;
import Forms.TableMouseListener;
import Forms.CustomTableModel;
import Forms.TableData;
import Forms.TableRow;
import InputHelpers.InputHandler;
import Paths.Paths;
import Properties.MessageHandler;
import Properties.PropertiesHandler;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;
import registry.RegistryHive;
import utils.WindowsDate;

/**
 *
 * @author 
 */
public class UserAssistHandler extends AbstractHandler
        implements AbstractKeyListener, TableMouseListener, MessageHandler {

    private String pathUserDat;
    private RegistryHive regUserDat;
    private UserAssistForm form;
    private CustomTableModel model;
    private final ArrayList<AssistRecord> assistEntries = new ArrayList<>();
    private final InputHandler inputHandler;
    private final String title = "User Assist";
    private PropertiesHandler propertiesHandler;

    public UserAssistHandler(DialogHandler dialogHandler) {
        super(dialogHandler);
        inputHandler = new InputHandler(dialogHandler.frame());
    }

    @Override
    public void showInfo(String message) {
        dialogHandler.frame().showInfo(message);
    }

    @Override
    public void updateCurrentCell() {

    }

    @Override
    public boolean show() throws RegistryException {
        if (getRoot() && getUser()) {
            dialogHandler.frame().showInfo("Opening hives...");
            if (processHives()) {
                dialogHandler.frame().showInfo("Successfully opened hive NTUSER.DAT");
                form = new UserAssistForm(title, this);
                dialogHandler.frame().getDesktopPane().add(form);
                form.setVisible(true);
                buildTable();
                form.getTable().setModel(model);
                form.getTable().addKeyListener(this);
                form.getTable().addMouseListener(this);
                return (true);
            }
        }
        return (false);
    }

    private void buildTable() {
        TableData data = new TableData();
        String[] columns = {"Name", "Count", "Timestamp"};
        for (AssistRecord ar : assistEntries) {
            TableRow row = new TableRow();
            row.add(ar.name());
            row.add("" + ar.count());
            if (ar.isUEMESession()) {
                row.add("");
            } else {
                row.add(new WindowsDate(ar.timestamp()));
            }
            data.add(row);
        }
        model = new CustomTableModel(data, columns);
    }

    @Override
    public boolean getUser() {
        if (super.getUser()) {
            pathUserDat = root + Paths.Users + user + "/NTUSER.DAT";
            return (true);
        } else {
            return (false);
        }
    }

    private boolean openHives() throws RegistryException {
        // open NTUSER.DAT
        regUserDat = new RegistryHive(pathUserDat);
        if (!regUserDat.open()) {
            dialogHandler.frame().showErrorDialog("Could not open hive at " + pathUserDat);
            return (false);
        }
        return (true);
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
        showInfo();
    }

    private boolean processHives() throws RegistryException {
        return (openHives() && getAssistEntries());
    }

    private boolean getAssistEntries() {
        try {
            KeyNode node = regUserDat.loadKeyFromPath("Software/Microsoft/Windows/CurrentVersion/Explorer/UserAssist");
            for (KeyNode guidNode : node.children()) {
                for (KeyNode countNode : guidNode.children()) {
                    if (countNode.name().equals("Count")) {
                        for (ValueNode vn : countNode.values()) {
                            assistEntries.add(new AssistRecord(vn.name(), guidNode.name(), vn));
                        }
                    }
                }
            }
        } catch (RegistryException e) {
            dialogHandler.frame().showErrorDialog(e.getMessage());
        }
        return (true);
    }

    private void deleteCurrentRow() {
        try {
            int row = form.getTable().getSelectedRow();
            if ((row >= 0) && (row < assistEntries.size())) {
                // get value node
                ValueNode vn = assistEntries.get(row).valueNode();
                if (vn != null) {
                    vn.delete(true);
                    assistEntries.remove(row);
                    model.removeRow(row);
                    form.getTable().addNotify();
                }
            }
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentRow();
                break;
        }
        showInfo();
    }

    @Override
    public void singleLeftClick(CellRef ref) {
        editCell(ref);
    }

    protected void editCell(final CellRef cellRef) {
        if ((cellRef.getCol() == 2) && (cellRef.getValue() instanceof WindowsDate)) {
            WindowsDate dt = (WindowsDate) cellRef.getValue();
            inputHandler.filetimeInput().setValue(dt.toDate());
            if (inputHandler.handleInput(inputHandler.filetimeInput(), "Edit Timestamp")) {
                dt.timestamp(inputHandler.filetimeInput().getValue());
                assistEntries.get(cellRef.getRow()).timestamp(dt);
            }
        }
        showInfo();
    }

    private void showInfo() {
        if (regUserDat.hiveChanged()) {
            form.btSave().setEnabled(true);
            form.title(title + " (not saved)");
        } else {
            form.btSave().setEnabled(false);
            form.title(title);
        }
    }

    public void showProperties(ActionEvent e) {
        int row = form.getTable().getSelectedRow();
        if (row >= 0) {
            propertiesHandler = new PropertiesHandler(this, assistEntries.get(row).valueNode().vkCell());
        }
    }
}
