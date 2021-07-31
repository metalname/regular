/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.directory;

import CBF.file.FileBuffer;
import java.util.ArrayList;

/**
 *
 * @author 
 */
public class Directory {

    private ArrayList<DirectoryEntry> entries;
    private final FileBuffer buffer;
    private DirectoryTree directoryTree;
    private int totalCount = 0;
    private boolean changed = false;

    public Directory(FileBuffer buffer) {
        entries = new ArrayList<>();
        this.buffer = buffer;
        load();
    }

    private void load() {
        buffer.position(0);
        int count = buffer.size() / DirectoryEntry.dirSize;
        for (int i = 0; i < count; i++) {
            buffer.position(i * DirectoryEntry.dirSize);
            DirectoryEntry sid = new DirectoryEntry();
            sid.read(buffer);
            add(sid);
            //System.out.println("SID = " + i);
            //System.out.println(sid.dump());
        }
        directoryTree = new DirectoryTree(entries, 0);
    }

    private class IntWrapper {

        public int number = 0;

        public void increment() {
            number++;
        }
    }

    private void write() {
        totalCount = 0;
        IntWrapper w = new IntWrapper();
        renumberTree(directoryTree, w);
        rebuildEntries();
        writeEntries();
    }

    private void writeEntries() {
        buffer.wipe();
        buffer.position(0);
        int i = 0;
        for (DirectoryEntry sid : entries) {
            buffer.position(i++ * DirectoryEntry.dirSize);
            sid.write(buffer);
        }
    }

    private void rebuildEntries() {
        ArrayList<DirectoryEntry> newList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            newList.add(null);
        }
        rebuildTree(directoryTree, newList);
        entries = newList;
    }

    private void renumberTree(DirectoryTree tree, IntWrapper w) {
        // renumber SIDs for this tree
        renumberSIDs(tree, w);
        // traverse this tree and renumber for any children
        tree.traverse(tree.root(), node -> {
            if (node.getSubTree() != null) {
                renumberTree(node.getSubTree(), w);
            }
        });
        totalCount += tree.getCount();
    }

    // renumber SIDs    
    private void renumberSIDs(DirectoryTree tree, IntWrapper w) {
        traverse(tree.root(), w);
        /*
        tree.traverse(tree.root(), node -> {
            node.setSID(w.number);
            w.increment();
        });
        */
    }
    
    private void traverse(DirectoryNode node, IntWrapper w) {
        if (node.getLeft() != null) {
            traverse(node.getLeft(), w);
        }
        node.setSID(w.number);
        w.increment();
        if (node.getRight() != null) {
            traverse(node.getRight(), w);
        }
    }    

    private void add(DirectoryEntry directory) {
        entries.add(directory);
    }

    private void rebuildTree(DirectoryTree tree, ArrayList<DirectoryEntry> ar) {
        rebuildNodes(tree, ar);
        tree.traverse(tree.root(), node -> {
            if (node.getSubTree() != null) {
                rebuildTree(node.getSubTree(), ar);
            }
        });
    }

    private void rebuildNodes(DirectoryTree tree, ArrayList<DirectoryEntry> ar) {
        tree.traverse(tree.root(), node -> {
            DirectoryEntry entry = node.getEntry();
            if (node.getLeft() == null) {
                entry.setLeftSib(-1);
            } else {
                entry.setLeftSib(node.getLeft().getSID());
            }
            if (node.getRight() == null) {
                entry.setRightSib(-1);
            } else {
                entry.setRightSib(node.getRight().getSID());
            }
            if (node.getSubTree() == null) {
                entry.setChild(-1);
            } else {
                entry.setChild(node.getSubTree().root().getSID());
            }
            entry.setColor(node.getColor());
            ar.set(node.getSID(), entry);
        });
    }

    public DirectoryEntry get(int i) {
        return (entries.get(i));
    }

    public DirectoryEntry getEntry(String path) {
        DirectoryNode node = getNode(path);
        if (node != null) {
            return (node.getEntry());
        } else {
            return (null);
        }
    }

    public DirectoryNode getNode(String path) {
        String[] dirs = path.split("/");
        DirectoryTree tree = directoryTree.root().getSubTree();
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

    public void remove(String name) {
        DirectoryNode node = getNode(name);
        if (node != null) {
            node.getContainerTree().remove(node);
            changed = true;
        }
    }

    public int size() {
        return (entries.size());
    }

    public void delete(String name) {
        remove(name);
    }

    public FileBuffer getBuffer() {
        return (buffer);
    }

    public void save() {
        if (changed) {
            write();
        }
    }

}
