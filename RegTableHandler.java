package RegTableHandler;

import Properties.MessageHandler;
import Forms.FormController;
import Forms.MainFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import InputHelpers.AbstractInput;
import InputHelpers.BinaryInput;
import Properties.PropertiesHandler;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;

/**
 * Handler for the registry values table (right-hand side of pane)
 *
 */
public class RegTableHandler extends MouseAdapter implements CellEditorListener, KeyListener, MessageHandler {

    private final MainFrame frame;
    private final FormController controller;
    private final JTextField textField = new JTextField();
    private final RegCellEditor customCellEditor = new RegCellEditor();
    private final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(textField);
    private RegTableModel tableModel;
    private CellRef editingCell;
    private final TablePopupMenu tablePopupMenu;
    private boolean inEdit = false;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Internal class - defines a cell point
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    protected class CellRef {

        protected final int row;
        protected final int col;
        protected final ValueNode value;
        protected final JTable table;
        protected Point point;

        public CellRef(MouseEvent evt) {
            table = (JTable) evt.getSource();
            point = evt.getPoint();
            row = table.rowAtPoint(evt.getPoint());
            col = table.columnAtPoint(evt.getPoint());
            if (table.getModel().getValueAt(row, col) instanceof ValueNode) {
                value = (ValueNode) table.getModel().getValueAt(row, col);
            } else {
                value = null;
            }
        }

        protected int getRow() {
            return (row);
        }

        protected int getCol() {
            return (col);
        }

        protected ValueNode getValue() {
            return (value);
        }

        protected JTable getTable() {
            return (table);
        }

        protected boolean isValue() {
            return (value != null);
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Constructor
     *
     * @param frame
     * @param controller
     */
    public RegTableHandler(MainFrame frame, FormController controller) {
        this.tablePopupMenu = new TablePopupMenu(this);
        this.frame = frame;
        this.controller = controller;
        table().addMouseListener(this); // set parent JTable mouse listener to this object
        table().addKeyListener(this);   // set parent JTable key listener to this object
        customCellEditor.addCellEditorListener(this);
        defaultCellEditor.addCellEditorListener(this);
    }

    /**
     * Handle mouse click event
     *
     * @param evt
     */
    @Override
    public void mouseClicked(final MouseEvent evt) {
        singleClick(evt);
    }

    public void singleClick(final MouseEvent evt) {
        CellRef cellRef = new CellRef(evt);
        // distinguish left and right click
        if (SwingUtilities.isLeftMouseButton(evt) && !(evt.isConsumed())) {
            // left-click, edit cell
            editCell(cellRef);
        } else if (SwingUtilities.isRightMouseButton(evt) && !(evt.isConsumed())) {
            // right-click, bring up context menu
            int row = frame.getTable().rowAtPoint(cellRef.point);
            frame.getTable().setRowSelectionInterval(row, row);
            tablePopupMenu.show(frame.getTable(), (int) cellRef.point.getX(), (int) cellRef.point.getY());
        }
    }

    /**
     * Display a message on the parent frame
     *
     * @param message
     */
    @Override
    public void showInfo(String message) {
        frame().showInfo(message);
    }

    protected int handleInput(AbstractInput input, String title) {
        return (JOptionPane.showOptionDialog(frame(), input,
                title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null));
    }

    /**
     * Edit the currently selected cell
     *
     * @param cellRef
     */
    protected void editCell(final CellRef cellRef) {
        editingCell = cellRef;      // create a cellRef object at the selected location
        // check which column of the table was clicked
        // column 0 is the value name
        // column 2 is the actual value
        if (cellRef.getCol() == 0) {
            inEdit = true;              // set the in-edit flag            
            // editing the value name
            // make this cell editable                
            ((RegTableModel) cellRef.getTable().getModel())
                    .setCellEditable(cellRef.getRow(), cellRef.getCol(), true);
            // edit the value name
            cellRef.getTable().editCellAt(cellRef.getRow(), cellRef.getCol());
        }
        if (cellRef.getCol() == 2) {
            inEdit = true;              // set the in-edit flag
            // editing the cell value
            // check value data type and invoke corresponding editors
            switch (cellRef.getValue().type()) {
                case REG_BINARY:
                case REG_UNKNOWN:
                    // call the hex editor
                    ValueNode vn = cellRef.getValue();
                    BinaryInput binaryInput = new BinaryInput();
                    binaryInput.setValue(vn.getData().clone());
                    if (handleInput(binaryInput, "Edit Binary") == JOptionPane.OK_OPTION) {
                        vn.setData(binaryInput.getValue());
                        updateCurrentCell();
                    }
                    break;
                default:
                    // all other cell types - use the default JTable editor
                    // make this cell editable                
                    ((RegTableModel) cellRef.getTable().getModel())
                            .setCellEditable(cellRef.getRow(), cellRef.getCol(), true);
                    cellRef.getTable().editCellAt(cellRef.getRow(), cellRef.getCol());
                    break;
            }
        }
    }

    /**
     * Getter for parent frame's JTable
     *
     * @return
     */
    private JTable table() {
        return (frame.getTable());
    }

    /**
     * Getter for parent frame
     *
     * @return
     */
    @Override
    public MainFrame frame() {
        return (frame);
    }

    /**
     * Populate the JTable with values from the selected key node Will be
     * invoked when a cell node in the left-hand tree pane is selected
     *
     * @param node
     */
    public void populateTable(KeyNode node) {
        if (node.hasValues()) {
            ArrayList<ArrayList<Object>> data = new ArrayList<>();
            try {
                // iterate cell values and populate ArrayList
                for (ValueNode vn : node.values()) {
                    ArrayList<Object> row = new ArrayList<>();
                    row.add(vn.name());
                    row.add(vn.type().toString());
                    row.add(vn);
                    data.add(row);
                }
                // update table model with new data from ArrayList
                tableModel = new RegTableModel(data);
                // reload JTable with new data from table model
                table().setModel(tableModel);
                // set the default cell editor
                table().setDefaultEditor(String.class, defaultCellEditor);
                // set the custom editor
                table().setDefaultEditor(ValueNode.class, customCellEditor);
            } catch (RegistryException e) {
                frame.showErrorDialog(e.getMessage());
            }
        } else {
            // this node has no values, populate Jtable with empty model
            table().setModel(new DefaultTableModel());
        }
    }

    @Override
    public void updateCurrentCell() {
        controller.showTitle();
    }

    /**
     * Handle editing canceled event
     *
     * @param evt
     */
    @Override
    public void editingCanceled(final ChangeEvent evt) {
        if (evt.getSource() instanceof RegCellEditor) {
            final int row = ((RegCellEditor) evt.getSource()).getRow();
            final int column = ((RegCellEditor) evt.getSource()).getColumn();
            tableModel.setCellEditable(row, column, false);
        }
        inEdit = false;
    }

    /**
     * Handle editing stopped event Updates the registry value with edited data
     *
     * @param evt
     */
    @Override
    public void editingStopped(final ChangeEvent evt) {
        inEdit = false;
        // determine if value node name has been chnaged (column 0),
        // or actual data has been edited (column 2)
        if (evt.getSource() instanceof DefaultCellEditor) {
            // value name has been updated
            tableModel.setCellEditable(editingCell.row, editingCell.col, false);
            // create a new value node with the original value data
            ValueNode valueNode = (ValueNode) tableModel.getValueAt(editingCell.row, 2);
            // try to change value name
            // if the new value name is too long for the original value, 
            // a new registry value cell will be created
            try {
                valueNode.name(textField.getText());
            } catch (RegistryException e) {
                // something went wrong - display message
                frame.showErrorDialog(e.getMessage());
            }
        } else if (evt.getSource() instanceof RegCellEditor) {
            // value node data has been edited

            // get table row and column from event source
            RegCellEditor editor = (RegCellEditor) evt.getSource();
            final int row = editor.getRow();
            final int column = editor.getColumn();
            // make cell non-editable
            tableModel.setCellEditable(row, column, false);
            // create a value node with original cell data
            ValueNode valueNode = (ValueNode) tableModel.getValueAt(row, column);
            // try to update cell value with edited data
            // this might result in the value cell being moved to a new location in the registry
            if (valueNode.setData(editor.getField().getText())) {
                frame().showInfo("Element value updated");
            } else {
                // edited value is invalid for this cell data type
                frame().showInfo("Error: value " + editor.getField().getText() + " is invalid for this field");
            }
        }
        updateCurrentCell();    // changes the frame title to reflect hive changed
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    /**
     * Handle key press (delete key)
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (!inEdit) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                    handleDeleteDialog();   // callthe delete dialog
                    break;
            }
        }
    }

    /**
     * Show delete dialog
     *
     */
    public void handleDeleteDialog() {
        if (controller.getDeleteOptions().showDeleteDialog()) {
            deleteSelectedRow();
        }
    }

    /**
     * Delete the selected value node
     *
     */
    private void deleteSelectedRow() {
        // get the selected table row
        int row = frame.getTable().getSelectedRow();
        // get value node at selected row
        ValueNode vn = tableModel.getValueNode(row);
        try {
            if (vn != null) {
                // delete the value
                vn.delete(controller.getDeleteOptions().optWipe);
                // update table model
                tableModel.removeRow(row);
                frame.getTable().addNotify();
            }
        } catch (RegistryException e) {
            frame.showErrorDialog(e.getMessage());
        }
        controller.showTitle();
    }

    /**
     * Show value node properties
     *
     */
    public void showProperties() {
        if (table().getSelectedRow() >= 0) {
            PropertiesHandler ph = new PropertiesHandler(this, ((ValueNode) tableModel.getValueAt(table().getSelectedRow(), 2)).vkCell());
        }
    }

}
