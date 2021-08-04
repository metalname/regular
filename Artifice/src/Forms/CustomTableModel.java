package Forms;

import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author 
 */
public class CustomTableModel implements TableModel {

    private final ArrayList<TableModelListener> tableModelListeners = new ArrayList<>();
    private final TableData data;
    private final String[] names;
    private final boolean[][] editFlags;

    public CustomTableModel(TableData data, String[] names) {
        this.data = data;
        this.names = names;
        int rows = data.size();
        var columns = 0;
        if (data.size() > 0) {
            columns = data.get(0).size();
        } else {
            columns = 1;
        }
        editFlags = new boolean[rows][columns];
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
    
    public void removeRow(int index) {
        if ((index >= 0) && (index < data.size())) {
            data.remove(index);
        }
    }
}
