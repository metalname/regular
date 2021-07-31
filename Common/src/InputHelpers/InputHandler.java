package InputHelpers;

import javax.swing.JFrame;

/**
 *
 * Helper class - handles getting and setting of data to be edited
 */
public class InputHandler {
    
    private final JFrame frame;
    private FiletimeInput filetimeInput;
    private BinaryInput binaryInput;
    
    public InputHandler(JFrame frame) {
        this.frame = frame;
    }
    
    public boolean handleInput(AbstractInput input, String title) {
        InputDialog inputDialog = new InputDialog(frame, title, input);
        inputDialog.setVisible(true);
        return(inputDialog.status());
    }    
    
    public FiletimeInput filetimeInput() {
        if (filetimeInput == null) {
            filetimeInput = new FiletimeInput();
        }
        return(filetimeInput);
    }
    
    public BinaryInput binaryInput() {
        if (binaryInput == null) {
            binaryInput = new BinaryInput();
        }
        return(binaryInput);        
    }
    
}
