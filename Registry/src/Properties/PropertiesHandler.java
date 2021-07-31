package Properties;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import InputHelpers.*;
import javax.swing.JFrame;
import registry.Cell.RegistryCell;
import registry.element.*;
import utils.WindowsDate;

/**
 *
 * Swing dialog for cell properties
 * Display properties as grid of property name and value
 * Allows editing of value on mouse click
 */
public class PropertiesHandler extends MouseAdapter implements TableModel, CellEditorListener {

    private JDialog dialog;
    private JFrame frame;
    private boolean[][] editFlags;
    
    // properties are stored as list of lists (2-dimensional array)
    // elements at column 0 are property names, column 1 is property value
    private ArrayList<ArrayList<Object>> data;
    
    private JTable table;
    private JPanel tablePanel;
    private final MessageHandler handler;
    private final RegistryCell cell;
    private final PropertiesCellEditor editor = new PropertiesCellEditor();

    // create input handlers for all four types of data values
    private final NkFlagsInput nkFlagsInput = new NkFlagsInput();
    private final FiletimeInput filetimeInput = new FiletimeInput();
    private final IntegerInput integerInput = new IntegerInput();
    private final BinaryInput binaryInput = new BinaryInput();

    /**
     * Constructor
     * 
     * @param handler class that implements MessageHandler interface
     * @param cell NK cell for which to edit properties
     */
    public PropertiesHandler(MessageHandler handler, RegistryCell cell) {
        this.handler = handler;
        this.cell = cell;
        createDialog();
    }

