package RegTableHandler;

import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import registry.Nodes.ValueNode;

/**
 *
 * Implements a table model for registry values
 */
public class RegTableModel implements TableModel {

    private ArrayList<TableModelListener> tableModelListeners = new ArrayList<>();
    private ArrayList<ArrayList<Object>> data = new ArrayList<>();
    private final String[] names = {"Name", "Type", "Value"};
    private final boolean[][] editFlags;

    /** 
     * Constructor
     * 
     * Reads data from supplied list of lists
     * @param data
     */
    public RegTableModel(ArrayList<ArrayList<Object>> data) {
        this.data = data;
        editFlags = new boolean[data.size()][data.get(0).size()];
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModelListeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableModelListeners.remove(l);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (data.get(0).get(columnIndex).getClass());
    }

    @Override
    public void setValueAt(Object o, int row, int column) {
        data.get(row).set(column, o);
    }

    @Override
    public Object getValueAt(int row, int column) {
        return (data.get(row).get(column));
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return (editFlags[row][column]);
    }

    @Override
    public String getColumnName(int column) {
        return (names[column]);
    }

    @Override
    public int getColumnCount() {
        return (names.length);
    }

    @Override
    public int getRowCount() {
        return (data.size());
    }

    public void setCellEditable(int row, int column, boolean editFlag) {
        editFlags[row][column] = editFlag;
    }
    
    public ValueNode getValueNode(int row) {
        if ((row >= 0) && (row < data.size())) {
            return((ValueNode) data.get(row).get(2));
        } else {
            return(null);
        }
    }
    
    public void removeRow(int row) {
        if ((row >= 0) && (row < data.size())) {
            data.remove(row);
        }    
    }
}
