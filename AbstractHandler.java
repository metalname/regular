package Forms;

import Dialogs.DialogHandler;
import javax.swing.JOptionPane;
import registry.RegistryException;

/**
 *
 * Base handler class
 * 
 * Each top-level menu item will have a handler
 */
public abstract class AbstractHandler {

    protected final DialogHandler dialogHandler;
    protected String root, user;

    /**
     * Constructor
     * 
     * @param dialogHandler 
     */
    public AbstractHandler(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }

    public MainFrame frame() {
        return (dialogHandler.frame());
    }

    /**
     * Shared close dialog
     * 
     * @return 
     */
    public boolean confirmClose() {
        if (changed()) {
            int result = JOptionPane.showConfirmDialog(dialogHandler.frame(), "Data has been modified. Save changes?",
                    "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                save();
                close();
                return (true);
            } else if (result == JOptionPane.NO_OPTION) {
                close();
                return (true);
            }
            return (false);
        } else {
            return (true);
        }
    }

    /**
     * Gets the selected root path to the Windows image, if set
     * 
     * @return 
     */
    public boolean getRoot() {
        root = dialogHandler.frame().getSelectedRoot();
        if (root == null) {
            frame().getRoot();
            root = frame().getSelectedRoot();
        }
        if (root != null) {
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * Gets the selected user account if set
     * Shows dialog otherwise
     * 
     * @return 
     */
    public boolean getUser() {
        if (getRoot()) {
            user = dialogHandler.frame().getSelectedUser();
            if (user == null) {
                frame().getUser();
                user = dialogHandler.frame().getSelectedUser();
            }
            if (user != null) {
                return (true);
            } else {
                return (false);
            }
        } else {
            return (false);
        }
    }

    /**
     * Show the base form associated with this handler
     * 
     * @return
     * @throws RegistryException 
     */
    public abstract boolean show() throws RegistryException;

    /**
     * Close this form
     * 
     * @return 
     */
    public abstract boolean close();

    /**
     * Save changes
     * 
     */
    public abstract void save();

    /**
     * Check if data has been changed
     * 
     * @return 
     */
    public abstract boolean changed();
}
