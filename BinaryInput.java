package InputHelpers;

import hexedit.HexEdit;
import java.awt.GridLayout;

/**
 *
 * Swing component for editing of binary data
 */
public class BinaryInput extends AbstractInput {
    private static final long serialVersionUID = -1467422051075776185L;
    
    private final HexEdit hexEdit = new HexEdit(new byte[0]);
    private byte[] buffer;      // model - byte array
    
    /**
     * Constructor
     * 
     */
    public BinaryInput() {
        super();
        setLayout(new GridLayout(1,1));
        hexEdit.addAncestorListener(new RequestFocusListener());    // set focus
        add(hexEdit);
    }
    
    /**
     * Sets value to be edited
     * 
     * @param buffer 
     */
    public void setValue(byte[] buffer) {
        hexEdit.requestFocusInWindow();
        this.buffer = buffer;
        hexEdit.reset(buffer);
    }
    
    /**
     * Get edited data
     * 
     * @return 
     */
    public byte[] getValue() {
        return(buffer);
    }
    
}
