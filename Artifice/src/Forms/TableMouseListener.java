/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author 
 */
public interface TableMouseListener extends MouseListener {
    
    public default void mouseExited(MouseEvent e) {        
    }
    
    public default void mouseEntered(MouseEvent e) {        
    }    
    
    public default void mouseReleased(MouseEvent e) {        
    }       
    
    public default void mousePressed(MouseEvent e) {        
    }     
    
    public default void mouseClicked(MouseEvent e) {   
        CellRef cellRef = new CellRef(e);        
        if (!e.isConsumed()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                singleLeftClick(cellRef);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                singleRightClick(cellRef);
            }
        }
    }     
    
    public default void singleLeftClick(CellRef cellRef) {        
    }
    
    public default void singleRightClick(CellRef cellRef) {        
    }
}
