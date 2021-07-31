package Forms;

import Dialogs.DialogHandler;
import Forms.System.USBDrives.SystemUSBDrivesHandler;
import Forms.System.Accounts.UserAccountsHandler;
import Forms.User.Assist.UserAssistHandler;
import Forms.User.JumpList.UserJumpListHandler;
import Forms.User.RecentDocs.UserRecentDocsHandler;
import Forms.User.ShellBags.UserShellBagsHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import registry.RegistryException;

/**
 *
 * This is the initial frame displayed when the application starts
 */
public final class MainFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 6295307430766303434L;

    /**
     * Enums for all implemented form handlers
     * 
     */
    private enum HandlerEnum {

        H_SYSTEM_ACCOUNTS, H_USER_ASSIST, H_USER_SHELLBAGS, H_USER_RECENTDOCS, 
        H_SYSTEM_USBDRIVES, H_USER_JUMPLISTS;
    }

    // instantiate a DialogHandler for use by all sub-forms
    private final DialogHandler dialogHandler = new DialogHandler(this);
    // list of implemented handlers
    private final HandlerList handlers;
    // selected root path to image
    private String selectedRoot;
    // selected user account
    private String selectedUser;

    /**
     * Constructor
     */
    public MainFrame() {
        initComponents();
        // build a list of handlers for menu items
        handlers = new HandlerList(HandlerEnum.values().length, this::handlerBuilder);
                
        // handle the close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAll();
            }
        });
    }

    /**
     * Creates handlers for all implemented menu items
     * 
     * @param e
     * @return 
     */
    public AbstractHandler handlerBuilder(Enum e) {
        switch ((HandlerEnum) e) {
            case H_SYSTEM_ACCOUNTS:
                return (new UserAccountsHandler(dialogHandler));
            case H_USER_ASSIST:
                return (new UserAssistHandler(dialogHandler));
            case H_USER_SHELLBAGS:
                return (new UserShellBagsHandler(dialogHandler));
            case H_USER_RECENTDOCS:
                return (new UserRecentDocsHandler(dialogHandler));
            case H_SYSTEM_USBDRIVES:
                return(new SystemUSBDrivesHandler(dialogHandler));
            case H_USER_JUMPLISTS:
                return(new UserJumpListHandler(dialogHandler));                
        }
        return (null);
    }

    private void closeAll() {
        if (!handlers.close()) {
            return;
        }
        dispose();
        System.exit(0);
    }
    
    /**
     * Updates the display at the top of the form
     * 
     */
    public void showTitle() {
        String title = "Artifice " + 
                (selectedRoot == null ? "" : selectedRoot) + 
                (selectedUser == null ? "" : selectedUser);
        setTitle(title);
    }

    /**
     * Getter for selected root path
     * 
     * @return 
     */
    public String getSelectedRoot() {
        return(selectedRoot);
    }
    
    /**
     * Getter for selected user account
     * 
     * @return 
     */
    public String getSelectedUser() {
        return(selectedUser);
    }
    
    /**
     * Displays an error message
     * 
     * @param message 
     */
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Getter for mainDesktopPane
     * 
     * @return 
     */
    public JDesktopPane getDesktopPane() {
        return (mainDesktopPane);
    }

    /**
     * Displays a message on the info panel
     * 
     * @param message 
     */
    public void showInfo(String message) {
        Thread thread = new Thread(() -> {
            infoPanel.setText(message);
        });
        thread.start();
    }

    /**
     * Handles a menu click
     * Passes control to DialogHandler associated with menu item
     * 
     * @param he 
     */
    private void handleMenu(HandlerEnum he) {
        try {
        handlers.show(he);
        } catch (RegistryException e) {
            showErrorDialog(e.getMessage());
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Inner class
    // Creates and handles an info panel at the bottom of the main form
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private class InfoPanel extends JPanel {
        private static final long serialVersionUID = -511125222637881645L;

        private final JLabel infoLabel = new JLabel("Ready");

        public InfoPanel(int width) {
            super();
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());
            add(infoLabel, BorderLayout.CENTER);
        }

        @Override
        public Dimension getMaximumSize() {
            return (super.getMaximumSize());
        }

        @Override
        public Dimension getPreferredSize() {
            return (super.getPreferredSize());
        }

        public void setText(String text) {
            infoLabel.setText(text);
        }

    }
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //**
    // Main form components 
    //**
    
    private JPanel mainPanel;               // main panel
    private JDesktopPane mainDesktopPane;   // main desktop pane

    // menus
    private JMenuBar menuMain;              // main menu

    private InfoPanel infoPanel;            // info panel
    
    // File menu
    private JMenu menuFile;                 // File menu
    private JMenuItem menuFileExit;         // File|Exit menu item
    
    private void initFileMenu() {
        menuFile = new JMenu("File");
        menuFileExit = new JMenuItem("Exit");       
        // add menu item listener
        menuFileExit.addActionListener((evt) -> {
            menuFileExitActionPerformed(evt);
        });
        
        menuFile.add(menuFileExit);
        menuMain.add(menuFile);        
    }
    
    // Options menu
    private JMenu menuOptions;              // Options menu
    private JMenuItem menuOptionsRoot;      // Options|Select Root menu item
    private JMenuItem menuOptionsUser;      // Options|Select User menu item
    
    private void initOptionsMenu() {
        menuOptions = new JMenu("Options");
        menuOptionsRoot = new JMenuItem("Select Root");
        menuOptionsUser = new JMenuItem("Select User");  

        menuOptionsRoot.addActionListener((evt) -> {
            menuOptionsRootActionPerformed(evt);
        });
        
        menuOptions.add(menuOptionsRoot);

        menuOptionsUser.addActionListener((evt) -> {
            menuOptionsUserActionPerformed(evt);
        });
        menuOptions.add(menuOptionsUser);

        menuMain.add(menuOptions);        
    }
    
    // User menu
    private javax.swing.JMenu menuUser;                 // Options menu
    private javax.swing.JMenuItem menuUserAssist;       // Options|User Assist menu item
    private javax.swing.JMenuItem menuUserShellBags;    // Options|Shell Bags menu item
    private javax.swing.JMenuItem menuUserRecentDocs;   // Options|Recent Docs menu item
    private javax.swing.JMenuItem menuJumpLists;        // Options|Jump Lists menu item
    
    /**
     * Initialize the User menu
     * Actions for this menu will use the same handler
     * The action will pass an enum to the handler to identify which form to open
     */
    private void initUserMenu() {
        menuUser = new JMenu("User");
        menuUserAssist = new JMenuItem("User Assist");
        menuUserShellBags = new JMenuItem("Shell Bags");
        menuUserRecentDocs = new JMenuItem("Recent Docs");
        menuJumpLists = new JMenuItem("Jump Lists");

        // User menu
        // User Assist
        menuUserAssist.addActionListener((evt) -> {
            handleMenu(HandlerEnum.H_USER_ASSIST);
        });
        menuUser.add(menuUserAssist);

        // Shell Bags
        menuUserShellBags.addActionListener((evt) -> {
            handleMenu(HandlerEnum.H_USER_SHELLBAGS);
        });
        menuUser.add(menuUserShellBags);
        
        // Shell Bags
        menuUserRecentDocs.addActionListener((evt) -> {
            handleMenu(HandlerEnum.H_USER_RECENTDOCS);
        });
        menuUser.add(menuUserRecentDocs);
        
        // Jump Lists
        menuJumpLists.addActionListener((evt) -> {
            handleMenu(HandlerEnum.H_USER_JUMPLISTS);
        });
        menuUser.add(menuJumpLists);        
        
        menuMain.add(menuUser);        
    }
    
    // Sstem menu
    private JMenu menuSystem;               // System menu
    private JMenuItem menuSystemAccounts;   // System|User Accounts menu item
    private JMenuItem menuSystemUSBDrives;  // System|USB Drives menu item
        
    private void initSystemMenu() {
        menuSystem = new JMenu("System");
        
        // USB Drives        
        menuSystemUSBDrives = new JMenuItem("USB Drives");
        menuSystemUSBDrives.addActionListener((evt) -> {
            handleMenu(HandlerEnum.H_SYSTEM_USBDRIVES);
        });
        menuSystem.add(menuSystemUSBDrives);
        
        // User Accounts
        menuSystemAccounts = new JMenuItem("User Accounts");        
        menuSystemAccounts.addActionListener((evt) -> {
            handleMenu(HandlerEnum.H_SYSTEM_ACCOUNTS);
        });
        menuSystem.add(menuSystemAccounts);

        menuMain.add(menuSystem);            
    }

    /**
     * Initialize menu
     */
    private void initMenu() {
        menuMain = new JMenuBar();
        initFileMenu();
        initOptionsMenu();
        initSystemMenu();
        initUserMenu();        
        setJMenuBar(menuMain);
    }

    /**
     * Initialize components
     */
    private void initComponents() {

        mainDesktopPane = new JDesktopPane();
        mainPanel = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        mainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mainPanel.setPreferredSize(new Dimension(800, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;

        getContentPane().add(mainPanel, gbc);

        infoPanel = new InfoPanel(mainPanel.getWidth());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        getContentPane().add(infoPanel, gbc);

        mainDesktopPane = new JDesktopPane();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(mainDesktopPane, BorderLayout.CENTER);

        initMenu();

        pack();
    }
    
    /**
     * Show the 'select root' dialog
     * 
     */
    public void getRoot() {
        selectedRoot = dialogHandler.getRoot();
        showTitle();
    }
    
    /**
     * Action handler for 'select root' menu item
     * @param evt 
     */
    private void menuOptionsRootActionPerformed(ActionEvent evt) {
        getRoot();
    }
    
    public void getUser() {        
        if (selectedRoot == null) {
            selectedRoot = dialogHandler.getRoot();
        }
        if (selectedRoot != null) {
            selectedUser = dialogHandler.getUser(selectedRoot);
        }
        showTitle();        
    }
    
    /**
     * Action handler for 'select user' menu item
     * 
     * @param evt 
     */
    private void menuOptionsUserActionPerformed(ActionEvent evt) {
        getUser();
    }

    /**
     * Action handler for File|Exit menu item
     * @param evt 
     */
    private void menuFileExitActionPerformed(ActionEvent evt) {
        closeAll();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            var frame = new MainFrame();
            frame.setVisible(true);
            // get the root path to the Windows image
            frame.getRoot();

        });
    }

}
