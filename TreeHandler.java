package TreeHandler;

import Properties.MessageHandler;
import Forms.FormController;
import Forms.MainFrame;
import Properties.PropertiesHandler;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import registry.Nodes.KeyNode;
import registry.RegistryException;
import registry.RegistryHive;
import registry.RegistrySearcher;

/**
 * Handler for the registry key tree (left-hand side of pane)
 *
 */
public class TreeHandler extends MouseAdapter
        implements TreeSelectionListener, TreeWillExpandListener, KeyListener, MessageHandler {

    private RegistryHive hive;                  // currently selected hive
    private final MainFrame frame;              // parent frame
    private final FormController controller;    // parent form controller    
    private CustomTreeNode rootNode;            // custom TreeNode
    private TreePopupMenu treePopupMenu;        // popup context menu
    private RegistrySearcher searcher;          // registry searcher object
    private String searchText;                  // search text
    private boolean optIgnoreCase = true;       // case-sensitive toggle for key search

    /**
     * Constructor
     *
     * @param frame
     * @param hive
     * @param controller
     */
    public TreeHandler(MainFrame frame, RegistryHive hive, FormController controller) {
        this.hive = hive;
        this.frame = frame;
        this.controller = controller;
    }

    /**
     * Getter for enclosed JTree
     *
     * @return
     */
    private JTree tree() {
        return (frame.getTree());
    }

    /**
     * Getter for parent frame
     *
     * @return
     */
    @Override
    public MainFrame frame() {
        return (frame);
    }

    /**
     * Setter for hive
     *
     * @param hive
     */
    public void hive(RegistryHive hive) {
        this.hive = hive;
    }

    /**
     * Display a message on parent frame
     *
     * @param message
     */
    @Override
    public void showInfo(String message) {
        frame().showInfo(message);
    }

    /**
     * Initializes the JTree with a supplied TreeModel
     *
     * @param model
     */
    private void newTree(CustomTreeModel model) {
        tree().setModel(model);
        tree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree().addTreeSelectionListener(this);
        tree().addTreeWillExpandListener(this);
        tree().addKeyListener(this);
        if (treePopupMenu == null) {
            treePopupMenu = new TreePopupMenu(this);
            tree().addMouseListener(this);
        }
    }

    /**
     * Populate the registry key tree
     *
     * @return
     */
    public boolean populateTree() {
        try {
            // create a new KeyNode from the hive root node
            KeyNode root = new KeyNode("", hive);
            // create a TreeNode object from root node
            rootNode = new CustomTreeNode(root);
            // load the root node's children
            loadNodeChildren(rootNode);
            // create a custom TreeModel from the root node
            CustomTreeModel model = new CustomTreeModel(rootNode);
            // set the current tree model to root model
            newTree(model);
            return (true);
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
            return (false);
        }
    }

    /**
     * Get selected node in tree
     *
     * @return
     */
    protected CustomTreeNode getSelectedNode() {
        return ((CustomTreeNode) tree().getLastSelectedPathComponent());
    }

    /**
     * Load a node's children We don't load the entire tree at once - this
     * method is only called when a node is selected
     *
     * @param treeNode
     * @return
     */
    private boolean loadNodeChildren(CustomTreeNode treeNode) {
        try {
            // get the key node from the tree
            KeyNode keyNode = treeNode.getUserObject();
            // load children if not already loaded
            if (!treeNode.childrenLoaded()) {
                if (keyNode.hasChildren()) {
                    // iterate over child keys and add to tree underneath selected node
                    for (KeyNode node : keyNode.children()) {
                        treeNode.add(new CustomTreeNode(node));
                    }
                }
                // set childrenLoaded flag to true
                treeNode.childrenLoaded(true);
                return (true);
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
        }
        return (false);
    }

    /**
     * Handle valueChanged event This method is called when a new tree branch is
     * selected
     *
     * @param e
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (getSelectedNode() != null) {
            // load this node's children
            loadNodeChildren(getSelectedNode());
            // display registry path
            frame.showInfo(getSelectedNode().getUserObject().path() + " (" + getSelectedNode().getUserObject().timestampS() + ")");
            // populate the right-hand pane with key values (if any)
            controller.regTableHandler().populateTable(getSelectedNode().getUserObject());
        }
    }

    /**
     * Handle treeWillExpand event This method is called when the (+) is clicked
     * next to a tree node
     *
     * @param e
     */
    @Override
    public void treeWillExpand(TreeExpansionEvent e) {
        CustomTreeNode node = (CustomTreeNode) e.getPath().getLastPathComponent();
        loadNodeChildren(node);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent e) {

    }

    /**
     * Display the selected nodes attributes for editing
     *
     */
    public void showProperties() {
        if (getSelectedNode() != null) {
            PropertiesHandler ph = new PropertiesHandler(this, getSelectedNode().getUserObject().nkCell());
        }
    }

    @Override
    public void updateCurrentCell() {
        getSelectedNode().getUserObject().updateCell();
        controller.showTitle();
    }
    
    /**
     * Display the delete key dialog
     */
    public void handleDeleteDialog() {
        // If the 'don't show again' button was previously selected,
        // skip the delete dialog
        if (!controller.showDeleteDialog()) {
            return;     // if canel button is clicked, return from here
        }
        try {
            // delete the selected node
            getSelectedNode().getUserObject().delete(controller.getDeleteOptions().optWipe);
            // delete the selected node from the tree
            CustomTreeNode subTree = getSelectedNode();
            if (subTree.getParent() != null) {
                // load the parent node's children
                loadNodeChildren(subTree.getParent());
                // get the selected row
                int selected = (tree().getSelectionRows() == null) ? 0 : tree().getSelectionRows()[0];
                // remove selecetd row from TreeModel
                ((CustomTreeModel) tree().getModel()).removeNodeFromParent(subTree);
                // move the selection to the previous row
                if (selected < tree().getRowCount()) {
                    tree().setSelectionRow(selected);
                } else {
                    tree().setSelectionRow(selected - 1);
                }
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
        }
        controller.showTitle();
    }

    /**
     * Show the 'find' dialog panel
     *
     * @return
     */
    protected boolean showFindDialog() {
        FindDialogPanel panel = new FindDialogPanel(searchText, optIgnoreCase);
        int ret = JOptionPane.showOptionDialog(frame(), panel,
                "Enter Search Text", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (ret == JOptionPane.OK_OPTION) {
            searchText = panel.getText();
            optIgnoreCase = panel.getIgnoreCase();
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * Handle the search dialog panel
     *
     */
    public void handleFindDialog() {
        if (showFindDialog()) {
            findFirst();
        }
    }

    /**
     * Handle the mouseClicked event
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        // check for right-click and display the context menu
        // left-click is handled by the JTree object
        if (SwingUtilities.isRightMouseButton(e)) {

            int row = tree().getClosestRowForLocation(e.getX(), e.getY());
            tree().setSelectionRow(row);
            if (getSelectedNode().getUserObject() instanceof KeyNode) {
                treePopupMenu.show(tree(), e.getX(), e.getY(), getSelectedNode().getUserObject());
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    /**
     * Handle the key presses
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            // delete key
            case KeyEvent.VK_DELETE:
                handleDeleteDialog();
                break;
            case KeyEvent.VK_F3:
                findNext();
                break;
        }
    }

    /**
     * Load a KeyNode
     *
     * @param node
     * @return
     * @throws RegistryException
     */
    private TreePath loadNode(KeyNode node) throws RegistryException {
        //start at root, walk down tree loading cells along the way as needed
        CustomTreeNode treeNode = rootNode;
        for (String name : node.keysToRoot()) {
            CustomTreeNode childNode = findNodeChild(treeNode, name);
            if (childNode != null) {
                treeNode = childNode;
            } else {
                frame.showErrorDialog("Could not walk nodes in TreeHandler.loadNode");
                return (null);
            }
        }
        return (new TreePath(treeNode.getPath()));
    }

    private CustomTreeNode findNodeChild(CustomTreeNode treeNode, String name) throws RegistryException {
        loadNodeChildren(treeNode);
        for (CustomTreeNode node : treeNode) {
            if (node.getUserObject().name().equals(name)) {
                return (node);
            }
        }
        return (null);
    }

    /**
     * Finds first search result
     *
     */
    private void findFirst() {
        try {
            // create a hive searcher object
            searcher = hive.newSearch();
            // get first match
            KeyNode node = searcher.findFirst(searchText, optIgnoreCase);
            if (node != null) {
                positionTree(node);
            } else {
                searcher = null;
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
            searcher = null;
        }
    }

    private void positionTree(KeyNode node) {
        try {
            // sets selected node to first found
            TreePath path = loadNode(node);
            if (path != null) {
                tree().setSelectionPath(path);
                tree().scrollPathToVisible(path);
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
            searcher = null;
        }
    }

    private void findNext() {
        try {
            if (searcher != null) {
                KeyNode node = searcher.findNext();
                if (node != null) {
                    positionTree(node);
                } else {
                    searcher = null;
                }
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
            searcher = null;
        }
    }
}
