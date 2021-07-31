package registry.Nodes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.function.Consumer;
import registry.Cell.*;
import registry.RegistryBuffer;
import registry.RegistryException;
import registry.RegistryHive;
import registry.element.DataElementCollection;
import utils.WindowsDate;

/**
 *
 * Wraps a key (NK) cell
 * 
 * Uses lazy evaluation - child nodes and values will only be loaded if queried
 */
public class KeyNode {

    private KeyNode parent;             // parent of this node
    private KeyList children;           // list of child nodes
    private NkCell nkCell = null;       // this cell (NK)
    private ValueList values;           // list of values
    private final RegistryHive hive;    // instance of registry hive
    private String path;                // path to this node

    /**
     * Constructor
     * @param nkCell NK cell
     * @param hive  instance of RegistryHive
     */
    public KeyNode(NkCell nkCell, RegistryHive hive) {
        this.nkCell = nkCell;
        this.hive = hive;
        parent = null;
        children = null;
        path = "";
    }

    /**
     * Constructor
     * Creates KeyNode from specified path into supplied registry hive
     * @param path
     * @param hive
     * @throws RegistryException 
     */
    public KeyNode(String path, RegistryHive hive) throws RegistryException {
        this.path = path;
        this.hive = hive;
        if (!walkPath()) {
            throw new RegistryException("Path not found (" + path + ")");
        }
    }

    /**
     * Finds and returns this node's parent
     * @return
     * @throws RegistryException 
     */
    public KeyNode parent() throws RegistryException {
        if (parent == null) {
            // ensure that this is not the root key
            if (nkCell != hive.getRootNode().nkCell()) {
                parent = new KeyNode(nkCell.getParent(hive.getRegistryBuffer()), hive);
            }
        }
        return (parent);
    }

    /**
     * Sets this node's parent
     * @param parent 
     */
    public void parent(KeyNode parent) {
        this.parent = parent;
    }

    /**
     * Finds and returns a list of this node's children
     * @return
     * @throws RegistryException 
     */
    public KeyList children() throws RegistryException {
        if (children == null) {
            if (nkCell.getSubkeyCount() > 0) {
                getChildren();
            } else {
                return(new KeyList());
            }
        }
        return (children);
    }
    
    /**
     * Return a child node by name
     * @param name
     * @return
     * @throws RegistryException 
     */
    public KeyNode child(String name) throws RegistryException {
        for (KeyNode node: children()) {
            if (node.name().equals(name)) {
                return(node);
            }
        }
        return(null);
    }

    /**
     * Reloads child list
     * @throws RegistryException 
     */
    public void reload() throws RegistryException {
        getChildren();
    }

    /**
     * Sets this node's children
     * @param children 
     */
    public void children(KeyList children) {
        this.children = children;
    }

    /**
     * Builds list of child nodes for this key
     * @return
     * @throws RegistryException 
     */
    private KeyList getChildren() throws RegistryException {
        children = new KeyList();
        for (NkCell cell : nkCell.getChildren(hive.getRegistryBuffer())) {
            if (cell != null) {
                KeyNode kn = new KeyNode(cell, hive);
                kn.path(path + "/" + kn.name());
                kn.parent(this);
                children.add(kn);
            }
        }
        return (children);
    }
    
    /**
     * Traverses a given path and sets this node's nKey to target
     * @return
     * @throws RegistryException 
     */
    private boolean walkPath() throws RegistryException {
        // split path string
        String thisPath = path;
        NkCell cell = hive.getRootNode().nkCell();
        int i = 0;
        if (thisPath.length() > 0) {
            if (thisPath.charAt(0) == '/') {
                thisPath = thisPath.substring(1);
            }
            String[] keyNames = thisPath.split("/", i);
            for (String keyName : keyNames) {
                if ((cell = findSubCell(cell, keyName)) == null) {
                    return (false);
                }
            }
        }
        nkCell = cell;
        return (true);
    }
    
    /**
     * Searches for child node of specified name
     * @param cell
     * @param keyName
     * @return
     * @throws RegistryException 
     */
    private NkCell findSubCell(NkCell cell, String keyName) throws RegistryException {
        for (NkCell subCell : cell.getChildren(hive.getRegistryBuffer())) {
            if (subCell.getKeyName().equals(keyName)) {
                return (subCell);
            }
        }
        return (null);
    }

    /**
     * Gets this node's name
     * @return 
     */
    public String name() {
        return (nkCell.getKeyName());
    }
    
