package Properties;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import registry.element.DataElement;

/**
 *
 * 
 * Editor for PropertiesHandler
 */
public class PropertiesCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = 3386725835131420116L;

    final protected JTextField field = new JTextField();
    protected DataElement oldValue;
    protected int row, col;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if ((column == 1) && (value instanceof DataElement)) {
            oldValue = (DataElement) value;
            this.row = row;
            this.col = column;
            field.setText(oldValue.toString());
            return (field);
        }
        return (null);
    }

    @Override
    public Object getCellEditorValue() {
        return (oldValue);
    }

    public int getRow() {
        return (row);
    }

    public int getColumn() {
        return (col);
    }

    public JTextField getField() {
        return (field);
    }
        
}