    /**
     * Property editor dialog
     */
    private void createDialog() {
        dialog = new JDialog(frame, "Edit Cell Properties for " + cell.toString(), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.insets = new Insets(5, 5, 5, 5);
        createTablePanel();     // creaes JPanel from cell properties
        dialog.add(tablePanel, gc);
        gc.gridwidth = 1;
        gc.gridy = 1;
        JButton btOK = new JButton("OK");
        btOK.addActionListener(e -> {
            dialog.dispose();
        });
        dialog.add(btOK, gc);
        gc.gridx = 1;
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Get data value at specified row and column
     * @param row
     * @param column
     * @return 
     */
    @Override
    public Object getValueAt(int row, int column) {
        return (data.get(row).get(column));
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        return (editFlags[row][col]);
    }

    public void setCellEditable(final int row, final int col, final boolean editable) {
        editFlags[row][col] = editable;
    }

    /**
     * Handle escape button
     * @param evt 
     */
    @Override
    public void editingCanceled(final ChangeEvent evt) {
        if (evt.getSource() instanceof PropertiesCellEditor) {
            final int row = ((PropertiesCellEditor) evt.getSource()).getRow();
            final int column = ((PropertiesCellEditor) evt.getSource()).getColumn();            
            setCellEditable(row, column, false);
        }
    }

    /**
     * Handle end of edit - update value
     * @param evt 
     */
    @Override
    public void editingStopped(final ChangeEvent evt) {
        if (evt.getSource() instanceof PropertiesCellEditor) {
            // get row and column value from event source
            final int row = ((PropertiesCellEditor) evt.getSource()).getRow();
            final int column = ((PropertiesCellEditor) evt.getSource()).getColumn();
            
            // disable editing on cell
            setCellEditable(row, column, false);
            
            // get new value from JTable cell
            DataElement element = (DataElement) this.getValueAt(row, column);       
            // attempt to update underlying element with new data
            if (element.setData(((PropertiesCellEditor) evt.getSource()).getField().getText())) {
                handler.updateCurrentCell();    // element update successful, update registry cell value
            } else {
                // element update was unsuccessful
                // should not happen because editors will only allow valid values
                handler.showInfo("Error: value " + ((PropertiesCellEditor) evt.getSource()).getField().getText() + " is invalid for this field");
            }
        }
    }

    /**
     * Creates a JPanel from the cell properties
     */
    private void createTablePanel() {
        tablePanel = new JPanel();
        buildTable();
        tablePanel.add(table);
        tablePanel.setBorder(BorderFactory.createEtchedBorder());
    }

    private void buildTable() {
        // data is the model for the JTable
        // list of lists (2-dimensional array)
        data = new ArrayList<>();

        // iterate over cell data elements
        for (DataElement element : cell.elements()) {
            ArrayList<Object> row = new ArrayList<>();
            row.add(element.getLabel());    // column 0 is property label
            row.add(element);               // column 1 is property value
            data.add(row);
        }
        
        // create JTable object and set value from 2-dimensional array
        table = new JTable();
        table.setModel(this);
        
        // add mouse listener
        table.addMouseListener(this);
        
        // add editor listener
        editor.addCellEditorListener(this);
        table.setDefaultEditor(DataElement.class, editor);       
        autoSizePanel();
        
        // set all edit flags to false initially
        editFlags = new boolean[data.size()][2];
    }

    /**
     * Auto size the panel based on data in 2-dimensional array
     */
    private void autoSizePanel() {
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener ml) {

    }

    @Override
    public void removeTableModelListener(TableModelListener ml) {

    }

    /**
     * Set data at specified row and column
     * 
     * @param o
     * @param row
     * @param column 
     */
    @Override
    public void setValueAt(Object o, int row, int column) {
        data.get(row).set(column, o);
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return ("Label");
            case 1:
                return ("Data");
            default:
                return (null);
        }
    }

    @Override
    public int getColumnCount() {
        return (2);
    }

    @Override
    public int getRowCount() {
        return (data.size());
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Inner class
    // Create a cell reference from a mouse event
    protected class CellRef {

        protected final int row;
        protected final int col;
        protected final DataElement element;
        protected final JTable table;

        public CellRef(MouseEvent evt) {
            table = (JTable) evt.getSource();
            row = table.rowAtPoint(evt.getPoint());
            col = table.columnAtPoint(evt.getPoint());
            
            // if this cell is a data element (meaning it's column 1),
            // store the element value
            if (table.getModel().getValueAt(row, col) instanceof DataElement) {
                element = (DataElement) table.getModel().getValueAt(row, col);
            } else {
                element = null;
            }
        }

        protected int getRow() {
            return (row);
        }

        protected int getCol() {
            return (col);
        }

        protected DataElement getElement() {
            return (element);
        }

        protected JTable getTable() {
            return (table);
        }

        protected boolean isDataElement() {
            return (element != null);
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    /**
     * Handle mouse click
     * 
     * @param evt 
     */
    @Override
    public void mouseClicked(final MouseEvent evt) {
        if (SwingUtilities.isLeftMouseButton(evt) && !(evt.isConsumed())) {
            // create a Cellref object from mouse event
            CellRef cellRef = new CellRef(evt);
            // If it's a data element, go into edit mode
            // otherwise, ignore mouse click
            if (cellRef.isDataElement()) {
                editCell(cellRef);
            }
        }
    }

    protected int handleInput(AbstractInput input, String title) {
        return (JOptionPane.showOptionDialog(frame, input,
                title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null));
    }

    /**
     * Edit the cell
     * 
     * @param cellRef 
     */
    private void editCell(final CellRef cellRef) {
        // create an instance of an edit handler depending on data type
        switch (cellRef.getElement().getType()) {
            case E_NKFLAGS:
                // show NkFlagsInput
                NkFlagsDataElement nkfe = (NkFlagsDataElement) cellRef.getElement();
                nkFlagsInput.setValue(nkfe.getValue());
                if (handleInput(nkFlagsInput, "Edit Flags") == JOptionPane.OK_OPTION) {
                    nkfe.setData(nkFlagsInput.getValue());
                    handler.updateCurrentCell();
                }
                break;
            case E_FILETIME:
                // show FiletimeInput                
                FiletimeDataElement fte = (FiletimeDataElement) cellRef.getElement();
                filetimeInput.setValue(new WindowsDate(fte.getValueL()).toDate());
                if (handleInput(filetimeInput, "Edit Timestamp") == JOptionPane.OK_OPTION) {
                    fte.setValueL(new WindowsDate(filetimeInput.getValue()).timestamp());
                    handler.updateCurrentCell();
                }
                break;
            case E_INT:
                // Show IntegerInput (type INTEGER)
                IntDataElement ie = (IntDataElement) cellRef.getElement();
                integerInput.setValue(ie.getValueI(), IntegerInput.I_INT);
                if (handleInput(integerInput, "Edit Number") == JOptionPane.OK_OPTION) {
                    if (integerInput.isValidNumber()) {
                        ie.setValueI((int) integerInput.getValue());
                        handler.updateCurrentCell();
                    } else {
                        handler.showInfo(integerInput.getValueString() + " is out of range for INT");
                    }
                }
                break;
            case E_SHORT:
                // show IntegerInput (type SHORT)
                ShortDataElement se = (ShortDataElement) cellRef.getElement();
                integerInput.setValue(se.getValueI(), IntegerInput.I_SHORT);
                if (handleInput(integerInput, "Edit Number") == JOptionPane.OK_OPTION) {
                    if (integerInput.isValidNumber()) {
                        se.setValueI((short) integerInput.getValue());
                        handler.updateCurrentCell();
                    } else {
                        handler.showInfo(integerInput.getValueString() + " is out of range for SHORT");
                    }
                }
                break;
            case E_LONG:
                // show IntegerInput (type LONG)
                LongDataElement le = (LongDataElement) cellRef.getElement();
                integerInput.setValue(le.getValueL(), IntegerInput.I_LONG);
                if (handleInput(integerInput, "Edit Number") == JOptionPane.OK_OPTION) {
                    if (integerInput.isValidNumber()) {
                        le.setValueL(integerInput.getValue());
                        handler.updateCurrentCell();
                    } else {
                        handler.showInfo(integerInput.getValueString() + " is out of range for LONG");
                    }
                }
                break;
            case E_BINARY:
                // show BinaryInput
                BinDataElement be = (BinDataElement) cellRef.getElement();
                binaryInput.setValue(be.getData().clone());
                if (handleInput(binaryInput, "Edit Binary") == JOptionPane.OK_OPTION) {
                    be.setData(binaryInput.getValue());
                    handler.updateCurrentCell();
                }
                break;
            default:
                // no specialized editor - open field for direct input
                // make this cell editable
                setCellEditable(cellRef.getRow(), cellRef.getCol(), true);
                //editingCell = cellRef;
                cellRef.getTable().editCellAt(cellRef.getRow(), cellRef.getCol());
                break;
        }
    }
    
}
