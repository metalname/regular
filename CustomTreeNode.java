package TreeHandler;

import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import registry.Nodes.KeyNode;

/**
 *
 *
 */
public class CustomTreeNode extends DefaultMutableTreeNode implements Iterable<CustomTreeNode> {

    private static final long serialVersionUID = -3631816213605959977L;
    private boolean childrenLoaded = false;

    public CustomTreeNode(KeyNode keyNode) {
        super(keyNode);
        if (!keyNode.hasChildren()) {
            childrenLoaded = true;
        }
    }

    @Override
    public boolean isLeaf() {
        return (!getUserObject().hasChildren());
    }

    @Override
    public KeyNode getUserObject() {
        return ((KeyNode) super.getUserObject());
    }

    @Override
    public CustomTreeNode getParent() {
        return ((CustomTreeNode) super.getParent());
    }

    public void childrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }

    public boolean childrenLoaded() {
        return (childrenLoaded);
    }

    @Override
    public Iterator<CustomTreeNode> iterator() {
        return (new Iterator() {

            private final Enumeration<TreeNode> enumeration = children();

            @Override
            public boolean hasNext() {
                return (enumeration.hasMoreElements());
            }

            @Override
            public CustomTreeNode next() {
                return ((CustomTreeNode) enumeration.nextElement());
            }

        });
    }

}
