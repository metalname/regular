package TreeHandler;

import javax.swing.tree.DefaultTreeModel;

/**
 *
 * 
 */
public class CustomTreeModel extends DefaultTreeModel {
    private static final long serialVersionUID = 4101508808879412171L;
    
    public CustomTreeModel(CustomTreeNode root) {
        super(root);
    }
}
