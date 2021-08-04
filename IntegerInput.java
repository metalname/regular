package InputHelpers;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * Input helper for integer
 */
public class IntegerInput extends AbstractInput {

    public final static int I_SHORT = 0;
    public final static int I_LONG = 1;
    public final static int I_INT = 2;

    protected final static int B_HEX = 16;
    protected final static int B_DEC = 10;

    protected JTextField numberField;
    protected JRadioButton btnHex;
    protected JRadioButton btnDec;
    protected ButtonGroup buttonGroup;
    protected int numberType = I_INT;
    protected static int base = B_HEX;
    protected long value;

    public IntegerInput() {
        numberField = new JTextField(10);
        numberField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = Character.toLowerCase(e.getKeyChar());
                switch (base) {
                    case B_HEX:
                        if (!(((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'f')) || (c == KeyEvent.VK_BACK_SPACE))) {
                            e.consume();
                        }
                        break;
                    case B_DEC:
                        if (!(((c >= '0') && (c <= '9')) || (c == KeyEvent.VK_BACK_SPACE))) {
                            e.consume();
                        }
                }
            }
        });
        numberField.addAncestorListener(new RequestFocusListener());
        btnHex = new JRadioButton("Hex");
        btnDec = new JRadioButton("Dec");
        buttonGroup = new ButtonGroup();
        buttonGroup.add(btnHex);
        buttonGroup.add(btnDec);
        GridLayout layout = new GridLayout(0, 1);
        setLayout(layout);
        add(numberField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 2));
        buttonPanel.add(btnHex);
        buttonPanel.add(btnDec);
        btnHex.addActionListener(this::hexActionPerformed);
        btnDec.addActionListener(this::decActionPerformed);
        buttonPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(buttonPanel);
    }

    public void setValue(long value, int numberType) {
        this.value = value;
        this.numberType = numberType;
        updateDisplay();
        setButtons();
    }

    public void hexActionPerformed(ActionEvent actionEvent) {
        value = getValue();
        base = B_HEX;
        updateDisplay();
    }

    public void decActionPerformed(ActionEvent actionEvent) {
        value = getValue();
        base = B_DEC;
        updateDisplay();
    }

    protected void setButtons() {
        switch (base) {
            case B_HEX:
                btnHex.setSelected(true);
                break;
            case B_DEC:
                btnDec.setSelected(true);
                break;
        }
    }

    public long getValue() {
        return (Long.parseLong(numberField.getText(), base));
    }

    protected void updateDisplay() {
        switch (base) {
            case B_HEX:
                numberField.setText(Long.toHexString(value));
                break;
            case B_DEC:
                numberField.setText(Long.toString(value));
                break;
        }
    }

    // check if new char would make the number too long
    protected boolean checkLength(long l) {
        String n = numberField.getText();
        switch (numberType) {
            case I_SHORT:
                return (l <= 0xff);
            case I_INT:
                return(true);
            default:
                return (true);
        }
    }

    public boolean isValidNumber() {
        long l;
        try {
            switch (base) {
                case B_HEX:
                    l = Long.parseLong(numberField.getText(), 16);
                    break;
                case B_DEC:
                    l = Long.parseLong(numberField.getText());
                    break;
            }                   
        } catch (NumberFormatException e) {
            return (false);
        }
        return(true);
    }

    public String getValueString() {
        return (numberField.getText());
    }

}
