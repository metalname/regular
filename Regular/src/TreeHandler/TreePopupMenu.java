package TreeHandler;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import registry.Nodes.KeyNode;

/**
 *
 * 
 */
public class TreePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = 993980897472693779L;

    protected JMenuItem menuDelete, menuFind;
    protected TreeHandler treeHandler;

    public TreePopupMenu(TreeHandler treeHandler) {
        super();
        this.treeHandler = treeHandler;
        menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(this::menuDeleteActionPerformed);
        add(menuDelete);
        menuFind = new JMenuItem("Find");
        menuFind.addActionListener(this::menuFindActionPerformed);
        add(menuFind);
    }

    public void menuDeleteActionPerformed(ActionEvent e) {
        treeHandler.handleDeleteDialog();
    }
    
    public void menuFindActionPerformed(ActionEvent e) {
        treeHandler.handleFindDialog();
    }

    public void show(Component invoker, int x, int y, KeyNode keyNode) {
        super.show(invoker, x, y);
    }

}
