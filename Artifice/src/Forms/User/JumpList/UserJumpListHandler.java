package Forms.User.JumpList;

import Dialogs.DialogHandler;
import Forms.AbstractHandler;
import Forms.CustomTableModel;
import Forms.ListTableForm;
import AppID.AppIDList;
import AppID.JumpListFile;
import Forms.AbstractKeyListener;
import Forms.TableData;
import Forms.TableRow;
import Paths.Paths;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jumplist.JumpList;

/**
 * Jump lists handler
 * Jump lists are records of recently opened documents associated with a pinned menu item
 * introduced in Windows 7
 * 
 * https://cyberforensicator.com/wp-content/uploads/2017/01/1-s2.0-S1742287616300202-main.2-14.pdf
 */

public class UserJumpListHandler extends AbstractHandler
        implements ListSelectionListener, AbstractKeyListener {

    private String pathJumpList;                    // path to jump list files
    private ListTableForm form;                     // split pane: list and table
    private CustomTableModel tableModel;            // TableModel (right-hand side of pane)
    private DefaultListModel listModel;             // ListModel (left-hand side of pane)
    private AppIDList appIDList;                    // AppID controller
    private ArrayList<JumpListFile> jumpListFiles;  // List of JumpListFile
    private final String title = "Jump Lists";
    private JumpList[] jumpLists;                   // List of JumpList

    public UserJumpListHandler(DialogHandler dialogHandler) {
        super(dialogHandler);
    }

    @Override
    public boolean changed() {
        return (false);
    }

    @Override
    public void save() {

    }

    @Override
    public boolean close() {
        return (true);
    }

    @Override
    public boolean show() {
        if (getUser()) {
            form = new ListTableForm(title, this);
            if (buildList()) {
                dialogHandler.frame().getDesktopPane().add(form);
                form.getTable().addKeyListener(this);
                form.setVisible(true);
            }
            return (true);
        }
        return (false);
    }

    /**
     * Gets the selected user account and sets pathToJumpList
     * @return 
     */
    @Override
    public boolean getUser() {
        if (super.getUser()) {
            // user account path: Windows\Users\$USER
            pathJumpList = root + Paths.Users + user;
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * Creates a list of jump list files
     * 
     * @return 
     */
    private boolean buildList() {
        // create and populate a list of JumpListFile objects
        appIDList = new AppIDList();
        jumpListFiles = appIDList.buildDisplayList(pathJumpList);
        
        // create and populate TableModel
        listModel = new DefaultListModel();
        if ((jumpListFiles != null) && (jumpListFiles.size() > 0)) {
            jumpListFiles.forEach((jumpListFile) -> {
                listModel.addElement(jumpListFile);
            });
            jumpLists = new JumpList[listModel.size()];            
            form.getList().setModel(listModel);
            form.getList().getSelectionModel().addListSelectionListener(this);
            form.getList().setSelectedIndex(0);
            return (true);
        } else {
            frame().showErrorDialog("No jumplist files or access denied");
            return (false);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = form.getList().getSelectedIndex();
        if (row >= 0) {
            buildTable((JumpListFile) form.getList().getSelectedValue(), row);
        }
    }

    private void buildTable(JumpListFile jlf, int index) {
        frame().showInfo("Loading detail for " + jlf.toString());
        String[] columns = {"Entry", "Timestamp", "NetBIOS", "Path"};
        try {
            if (jumpLists[index] == null) {
                jumpLists[index] = new JumpList(jlf.getFilename());
            }
            tableModel = new CustomTableModel(loadJumpFileTable(index), columns);
            form.getTable().setModel(tableModel);
            frame().showInfo("Loaded detail for " + jlf.toString());
        } catch (IOException e) {
            frame().showErrorDialog(e.getMessage());
        }
    }

    private TableData loadJumpFileTable(int index) {
        TableData data = new TableData();
        jumpLists[index].getEntries().stream().map((entry) -> {
            TableRow row = new TableRow();
            row.add(entry.getEntryNum());
            row.add(entry.getModDate());
            row.add(entry.getNetBios());
            row.add(entry.getPath());
            return row;
        }).forEachOrdered((row) -> {
            data.add(row);
        });
        return (data);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentRow();
        }
    }

    private void deleteCurrentRow() {
        int row = form.getTable().getSelectedRow();
        if (row >= 0) {
            try {
                jumpLists[row].deleteEntry((String) tableModel.getValueAt(row, 0));
                tableModel.removeRow(row);
                form.getTable().addNotify();
            } catch (IOException e) {
                frame().showErrorDialog(e.getMessage());
            }
        }
    }

}
