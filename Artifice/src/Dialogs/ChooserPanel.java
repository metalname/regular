/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogs;

import javax.swing.JPanel;

/**
 *
 * @author 
 */
public abstract class ChooserPanel extends JPanel {

    private static final long serialVersionUID = -5720723168020703225L;
    
    public ChooserPanel() {
        
    }
    
    public abstract String getTitle();
    public abstract String getSelected();
    
}
