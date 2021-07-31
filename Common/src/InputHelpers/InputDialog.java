package InputHelpers;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * Swing container for input helpers
 */
public class InputDialog extends JDialog {
    private static final long serialVersionUID = -5013715490336921601L;
    
    private JButton btOK, btCancel;
    private boolean status;
    
    public InputDialog(JFrame parent, String title, JPanel panel) {
        super(parent);
        setTitle(title);
        init(panel);
    }
    
    private void init(JPanel panel) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(panel, gbc);
        
        btOK = new JButton("OK");
        btOK.addActionListener(e -> {
            status = true;
            dispose();
        });        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;     
        gbc.insets = new Insets(5, 0, 5, 0);
        add(btOK, gbc);     
        
        btCancel = new JButton("Cancel");
        btCancel.addActionListener(e -> {
            status = false;
            dispose();
        });        
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;      
        gbc.insets = new Insets(5, 0, 5, 0);
        add(btCancel, gbc);
        panel.requestFocusInWindow();
        
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        
        pack();
    }
    
    public boolean status() {
        return(status);
    }
    
}
