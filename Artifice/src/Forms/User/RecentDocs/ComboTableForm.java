/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.RecentDocs;

import Forms.AbstractHandler;
import Forms.TableForm;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author 
 */
public class ComboTableForm extends TableForm {
    private static final long serialVersionUID = 1528841079433780011L;
    
    protected JButton btNKProperties;
    protected JButton btVKProperties;
    
    public ComboTableForm(String title, AbstractHandler handler) {
        super(title, handler);
    }    
    
    private JComboBox comboBox;
    private JPanel mainPanel;
    
    @Override
    protected void addMainPanel() {
        mainPanel = new JPanel();
        comboBox = new JComboBox();
        comboBox.setEditable(false);
        mainTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane(mainTable);

        mainTable.setModel(new javax.swing.table.DefaultTableModel());
        jScrollPane1.setViewportView(mainTable);      
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(comboBox, BorderLayout.PAGE_START);
        mainPanel.add(jScrollPane1, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }    
    
    public JComboBox comboBox() {
        return(comboBox);
    }
    
    @Override
    protected void addToolBar() {
        toolBar = new JToolBar();        
        addSaveButton();
        addPropertiesButton();
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

    public void propertiesButtonActionPerformed(ActionEvent e) {
        ((UserRecentDocsHandler) handler).showProperties(e);
    }    
            
}
