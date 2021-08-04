package Forms.User.ShellBags;

import Dialogs.DialogHandler;
import Forms.AbstractHandler;
import Forms.AbstractKeyListener;
import Forms.CellRef;
import Forms.CustomTableModel;
import Forms.TableData;
import Forms.TableMouseListener;
import Forms.TableRow;
import Forms.User.ShellBags.Extensions.ExBEEF0004;
import Forms.User.ShellBags.Extensions.ExtensionBlock.ExtensionType;
import InputHelpers.InputHandler;
import Paths.Paths;
import Properties.MessageHandler;
import Properties.PropertiesHandler;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;
import registry.RegistryHive;
import utils.WindowsDate;

/**
 *
 * @author 
 */
public class UserShellBagsHandler extends AbstractHandler
        implements TreeSelectionListener, TableMouseListener, AbstractKeyListener, MessageHandler {

    private RegistryHive regUsrClass;
    private String pathUsrClass;
    private ShellBagsForm form;
    private MruNode rootMruNode;
    private MruTreeNode rootTreeNode;
    private InputHandler inputHandler;
    private String title = "Shell Bags";
    private HashMap<String, KeyNode> bags = new HashMap();

    public UserShellBagsHandler(DialogHandler dialogHandler) {
        super(dialogHandler);
        inputHandler = new InputHandler(dialogHandler.frame());
    }

    @Override
    public boolean show() throws RegistryException {
        if (getRoot() && getUser()) {
            dialogHandler.frame().showInfo("Opening hives...");
            if (processHives()) {
                dialogHandler.frame().showInfo("Successfully opened hive UsrClass.dat");
                form = new ShellBagsForm(title, this);
                dialogHandler.frame().getDesktopPane().add(form);
                form.setVisible(true);
                form.splitPane().setDividerLocation(0.7);
                form.getTree().addTreeSelectionListener(this);
                form.getTree().addKeyListener(this);
                form.getTable().addMouseListener(this);
                if (buildMruTree()) {
                    form.getTree().setModel(new DefaultTreeModel(rootTreeNode));
                }
                return (true);
            }
        }
        return (false);
    }

    private boolean processHives() throws RegistryException {
        dialogHandler.frame().showInfo("Opening UsrClass.dat");
        regUsrClass = new RegistryHive(pathUsrClass);
        if (!regUsrClass.open()) {
            dialogHandler.frame().showErrorDialog("Could not open hive at " + pathUsrClass);
            return (false);
        }
        return (true);
    }

    private boolean buildMruTree() {
        try {
            buildBagMap();
            // load all keys under Local Settings/Software/Microsoft/Windows/Shell/BagMRU into a tree structure
            KeyNode kn = regUsrClass.loadKeyFromPath("Local Settings/Software/Microsoft/Windows/Shell/BagMRU");
            rootMruNode = new MruNode(kn, null, "Root", null);
            rootMruNode.load();     // recursive load - all children will also be loaded
            rootTreeNode = new MruTreeNode(rootMruNode);
            buildNodeTree(rootTreeNode);
            // map MRU nodes to shell bags
            rootMruNode.traverse(this::mapMRUToBag);
            return (true);
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
            return (false);
        }
    }

    public void mapMRUToBag(MruNode mruNode) {
        // see if there is an associated folder node
        if (mruNode.nodeSlot() != -1) {
            // look for entry in bags map
            KeyNode bagNode = bags.get("" + mruNode.nodeSlot());
            if (bagNode != null) {
                mruNode.bagNode(bagNode);
            }
        }
    }

    private boolean buildBagMap() {
        // load all keys under Local Settings/Software/Microsoft/Windows/Shell/Bags into a map structure  
        try {
            KeyNode kn = regUsrClass.loadKeyFromPath("Local Settings/Software/Microsoft/Windows/Shell/Bags");
            for (KeyNode bagNode : kn.children()) {
                if (isInt(bagNode.name())) {
                    bags.put(bagNode.name(), bagNode);
                }
            }
            return (true);
        } catch (RegistryException e) {
            frame().showErrorDialog(e.getMessage());
            return (false);
        }
    }

    public static boolean isInt(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!((name.charAt(i) >= '0') && (name.charAt(i) <= '9'))) {
                return (false);
            }
        }
        return (true);
    }

    private void buildNodeTree(MruTreeNode treeNode) {
        for (MruNode subNode : treeNode.getUserObject().children()) {
            MruTreeNode newNode = new MruTreeNode(subNode);
            treeNode.add(newNode);
            buildNodeTree(newNode);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (form.getTree().getLastSelectedPathComponent() instanceof MruTreeNode) {
            MruTreeNode node = (MruTreeNode) form.getTree().getLastSelectedPathComponent();
            buildTable(node.getUserObject());
            if (node.getUserObject().path() != null) {
                dialogHandler.frame().showInfo(node.getUserObject().path());
            }
        }
    }

    public enum TimeField {

        TF_MRUKEY, TF_BAGKEY, TF_MODIFIED, TF_CREATED, TF_ACCESSED
    };
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private class Timestamp {

        private Date date;
        private final TimeField timeField;
        private final MruNode node;

        public Timestamp(WindowsDate dt, TimeField timeField, MruNode node) {
            date = dt.toDate();
            this.timeField = timeField;
            this.node = node;
        }

        public Timestamp(FATDate dt, TimeField timeField, MruNode node) {
            date = dt.toDate();
            this.timeField = timeField;
            this.node = node;
        }

        @Override
        public String toString() {
            return (sdf.format(date));
        }

        public TimeField timeField() {
            return (timeField);
        }

        public Date date() {
            return (date);
        }

        public void date(Date date) {
            this.date = date;
        }

        public MruNode node() {
            return (node);
        }

        public FATDate toFATDate() {
            return (new FATDate(date));
        }

        public void FATDate(FATDate dt) {
            date = dt.toDate();
            node.fileEntryShell().modifiedDT(dt);
            node.update();
        }
    }

    private void buildTable(MruNode node) {
        String[] names = {"Name", "Value"};
        TableData data = new TableData();
        TableRow row = new TableRow();
        if (node.mruNode() != null) {
            row.add("MRU Key Timestamp");
            row.add(new Timestamp(new WindowsDate(node.mruNode().timestamp()), TimeField.TF_MRUKEY, node));
            data.add(row);
        }
        // see if there is an associated folder node
        if (node.bagNode() != null) {
            row = new TableRow();
            row.add("Bag Key Timestamp");
            row.add(new Timestamp(new WindowsDate(node.bagNode().timestamp()), TimeField.TF_BAGKEY, node));
            data.add(row);
        }
        if (node.fileEntryShell() != null) {
            row = new TableRow();
            row.add("File Entry Modified");
            row.add(new Timestamp(node.fileEntryShell().modifiedDT(), TimeField.TF_MODIFIED, node));
            data.add(row);
            if ((node.fileEntryShell().extensionBlock() != null)
                    && (node.fileEntryShell().extensionBlock().type() == ExtensionType.E_BEEF0004)) {
                row = new TableRow();
                row.add("File Entry Created");
                row.add(new Timestamp(((ExBEEF0004) node.fileEntryShell().extensionBlock()).createDT(), TimeField.TF_CREATED, node));
                data.add(row);
                row = new TableRow();
                row.add("File Entry Accessed");
                row.add(new Timestamp(((ExBEEF0004) node.fileEntryShell().extensionBlock()).accessDT(), TimeField.TF_ACCESSED, node));
                data.add(row);
            }
        }
        form.getTable().setModel(new CustomTableModel(data, names));
    }

    @Override
    public boolean getUser() {
        if (super.getUser()) {
            pathUsrClass = root + Paths.Users + user + "/" + Paths.UsrClass + "/UsrClass.dat";
            return (true);
        } else {
            return (false);
        }
    }

    @Override
    public boolean close() {
        if (regUsrClass != null) {
            regUsrClass.close();
            regUsrClass = null;
        }
        return (true);
    }

    @Override
    public boolean changed() {
        return ((regUsrClass != null) && (regUsrClass.hiveChanged()));
    }

    @Override
    public void save() {
        regUsrClass.save();
        showInfo();
    }

    @Override
    public void singleLeftClick(CellRef ref) {
        if (ref.getCol() == 1) {
            editCell(ref);
        }
    }

    private void editCell(CellRef ref) {
        if (ref.getValue() instanceof Timestamp) {
            Timestamp ts = (Timestamp) ref.getValue();
            inputHandler.filetimeInput().setValue(ts.date());
            if (inputHandler.handleInput(inputHandler.filetimeInput(), "Edit Timestamp")) {
                ts.date(inputHandler.filetimeInput().getValue());
                updateTimestamp(ts);
            }
        }
        showInfo();
    }

    private void updateTimestamp(Timestamp ts) {
        switch (ts.timeField()) {
            case TF_MRUKEY:
                WindowsDate wdt = new WindowsDate(ts.date());
                ts.node().mruNode().timestamp(wdt);
                break;
            case TF_BAGKEY:
                WindowsDate bdt = new WindowsDate(ts.date());
                ts.node().bagNode().timestamp(bdt);
                break;
            case TF_MODIFIED:
                FATDate mdt = ts.toFATDate();
                ts.node().fileEntryShell().modifiedDT(mdt);
                ts.node().update();
                break;
            case TF_CREATED:
                FATDate cdt = ts.toFATDate();
                ((ExBEEF0004) ts.node().fileEntryShell().extensionBlock()).createDT(cdt);
                ts.node().update();
                break;
            case TF_ACCESSED:
                FATDate adt = ts.toFATDate();
                ((ExBEEF0004) ts.node().fileEntryShell().extensionBlock()).accessDT(adt);
                ts.node().update();
                break;
        }
    }

    public void showInfo() {
        if (regUsrClass.hiveChanged()) {
            form.btSave().setEnabled(true);
            form.title(title + " (not saved)");
        } else {
            form.btSave().setEnabled(false);
            form.title(title);
        }
    }

    private MruTreeNode getSelectedNode() {
        if (form.getTree().getLastSelectedPathComponent() != null) {
            return ((MruTreeNode) form.getTree().getLastSelectedPathComponent());
        } else {
            return (null);
        }
    }

    private void deleteCurrentNode() {
        try {
            if (getSelectedNode() != null) {
                MruNode mruNode = getSelectedNode().getUserObject();
                if (mruNode != null) {
                    mruNode.delete();
                }
                MruTreeNode subTree = getSelectedNode();
                if (subTree.getParent() != null) {
                    int selected = (form.getTree().getSelectionRows() == null) ? 0 : form.getTree().getSelectionRows()[0];
                    ((DefaultTreeModel) form.getTree().getModel()).removeNodeFromParent(subTree);
                    if (selected < form.getTree().getRowCount()) {
                        form.getTree().setSelectionRow(selected);
                    } else {
                        form.getTree().setSelectionRow(selected - 1);
                    }
                }
            }
        } catch (RegistryException e) {
            dialogHandler.frame().showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentNode();
                break;
        }
        showInfo();
    }

    public void showProperties(ActionEvent e) {
        MruTreeNode node = getSelectedNode();
        if (node != null) {
            PropertiesHandler ph = new PropertiesHandler(this, node.getUserObject().mruNode().nkCell());
        }
    }

    public void binaryEdit(ActionEvent evt) {
        // binary data for this entry will be one of the value fields of the parent
        try {
            MruTreeNode mruTreeNode = getSelectedNode();
            if (mruTreeNode != null) {
                MruNode mruNode = mruTreeNode.getUserObject().parent();
                if (mruNode != null) {
                    // find the value node
                    // will have the same name as the key name of this node
                    ValueNode vn = mruNode.value(mruTreeNode.getUserObject().name());
                    if (vn != null) {
                        inputHandler.binaryInput().setValue(vn.getData());
                        if (inputHandler.handleInput(inputHandler.binaryInput(), "Edit Binary Data")) {
                            vn.setData(inputHandler.binaryInput().getValue());
                            showInfo();
                        }
                    }
                }
            }
        } catch (RegistryException e) {
            dialogHandler.frame().showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void showInfo(String message) {
        dialogHandler.frame().showInfo(message);
    }

    @Override
    public void updateCurrentCell() {
        MruTreeNode node = getSelectedNode();
        if (node != null) {
            node.getUserObject().mruNode().updateCell();
            showInfo();
        }
    }
}