    /**
     * Sets a new name for this cell
     * @param newName
     * @throws RegistryException 
     */
    public void name(String newName) throws RegistryException {
        // check if new name is larger than the existing
        if (newName.length() > name().length()) {
            // check if there is enough space in the cell for the new name
            int needBytes = newName.length() - name().length();
            if (nkCell.getSlack() < needBytes) {
                // not enough space for the new name
                // look for an empty cell of the correct size
                // 0x50 is the offset of the key name in the nk cell
                int offset = hive.findEmptyCell(0x50 + newName.length() + 1);
                if (offset >= 0) {
                    // create new NK cell
                    NkCell newCell = (NkCell) RegistryCell.newRegistryCell(offset, RegistryCellType.T_NK, getRegistryBuffer());
                    // copy elements from old cell
                    newCell.elements(nkCell.elements());
                    // wipe the old cell
                    nkCell.wipe(getRegistryBuffer());
                    // update parent nkCell with new value location
                    if (parent() != null) {
                        parent().updateChildOffset(nkCell.getOffset(), offset);
                    }
                    // repoint
                    nkCell = newCell;
                } else {
                    throw new RegistryException("Could not allocate space for new cell");
                }
            }
        }
        nkCell.setKeyName(newName);    
        nkCell.update(getRegistryBuffer());
        hive.hiveChanged(true);
    }    
    
    /**
     * Update child offset
     * @param oldOffset
     * @param newOffset 
     */
    public void updateChildOffset(int oldOffset, int newOffset) {
        if (nkCell.getIndexCell() != null) {
            nkCell.getIndexCell().replaceOffset(oldOffset, newOffset);
            nkCell.getIndexCell().update(getRegistryBuffer());
        }
    }

    /**
     * Create list of values for this cell
     * @throws RegistryException 
     */
    private void getValues() throws RegistryException {
        values = new ValueList();
        for (VkCell cell : nkCell.getValues(hive.getRegistryBuffer())) {
            values.add(new ValueNode(cell, this));
        }
    }

    /**
     * Returns list of values for this cell
     * Will create value list if it doesn't already exist
     * @return
     * @throws RegistryException 
     */
    public ValueList values() throws RegistryException {
        if (values == null) {
            getValues();
        }
        return (values);
    }
    
    /**
     * Find value by name
     * @param name
     * @return
     * @throws RegistryException 
     */
    public ValueNode value(String name) throws RegistryException {
        for (ValueNode vn: values()) {
            if (name.equals(vn.name())) {
                return(vn);
            }
        }
        return(null);
    }

    /**
     * Getter for nkCell
     * @return 
     */
    public NkCell nkCell() {
        return (nkCell);
    }

    /**
     * returns underlying registry buffer
     * @return 
     */
    public RegistryBuffer getRegistryBuffer() {
        return (hive.getRegistryBuffer());
    }

    /**
     * Check if cell has children
     * @return 
     */
    public boolean hasChildren() {
        return (nkCell.getSubkeyCount() > 0);
    }

    /**
     * get count of child nodes
     * @return 
     */
    public int numChildren() {
        return (nkCell.getSubkeyCount());
    }

    /**
     * Check if node has values
     * @return 
     */
    public boolean hasValues() {
        return (nkCell.getValueCount() > 0);
    }

    /**
     * Get count of values
     * @return 
     */
    public int numValues() {
        return (nkCell.getValueCount());
    }

    /**
     * Getter for underlying registry hive
     * @return 
     */
    public RegistryHive hive() {
        return (hive);
    }

    /**
     * Getter for path
     * @return 
     */
    public String path() {
        return (path);
    }

    /**
     * Setter for path
     * @param path 
     */
    private void path(String path) {
        this.path = path;
    }

    /**
     * Getter for elements
     * @return 
     */
    public DataElementCollection elements() {
        return (nkCell.elements());
    }

    /**
     * Reload node data
     */
    public void updateCell() {
        nkCell.update(hive.getRegistryBuffer());
        hive.hiveChanged(true);
    }

    /**
     * get string representation of cell timestamp
     * @return 
     */
    public String timestampS() {
        return (nkCell.getTimestampS());
    }
    
    /**
     * Get cell timestamp as epoch
     * @return 
     */
    public long timestamp() {
        return(nkCell.getTimestamp());
    }
    
    /**
     * Sets cell timestamp
     * @param dt 
     */
    public void timestamp(Date dt) {
        timestamp(new WindowsDate(dt));
    }
        
    /**
     * Updates cell timestamp
     * @param dt 
     */
    public void timestamp(WindowsDate dt) {
        nkCell.setTimestamp(dt);
        nkCell.update(hive.getRegistryBuffer());
        hive.hiveChanged(true);
    }

    @Override
    public String toString() {
        return (name());
    }

    /**
     * Deletes this cell
     * @param wipe if set, deleted cell will be wiped
     * @throws RegistryException 
     */
    public void delete(boolean wipe) throws RegistryException {
        //can't delete root cell
        if (nkCell != hive.getRootNode().nkCell()) {
            // delete the cell's children
            deleteCellSubkeys(wipe);
            //update parent
            RegistryCell cellparent = RegistryCell.makeRegistryCell(nkCell.getParentOffset(), hive.getRegistryBuffer());
            if ((cellparent != null) && (cellparent instanceof NkCell)) {
                ((NkCell) cellparent).notifyChildDeleted(nkCell.getOffset(), hive.getRegistryBuffer());
            }
            children = null;
            hive.hiveChanged(true);
        }
    }

