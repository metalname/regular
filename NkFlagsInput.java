package Properties;

import java.awt.GridLayout;
import javax.swing.JCheckBox;
import registry.element.NkFlagsDataElement;
import InputHelpers.AbstractInput;

/**
 *
 * Swing input dialog for NK flags
 * Display flags as a set of check boxes
 */
public class NkFlagsInput extends AbstractInput {

    protected final JCheckBox[] checkBoxes;     // Swing check boxes

    public NkFlagsInput() {
        super();
        setLayout(new GridLayout(0, 1));
        // create array of check boxes using flags from NkFlagsDataElement
        checkBoxes = new JCheckBox[NkFlagsDataElement.getFlags().length - 1];
        for (int i = 0; i < (NkFlagsDataElement.getFlags().length) - 1; i++) {
            checkBoxes[i] = new JCheckBox(NkFlagsDataElement.getFlags()[i + 1], false);
            add(checkBoxes[i]);
        }

    }

    /**
     * Set value from string
     * 
     * @param flagString 
     */
    public void setValue(String flagString) {
        int value = NkFlagsDataElement.flagsFromString(flagString);
        if (value != -1) {
            setValue(value);
        }
    }

    /**
     * Set value from int
     * Value will be interpreted as a bitmap
     * 
     * @param value 
     */
    public void setValue(int value) {
        int m = 1;
        // ityerate over bits and set checkbox value
        for (JCheckBox cb : checkBoxes) {
            // set checkbox value to true or false depending on value of bit at position m
            cb.setSelected((value & m) != 0);   
            m *= 2;
        }
    }

    /**
     * Get value as int
     * Packs checkbox state (true/false) as bits 
     * @return 
     */
    public int getValue() {
        int m = 1;
        int value = 0;
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                value += m;
            }
            m *= 2;
        }
        return (value);
    }
}
