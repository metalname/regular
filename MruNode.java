/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags;

import java.util.Stack;
import java.util.function.Consumer;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;
import BinBuffer.BinBuffer;
import Misc.StringEx;
import registry.value.DwordValue;
import utils.NumFormat;

/**
 *
 * @author 
 */
public class MruNode {

    private String name;
    private String key;
    private MruList children;
    private final KeyNode keyNode;
    private final ValueNode valueNode;
    private ClassType classType;
    private int clsid;
    private BinBuffer buffer;
    private FileEntryShell fileEntryShell;
    private int nodeSlot = -1;
    private KeyNode bagNode;
    private final MruNode parentMruNode;

    public MruNode(KeyNode keyNode, ValueNode parentValueNode, String key, MruNode parentMruNode) {
        this.keyNode = keyNode;
        this.valueNode = parentValueNode;
        this.key = key;
        this.parentMruNode = parentMruNode;
        unpack();
    }

    public void load() throws RegistryException {
        // each mru consists of one or more values with integer names
        // these are the children of this node
        children = new MruList(this);
        if (keyNode != null) {
            for (ValueNode vn : keyNode.values()) {
                if (UserShellBagsHandler.isInt(vn.name())) {
                    // there will be one subkey for each of these values
                    // get the key by its integer name
                    KeyNode subNode = keyNode.child(vn.name());
                    // add to child list
                    if (subNode != null) {
                        children.add(new MruNode(subNode, vn, subNode.name(), this));
                    } else {
                        // we have an entry for a subtree in the value node,
                        // but no corresponding entry as a child key node
                        // create a stub with the name and add it
                        children.add(new MruNode(null, vn, vn.name(), this));
                    }
                } else if ("NodeSlot".equals(vn.name())) {    // check for NodeSlot value
                    nodeSlot = ((DwordValue) vn.value()).getValueI();
                }
            }
        }
        // load child nodes
        loadChildren();
    }

    public FileEntryShell fileEntryShell() {
        return (fileEntryShell);
    }

    public KeyNode mruNode() {
        return (keyNode);
    }

    private void loadChildren() throws RegistryException {
        for (MruNode node : children) {
            node.load();
        }
    }

    public MruList children() {
        return (children);
    }

