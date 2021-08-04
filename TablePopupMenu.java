package RegTableHandler;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Implements a popup context menu for registry values
 * 
 */
public class TablePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = 993980897472693779L;

    protected JMenuItem menuDelete, menuProperties;
    protected RegTableHandler handler;

    /**
     * Constructor
     * 
     * @param handler 
     */
    public TablePopupMenu(RegTableHandler handler) {
        super();
        this.handler = handler;
        // add 'delete' menu item
        menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(this::menuDeleteActionPerformed);
        add(menuDelete);
        // add 'properties' menu item
        menuProperties = new JMenuItem("Properties");
        menuProperties.addActionListener(this::menuPropertiesActionPerformed);
        add(menuProperties);        
    }

    /**
     * Handle 'delete' menu item
     * 
     * @param e 
     */
    public void menuDeleteActionPerformed(ActionEvent e) {
        handler.handleDeleteDialog();
    }

    /**
     * Handle 'properties' menu item
     * 
     * @param e 
     */
    public void menuPropertiesActionPerformed(ActionEvent e) {
        handler.showProperties();
    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
    }

}
