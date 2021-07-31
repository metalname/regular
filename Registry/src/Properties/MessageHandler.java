package Properties;

import javax.swing.JFrame;

/**
 *
 * Message handler interface
 */
public interface MessageHandler {
    
    public void updateCurrentCell();
    public JFrame frame();
    public void showInfo(String message);
}
