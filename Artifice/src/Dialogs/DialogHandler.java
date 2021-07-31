package Dialogs;

import Forms.MainFrame;
import Paths.Paths;
import java.io.File;
import javax.swing.JOptionPane;

/**
 *
 * Handles various shared dialogs
 * 
 */
public class DialogHandler {

    private final MainFrame frame;
    private String[] users;

    /**
     * Constructor
     * 
     * @param frame 
     */
    public DialogHandler(MainFrame frame) {
        this.frame = frame;
    }

    /**
     * Handles the 'select root' dialog
     * Will return root is previously selected, or display dialog if not
     * 
     * @return 
     */
    public String getRoot() {
        RootChooserPanel driveChooserPanel = new RootChooserPanel();
        if (handleDialog(driveChooserPanel, "Select Root") == JOptionPane.OK_OPTION) {
            enumerateUsers(driveChooserPanel.getSelected());
            return (driveChooserPanel.getSelected());
        }
        return (null);
    }

    /**
     * Enumerates users on target image and displays them for selection
     * 
     * @param drive 
     */
    private void enumerateUsers(String drive) {
        String path = drive + Paths.Users;
        File fd = new File(drive + Paths.Users);
        if (fd.listFiles() != null) {
            users = new String[fd.listFiles().length];
            for (int i = 0; i < fd.listFiles().length; i++) {
                users[i] = fd.listFiles()[i].getName();
            }
        } else {
            frame.showErrorDialog("No user folders under " + path);
        }
    }

    /**
     * Handles the 'select user' dialog
     * 
     * @param drive
     * @return 
     */
    public String getUser(String drive) {
        UserChooserPanel userChooserPanel = new UserChooserPanel(drive, users);
        if (userChooserPanel.getList().getModel().getSize() > 0) {
            if (handleDialog(userChooserPanel, "Select User") == JOptionPane.OK_OPTION) {
                return (userChooserPanel.getSelected());
            }
        }
        return (null);
    }

    private int handleDialog(ChooserPanel chooserPanel, String title) {
        return (JOptionPane.showOptionDialog(frame, chooserPanel,
                title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null));
    }

    public MainFrame frame() {
        return (frame);
    }

    public String[] getUsers() {
        return (users);
    }

}
