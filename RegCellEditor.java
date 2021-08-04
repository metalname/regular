package RegTableHandler;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import registry.Nodes.ValueNode;

/**
 * Implements a simple cell editor for registry value nodes
 */
public class RegCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = 3236362990767490384L;

    final protected JTextField field = new JTextField();
    protected ValueNode oldValue;
    protected int row, col;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if ((column == 2) && (value instanceof ValueNode)) {
            oldValue = (ValueNode) value;
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
