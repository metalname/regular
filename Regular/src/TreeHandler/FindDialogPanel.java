package TreeHandler;

import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import InputHelpers.RequestFocusListener;

/**
 *
 * 
 */
public class FindDialogPanel extends JPanel {
    private static final long serialVersionUID = 431227182327434838L;
    
    private final JTextField textField;
    private final JCheckBox ckIgnoreCase;
    
    public FindDialogPanel(String text, boolean optIgnoreCase) {
        setLayout(new GridLayout(0, 1));
        textField = new JTextField(text, 30);
        textField.addAncestorListener(new RequestFocusListener());
        ckIgnoreCase = new JCheckBox("Ignore Case", optIgnoreCase);
        add(textField);
        add(ckIgnoreCase);
    }
    
    public String getText() {
        return(textField.getText());
    }
    
    public boolean getIgnoreCase() {
        return(ckIgnoreCase.isSelected());
    }
    
}
