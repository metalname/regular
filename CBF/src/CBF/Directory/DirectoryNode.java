/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.Directory;

/**
 *
 * @author 
 */
public class DirectoryNode {
    
    public DirectoryEntry entry;
    public DirectoryNode parent, left, right;
    public DirectoryTree subTree;
    public DirectoryTree containerTree;
    public DirectoryEntry.Color color;
    public int sid = 0;
    
    public DirectoryNode(DirectoryEntry entry) {
        this.entry = entry;
        this.color = entry.getColor();
    }
    
    public DirectoryNode getLeft() {
        return(left);
    }
    
    public void setLeft(DirectoryNode node) {
        this.left = node;
    }
    
    public DirectoryNode getRight() {
        return(right);
    }
    
    public void setRight(DirectoryNode node) {
        this.right = node;
    }
    
    public DirectoryNode getParent() {
        return(parent);
    }
    
    public void setParent(DirectoryNode node) {
        this.parent = node;
    }
    
    public int leftIndex() {
        return(entry.getLeftSib());
    }
    
    public int rightIndex() {
        return(entry.getRightSib());
    }
    
    public String getName() {
        return(entry.getName());
    }
    
    public DirectoryTree getSubTree() {
        return(subTree);
    }
    
    public void setSubTree(DirectoryTree tree) {
        this.subTree = tree;
    }
    
    public DirectoryEntry getEntry() {
        return(entry);
    }
    
    public void setEntry(DirectoryEntry entry) {
        this.entry = entry;
    }
    
    public DirectoryEntry.Color getColor() {
        return(color);
    }
    
    public void setColor(DirectoryEntry.Color color) {
        this.color = color;
    }
    
    public int getSID() {
        return(sid);
    }
    
    public void setSID(int sid) {
        this.sid = sid;
    }
    
    public void setContainerTree(DirectoryTree tree) {
        this.containerTree = tree;
    }
    
    public DirectoryTree getContainerTree() {
        return(containerTree);
    }
    
    public DirectoryNode sibling() {
        assert parent != null; // Root node has no sibling
        if (this == parent.left)
            return parent.right;
        else
            return parent.left;
    }
    
}
