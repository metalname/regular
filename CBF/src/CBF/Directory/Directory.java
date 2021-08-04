package CBF.Directory;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * Implements a CBF directory tree
 */
public class Directory {
    
    // size of each directory entry
    public static final int entrySize = 128;
    
    private final ByteBuffer buffer;            // buffer containing directory stream
    private ArrayList<DirectoryEntry> entries;  // list of directory entries
    private DirectoryTree rootDirectoryTree;    // Reb/Black tree for directory entries
    
    /**
     * Constructor
     * Buffer is the ByteBuffer that contains the directory stream
     * 
     * @param buffer 
     */
    public Directory(ByteBuffer buffer) {
        this.buffer = buffer;
        loadEntries();
        //System.out.println(dump());
        rootDirectoryTree = new DirectoryTree(entries, 0);
    }
    
    /**
     * Load direcory entries
     */
    private void loadEntries() {
        // calculate maximum number of directory entries
        var numEntries = buffer.capacity() / entrySize;
        entries = new ArrayList<>(numEntries);
        // loop over directory buffer and load entries
        for (int i = 0; i < numEntries; i++) {
            buffer.position(i * entrySize);
            entries.add(new DirectoryEntry(buffer));
        }
    }
    
    /**
     * Dump attribute to string for debugging
     * 
     * @return 
     */
    public String dump() {
        var sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            sb.append("SID: ").append(i).append(", ").append(entries.get(i).dump()).append('\n');
        }
        return(sb.toString());
    }
    
    /**
     * Return DirectoryEntry at specified index
     * 
     * @param i
     * @return 
     */
    public DirectoryEntry get(int i) {
        return (entries.get(i));
    }

    /**
     * Return DirectoryNode for give path
     * 
     * @param path
     * @return 
     */
    public DirectoryEntry getEntry(String path) {
        DirectoryNode node = getNode(path);
        if (node != null) {
            return (node.getEntry());
        } else {
            return (null);
        }
    }    
    
    /**
     * Splits path into individual entries
     * Traverses paths to locate node at end of chain
     * 
     * @param path
     * @return 
     */
    public DirectoryNode getNode(String path) {
        String[] dirs = path.split("/");
        DirectoryTree tree = rootDirectoryTree.root().getSubTree();
        DirectoryNode node = null;
        for (String dir : dirs) {
            if (tree == null) {
                node = null;
                break;
            }
            node = tree.findEntry(dir);
            if (node != null) {
                tree = node.getSubTree();
            }
        }
        if (node != null) {
            return (node);
        } else {
            return (null);
        }
    }
    
    /**
     * Search directory entries for specified name
     * 
     * @param name
     * @return 
     */
    public DirectoryEntry getEntryByName(String name) {
        for (var entry: entries) {
            if (entry.getName().equals(name)) {
                return(entry);
            }
        }
        return(null);
    }
    
    
}
