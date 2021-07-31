/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author 
 */
public class TableForm extends JInternalFrame implements AbstractInternalFrameListener {
    private static final long serialVersionUID = 3156002863928689815L;
    protected AbstractHandler handler;

    /**
     * Creates new form UserAccountsForm
     */
    public TableForm(String title, AbstractHandler handler) {
        super(title, true, true, true, true);
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);        
        initComponents();
        addInternalFrameListener(this);
        this.handler = handler;     
    }
        
    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        if (handler.confirmClose()) {
            dispose();
        }
    }       
    
    public JTable getTable() {
        return(mainTable);
    }
    
    //private javax.swing.JPanel UserAccountPanel;
    protected JTable mainTable;
    protected JScrollPane jScrollPane1;
    protected JToolBar toolBar;
    protected JButton btSave;
    
    @SuppressWarnings("unchecked")
    private void initComponents() {
        
        setLayout(new BorderLayout());
        
        addToolBar();
        addMainPanel();
                
        pack();
    }      
    
    protected void addMainPanel() {
        mainTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane(mainTable);

        mainTable.setModel(new javax.swing.table.DefaultTableModel());
        jScrollPane1.setViewportView(mainTable);      
        add(jScrollPane1, BorderLayout.CENTER);
    }
    
    protected void addToolBar() {
        toolBar = new JToolBar();        
        addSaveButton();
        add(toolBar, BorderLayout.PAGE_START);        
    }
    
    protected void addSaveButton() {
        btSave = new JButton();
        btSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Forms/resources/filesave.png"))); // NOI18N
        btSave.setFocusable(false);
        btSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btSave.addActionListener(this::saveButtonActionPerformed);
        btSave.setToolTipText("Save Changes");
        btSave.setEnabled(false);
        toolBar.add(btSave);        
    }
    
    public void saveButtonActionPerformed(ActionEvent e) {
        handler.save();
    }
    
    public JButton btSave() {
        return(btSave);
    }
    
    public void title(String title) {
        setTitle(title);
    }
              
}