    protected void traverse(Consumer<MruNode> f) {
        Stack<MruList> cellStack = new Stack<>();
        MruList currArray, nextArray;
        Stack<Integer> intStack = new Stack<>();
        int index = 0;

        currArray = new MruList();
        currArray.add(this);

        while (currArray != null) {
            if (index < currArray.size()) {
                MruNode node = currArray.get(index);
                if (node != null) {
                    nextArray = node.children();
                    if (nextArray.size() == 0) {
                        index++;
                    } else {
                        cellStack.push(currArray);
                        currArray = nextArray;
                        intStack.push(index);
                        index = 0;
                    }
                    //System.out.println(cell.toString());
                    f.accept(node);
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

    // process data from value node
    // value node is a SHELLITEM
    // see https://github.com/libyal/libfwsi/blob/master/documentation/Windows%20Shell%20Item%20format.asciidoc
    private void unpack() {
        if (valueNode == null) {
            name = "Root";
            classType = ClassType.C_Root;
        } else if (valueNode.getData() != null) {
            buffer = new BinBuffer(valueNode.getData());
            // first two bytes are the length field
            int valueLength = buffer.getShort();
            // next byte is class field
            clsid = buffer.getByte();
            classType = getClassType(clsid);
            switch (classType) {
                case C_RootFolder:
                    unpackRootFolder();
                    break;
                case C_MyComputer:
                    unpackMyComputer();
                    break;
                case C_MountedRemovable:
                    unpackMountedRemovable();
                    break;
                case C_ShellFSFolder:
                    unpackShellFSFolder();
                    break;
                case C_Unknown:
                    name = "Unknown Class Type " + NumFormat.numToHex((byte) clsid);
                    break;
            }
        } else {
            name = "(invalid key value)";
        }
    }

    private void unpackRootFolder() {
        name = "Root folder shell item";
    }

    private void unpackMyComputer() {
        // root folder is an ASCII zero-terminated string at offset 0x03
        buffer.position(0x03);
        name = buffer.getAsciiStringZ(buffer.size() - 0x03);
    }

    private void unpackMountedRemovable() {
        // name is a UNICODE string at 0x28
        buffer.position(0x28);
        name = buffer.getUnicodeStringZ(buffer.size() - 0x28);
    }

    private void unpackShellFSFolder() {
        fileEntryShell = new FileEntryShell(buffer);
        name = fileEntryShell.primaryname();
    }

    @Override
    public String toString() {
        //return (name + " (" + classType + ")");
        return (name);
    }

    private enum ClassType {

        C_Unknown, C_ShellDesktop, C_RootFolder, C_MyComputer, C_ShellFSFolder,
        C_NetworkRoot, C_CompressedFolder, C_Internet, C_ControlPanel,
        C_Printers, C_CommonPlacesFolder, C_UsersFilesFolder, C_Root, C_MountedRemovable;
    };

    private ClassType getClassType(int clsid) {
        if (clsid == 0x1e) {
            return (ClassType.C_ShellDesktop);
        } else if (clsid == 0x1f) {
            return (ClassType.C_RootFolder);
        } else if (clsid == 0x2e) {
            return (ClassType.C_MountedRemovable);
        } else if ((clsid >= 0x20) && (clsid <= 0x2f)) {
            return (ClassType.C_MyComputer);
        } else if ((clsid >= 0x30) && (clsid <= 0x3f)) {
            return (ClassType.C_ShellFSFolder);
        } else if ((clsid >= 0x40) && (clsid <= 0x4f)) {
            return (ClassType.C_NetworkRoot);
        } else if (clsid == 0x52) {
            return (ClassType.C_CompressedFolder);
        } else if (clsid == 0x71) {
            return (ClassType.C_ControlPanel);
        }
        return (ClassType.C_Unknown);
    }

    public void update() {
        valueNode.setData(buffer.data());
    }

    public int nodeSlot() {
        return (nodeSlot);
    }

    public void bagNode(KeyNode bagNode) {
        this.bagNode = bagNode;
    }

    public KeyNode bagNode() {
        return (bagNode);
    }

    public void delete() throws RegistryException {
        // delete parent value
        if (parentMruNode != null) {
            parentMruNode.deleteChild(this);   
            parentMruNode.deleteValue(key());
        }
        // delete this MRU node
        deleteKeyNode();
        // delete Bag node
        if (bagNode != null) {
            bagNode.delete(true);
        }
    }

    private void deleteChild(MruNode node) {
        if (children != null) {
            children.remove(node);
        }
    }
    
    private void deleteKeyNode() throws RegistryException {
        if (keyNode != null) {
            keyNode.delete(true);
            // all keys names above the deleted key will need to be decremented           
            // to prevent 'holes' in the shell bags
            int thisKey = Integer.parseInt(key());
            if (parentMruNode != null) {
                parentMruNode.renumberKeys(thisKey);
                parentMruNode.renumberValues(thisKey);
            }
        }
    }

    private void renumberKeys(int refKey) throws RegistryException {
        for (MruNode node : children) {
                int ikey = Integer.parseInt(node.key());
                if (ikey > refKey) {
                    String newKey = "" + (ikey - 1);
                    node.key(newKey);
                }
        }
    }
    
    private void renumberValues(int refKey) throws RegistryException {
        for (ValueNode vn: keyNode.values()) {
            if (StringEx.isInt(vn.name())) {
                int ikey = Integer.parseInt(vn.name());
                if (ikey > refKey) {
                    String newKey = "" + (ikey - 1);
                    vn.name(newKey);
                }
            }
        }
    }

    public String path() {
        if (keyNode != null) {
            return (keyNode.path());
        } else {
            return (null);
        }
    }

    // this method will be called by a child node when it is deleted
    // we also need to delete the corresponding entry from the value list
    public void deleteValue(String name) throws RegistryException {
        for (ValueNode vn : keyNode.values()) {
            if (name.equals(vn.name())) {
                vn.delete(true);
            }
        }
    }

    public MruNode parent() {
        return (parentMruNode);
    }

    public ValueNode value(String name) throws RegistryException {
        return (keyNode.value(name));
    }

    public String name() {
        return (name);
    }

    public String key() {
        return (key);
    }
    
    public void key(String key) throws RegistryException {
        this.key = key;
        keyNode.name(key);
    }
}
