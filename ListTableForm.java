package Forms;

import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * Defines a split pane with a list on the left-hand side, and a table on the right
 */
public class ListTableForm extends TableForm implements AbstractInternalFrameListener {
    private static final long serialVersionUID = -6246950977994378861L;
    protected JButton btNKProperties;
    protected JButton btBinaryEdit;

    /**
     * Creates new form UserAccountsForm
     * @param title
     * @param handler
     */
    public ListTableForm(String title, AbstractHandler handler) {
        super(title, handler);
    }
         
    @Override
    public JTable getTable() {
        return(mainTable);
    }
    
    public JList getList() {
        return(mainList);
    }
    
    public JSplitPane splitPane() {
        return(splitPane);
    }
    
    private JList mainList;
    private JScrollPane listScroller;
    private JScrollPane tableScroller;
    private JSplitPane splitPane;
                     
    @Override
    protected void addMainPanel() {
        mainTable = new JTable();
        mainList = new JList();
        listScroller = new JScrollPane(mainList);
        tableScroller = new JScrollPane(mainTable);
        
        mainTable.setModel(new DefaultTableModel());
        mainList.setModel(new DefaultListModel());
        listScroller.setViewportView(mainList);
        tableScroller.setViewportView(mainTable);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroller, tableScroller);
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
