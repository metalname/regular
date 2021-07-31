/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.Assist;

import Forms.TableForm;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author 
 */
public class UserAssistForm extends TableForm {
    
    protected JButton btProperties;
    
    public UserAssistForm(String title, UserAssistHandler handler) {
        super(title, handler);
    }
    
    @Override
    protected void addToolBar() {
        toolBar = new JToolBar();        
        addSaveButton();
        addPropertiesButton();
        add(toolBar, BorderLayout.PAGE_START);        
    }
    
    protected void addPropertiesButton() {
        btProperties = new JButton();
        btProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Forms/resources/gear.png"))); // NOI18N
        btProperties.setFocusable(false);
        btProperties.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btProperties.addActionListener(this::propertiesButtonActionPerformed);
        btProperties.setToolTipText("Key Properties");
        toolBar.add(btProperties);        
    }    
    
    public void propertiesButtonActionPerformed(ActionEvent e) {
        ((UserAssistHandler) handler).showProperties(e);
    }
    
}
