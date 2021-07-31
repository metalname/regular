/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags;

import Forms.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 
 */
public class ShellBagsForm extends TreeTableForm implements AbstractInternalFrameListener {
    private static final long serialVersionUID = 3156002863928689815L;
    protected JButton btNKProperties;
    protected JButton btBinaryEdit;

    /**
     * Creates new form UserAccountsForm
     */
    public ShellBagsForm(String title, UserShellBagsHandler handler) {
        super(title, handler);
    }

    @Override
    protected void addToolBar() {
        toolBar = new JToolBar();        
        addSaveButton();
        addPropertiesButton();
        addBinaryEditButton();
        add(toolBar, BorderLayout.PAGE_START);        
    }
    
    protected void addPropertiesButton() {
        btNKProperties = new JButton();
        btNKProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Forms/resources/gear.png"))); // NOI18N
        btNKProperties.setFocusable(false);
        btNKProperties.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btNKProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btNKProperties.addActionListener(this::propertiesButtonActionPerformed);
        btNKProperties.setToolTipText("Key Cell (nk) Properties");
        toolBar.add(btNKProperties);        
    }    
    
    protected void addBinaryEditButton() {
        btBinaryEdit = new JButton();
        btBinaryEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Forms/resources/binary.png"))); // NOI18N
        btBinaryEdit.setFocusable(false);
        btBinaryEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btBinaryEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btBinaryEdit.addActionListener(this::binaryEditButtonActionPerformed);
        btBinaryEdit.setToolTipText("Binary Edit");
        toolBar.add(btBinaryEdit);        
    }        

    public void propertiesButtonActionPerformed(ActionEvent e) {
        ((UserShellBagsHandler) handler).showProperties(e);
    }     
    
    public void binaryEditButtonActionPerformed(ActionEvent e) {
        ((UserShellBagsHandler) handler).binaryEdit(e);
    }      
              
}
