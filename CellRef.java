package Forms;

import java.awt.event.MouseEvent;
import javax.swing.JTable;

public class CellRef {

    protected final int row;
    protected final int col;
    protected final Object value;
    protected final JTable table;

    public CellRef(MouseEvent evt) {
        table = (JTable) evt.getSource();
        row = table.rowAtPoint(evt.getPoint());
        col = table.columnAtPoint(evt.getPoint());
        value = table.getModel().getValueAt(row, col);
    }

    public int getRow() {
        return (row);
    }

    public int getCol() {
        return (col);
    }

    public Object getValue() {
        return (value);
    }

    public JTable getTable() {
        return (table);
    }
}
