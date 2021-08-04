/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author 
 */
public interface AbstractKeyListener extends KeyListener {
    
    @Override
    public default void keyReleased(KeyEvent e) {
    }

    @Override
    public default void keyPressed(KeyEvent e) {
    }

    @Override
    public default void keyTyped(KeyEvent e) {
    }    
    
}
