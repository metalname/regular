package CBF.Directory;

import java.util.ArrayList;

/**
 *
 * 
 * Implementation of DirectoryEntry.Color.DE_RED/BLACK tree for directory entries
 */
public class DirectoryTree {

    protected ArrayList<DirectoryEntry> entries;
    protected DirectoryNode root;
    protected int count = 0;
    
    protected static final DirectoryEntry.Color BLACK = DirectoryEntry.Color.DE_BLACK;        
    protected static final DirectoryEntry.Color RED  = DirectoryEntry.Color.DE_RED;
              
    /**
     * Constructor
     * 
     * @param entries: ArrayList of Directory
     * 
     * @param startIndex: Starting array entry for this tree (DirectoryTree can have subtrees)
     */
    public DirectoryTree(ArrayList<DirectoryEntry> entries, int startIndex) {
        this.entries = entries;
        if (startIndex != -1) {
            this.root = new DirectoryNode(entries.get(startIndex));
            AddNode(root);
        } else {
            root = null;
        }
    }

    /**
     * Adds a node to the tree
     * 
     * @param node 
     */
    private void AddNode(DirectoryNode node) {
        node.setContainerTree(this);
        if (node.leftIndex() != -1) {
            DirectoryNode left = new DirectoryNode(entries.get(node.leftIndex()));
            node.setLeft(left);
            left.setParent(node);
            AddNode(left);
        }
        if (node.rightIndex() != -1) {
            DirectoryNode right = new DirectoryNode(entries.get(node.rightIndex()));
            node.setRight(right);
            right.setParent(node);
            AddNode(right);
        }
        // build subdirectory
        if (node.getEntry().getChild() != -1) {
            node.setSubTree(new DirectoryTree(entries, node.getEntry().getChild()));
        }
        count++;
    }

    // search for directory getEntry by name
    public DirectoryNode findEntry(String name) {
        return (findRecursive(name, root));
    }

    protected DirectoryNode findRecursive(String name, DirectoryNode node) {
        int c = compareName(name, node.getName());
        if (c < 0) {
            if (node.getLeft() != null) {
                return (findRecursive(name, node.getLeft()));
            } else {
                return (null);
            }
        } else if (c > 0) {
            if (node.getRight() != null) {
                return (findRecursive(name, node.getRight()));
            } else {
                return (null);
            }
        } else {
            return (node);
        }
    }

    protected void traverse(DirectoryNode node, DirectoryAction action) {
        if (node.getLeft() != null) {
            traverse(node.getLeft(), action);
        }
        action.performAction(node);
        if (node.getRight() != null) {
            traverse(node.getRight(), action);
        }
    }
    
    protected int compareName(String left, String right) {
        if (left.length() < right.length()) {
            return (-1);
        } else if (left.length() > right.length()) {
            return (1);
        } else {
            return (left.compareTo(right));
        }
    }

    public DirectoryNode root() {
        return(root);
    }
    
    private static DirectoryEntry.Color nodeColor(DirectoryNode n) {
        return n == null ? BLACK : n.color;
    }
    
    public void delete(DirectoryNode n) {
        if (n == null)
            return;  // Key not found, do nothing
        if (n.left != null && n.right != null) {
            // Copy key/value from predecessor and then delete it instead
            DirectoryNode pred = maximumNode(n.left);
            n.setEntry(pred.getEntry());
            n = pred;
        }

        assert n.left == null || n.right == null;
        DirectoryNode child = (n.right == null) ? n.left : n.right;
        if (nodeColor(n) == BLACK) {
            n.color = nodeColor(child);
            deleteCase1(n);
        }
        replaceNode(n, child);
        count--;
    }
    
    private void replaceNode(DirectoryNode oldn, DirectoryNode newn) {
        if (oldn.parent == null) {
            root = newn;
        } else {
            if (oldn == oldn.parent.left)
                oldn.parent.left = newn;
            else
                oldn.parent.right = newn;
        }
        if (newn != null) {
            newn.parent = oldn.parent;
        }
    }    
    
    private void rotateLeft(DirectoryNode n) {
        DirectoryNode r = n.right;
        replaceNode(n, r);
        n.right = r.left;
        if (r.left != null) {
            r.left.parent = n;
        }
        r.left = n;
        n.parent = r;
    }

    private void rotateRight(DirectoryNode n) {
        DirectoryNode l = n.left;
        replaceNode(n, l);
        n.left = l.right;
        if (l.right != null) {
            l.right.parent = n;
        }
        l.right = n;
        n.parent = l;
    }
    
    private DirectoryNode maximumNode(DirectoryNode n) {
        assert n != null;
        while (n.right != null) {
            n = n.right;
        }
        return n;
    }
    
    private void deleteCase1(DirectoryNode n) {
        if (n.parent == null)
            return;
        else
            deleteCase2(n);
    }
    private void deleteCase2(DirectoryNode n) {
        if (nodeColor(n.sibling()) == RED) {
            n.parent.color = RED;
            n.sibling().color = BLACK;
            if (n == n.parent.left)
                rotateLeft(n.parent);
            else
                rotateRight(n.parent);
        }
        deleteCase3(n);
    }
    private void deleteCase3(DirectoryNode n) {
        if (nodeColor(n.parent) == BLACK &&
            nodeColor(n.sibling()) == BLACK &&
            nodeColor(n.sibling().left) == BLACK &&
            nodeColor(n.sibling().right) == BLACK)
        {
            n.sibling().color = RED;
            deleteCase1(n.parent);
        }
        else
            deleteCase4(n);
    }
    private void deleteCase4(DirectoryNode n) {
        if (nodeColor(n.parent) == RED &&
            nodeColor(n.sibling()) == BLACK &&
            nodeColor(n.sibling().left) == BLACK &&
            nodeColor(n.sibling().right) == BLACK)
        {
            n.sibling().color = RED;
            n.parent.color = BLACK;
        }
        else
            deleteCase5(n);
    }
    
    private void deleteCase5(DirectoryNode n) {
        if (n == n.parent.left &&
            nodeColor(n.sibling()) == BLACK &&
            nodeColor(n.sibling().left) == RED &&
            nodeColor(n.sibling().right) == BLACK)
        {
            n.sibling().color = RED;
            n.sibling().left.color = BLACK;
            rotateRight(n.sibling());
        }
        else if (n == n.parent.right &&
                 nodeColor(n.sibling()) == BLACK &&
                 nodeColor(n.sibling().right) == RED &&
                 nodeColor(n.sibling().left) == BLACK)
        {
            n.sibling().color = RED;
            n.sibling().right.color = BLACK;
            rotateLeft(n.sibling());
        }
        deleteCase6(n);
    }
    
    private void deleteCase6(DirectoryNode n) {
        n.sibling().color = nodeColor(n.parent);
        n.parent.color = BLACK;
        if (n == n.parent.left) {
            assert nodeColor(n.sibling().right) == RED;
            n.sibling().right.color = BLACK;
            rotateLeft(n.parent);
        }
        else
        {
            assert nodeColor(n.sibling().left) == RED;
            n.sibling().left.color = BLACK;
            rotateRight(n.parent);
        }
    }
    

    public int getCount() {
        return(count);
    }
    

    public DirectoryEntry remove(DirectoryNode node) {

        DirectoryEntry oldValue = node.entry;
        delete(node);
        return oldValue;
    }
    
}
