/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import Forms.User.ShellBags.*;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 
 */
public class TreeTableForm extends TableForm implements AbstractInternalFrameListener {
    private static final long serialVersionUID = 3156002863928689815L;
    protected JButton btNKProperties;
    protected JButton btBinaryEdit;

    /**
     * Creates new form UserAccountsForm
     */
    public TreeTableForm(String title, UserShellBagsHandler handler) {
        super(title, handler);
    }
         
    @Override
    public JTable getTable() {
        return(mainTable);
    }
    
    public JTree getTree() {
        return(mainTree);
    }
    
    public JSplitPane splitPane() {
        return(splitPane);
    }
    
    private JTree mainTree;
    private JScrollPane treeScroller;
    private JScrollPane tableScroller;
    private JSplitPane splitPane;
                     
    @Override
    protected void addMainPanel() {
        mainTable = new JTable();
        mainTree = new JTree();
        treeScroller = new JScrollPane(mainTable);
        tableScroller = new JScrollPane(mainTree);

        mainTable.setModel(new DefaultTableModel());
        mainTree.setModel(null);
        treeScroller.setViewportView(mainTree);
        tableScroller.setViewportView(mainTable);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroller, tableScroller);
        add(splitPane, BorderLayout.CENTER);
        
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            splitPane.setDividerLocation(0.5);
        }
    }
         
}
