/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author 
 */
public class MruTreeNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 4108709377494094562L;
        
    public MruTreeNode(MruNode mruNode) {
        super(mruNode);
    }
    
    @Override
    public MruNode getUserObject() {
        return((MruNode) super.getUserObject());
    }
        
}

