/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author 
 */
public interface AbstractInternalFrameListener extends InternalFrameListener {

    @Override
    public default void internalFrameDeactivated(InternalFrameEvent e) {
    }
    
    @Override
    public default void internalFrameActivated(InternalFrameEvent e) {        
    }
    
    @Override
    public default void internalFrameDeiconified(InternalFrameEvent e) {        
    }    
    
    @Override
    public default void internalFrameIconified(InternalFrameEvent e) {        
    }       
    
    @Override
    public default void internalFrameClosed(InternalFrameEvent e) {
    }   
    
    public default void internalFrameClosing(InternalFrameEvent e) {        
    }
    
    public default void internalFrameOpened(InternalFrameEvent e) {        
    }    
    
}