    /**
     * Delete specified cell
     * @param cell
     * @param wipe 
     */
    protected void deleteSpecifiedCell(RegistryCell cell, boolean wipe) {
        if (wipe) {
            RegistryCell newCell = cell.wipe(hive.getRegistryBuffer());
        } else {
            cell.delete(hive.getRegistryBuffer());
        }
        hive.hiveChanged(true);
    }

    /**
     * Delete cell child keys
     * @param wipe 
     */
    protected void deleteCellSubkeys(final boolean wipe) {

        ArrayList<RegistryCell> deleteList = new ArrayList<>();
        
        // traverse cell and its children
        // create a list of all cells that need to be deleted
        traverse(cell -> {
            deleteList.add(cell);
        });
        
        // delete all cells in list
        deleteList.stream().forEach((c) -> {
            deleteSpecifiedCell(c, wipe);
        });
    }

    /**
     * Traverses all child nodes and performs specified function on each
     * This is an iterative instead of recursive function to prevent stack overflows
     * @param f 
     */
    public void traverse(Consumer<RegistryCell> f) {
        // use stack to keep state
        Stack<RegistryCell[]> cellStack = new Stack<>();
        RegistryCell[] currArray, nextArray;
        Stack<Integer> intStack = new Stack<>();
        int index = 0;

        currArray = new RegistryCell[1];
        currArray[0] = nkCell;

        while (currArray != null) {
            if (index < currArray.length) {
                RegistryCell cell = currArray[index];
                if (cell != null) {
                    nextArray = cell.getChildCells(hive.getRegistryBuffer());
                    if (nextArray.length == 0) {
                        index++;
                    } else {
                        cellStack.push(currArray);
                        currArray = nextArray;
                        intStack.push(index);
                        index = 0;
                    }
                    f.accept(cell);     // execute supplied function on this cell
                } else {
                    index++;
                }
            } else {
                if (cellStack.empty()) {
                    currArray = null;
                } else {
                    currArray = cellStack.pop();
                    index = intStack.pop() + 1;
                }
            }
        }
    }

    /**
     * Matches function
     * Used by RegistrySearcher to find matching keys
     * @param text
     * @param ignoreCase
     * @return 
     */
    public boolean matches(String text, boolean ignoreCase) {
        if (ignoreCase) {
            return (name().toLowerCase().contains(text));
        } else {
            return (name().contains(text));
        }
    }

    /**
     * Return top node for this tree
     * Top node is the node directly below the root
     * @return
     * @throws RegistryException 
     */
    public KeyNode topNode() throws RegistryException {
        return (backtrack(this));
    }

    /**
     * Search tree backwards for top node
     * @param node
     * @return
     * @throws RegistryException 
     */
    private KeyNode backtrack(KeyNode node) throws RegistryException {
        if (isRoot(node.parent())) {
            return (node);
        } else {
            return (backtrack(node.parent()));
        }
    }

    /**
     * Check if specified node is the hive root
     * @param node
     * @return
     * @throws RegistryException 
     */
    private boolean isRoot(KeyNode node) throws RegistryException {
        return (node.parent().nkCell() == hive.getRootNode().nkCell());
    }

    /**
     * Return an array of strings representing key names from this node to root
     * @return
     * @throws RegistryException 
     */
    public String[] keysToRoot() throws RegistryException {
        ArrayList<String> nameList = new ArrayList<>();
        KeyNode walker = this;
        while (walker != hive.getRootNode()) {
            nameList.add(walker.name());
            walker = walker.parent();
        }
        String[] nameArray = new String[nameList.size()];
        int j = 0;
        for (int i = nameList.size() - 1; i >= 0; i--) {
            nameArray[j++] = nameList.get(i);
        }
        return (nameArray);
    }
    
    /**
     * Deletes a specified value from this cell
     * @param name
     * @param wipe
     * @throws RegistryException 
     */
    public void deleteValue(String name, boolean wipe) throws RegistryException {
        for (ValueNode vn: values()) {
            if (vn.name().equals(name)) {
                vn.delete(wipe);
            }
        }
    }
    
    /**
     * Update offset of value
     * @param oldOffset
     * @param newOffset
     * @param buffer 
     */
    public void updateValueOffset(int oldOffset,int newOffset, RegistryBuffer buffer) {
        ValueListCell v = nkCell.valueListCell(buffer);
        if (v != null) {
            v.replaceOffset(oldOffset, newOffset);
            v.update(buffer);
        }
    }
    
    /**
     * Reload values
     * @throws RegistryException 
     */
    public void reloadValues() throws RegistryException {
        getValues();
    }
        
}
