/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import Forms.AbstractKeyListener;
import Forms.CellRef;
import Forms.CustomTableModel;
import Forms.System.USBDrives.DriveRecord.KeyEntry;
import Forms.TableData;
import Forms.TableMouseListener;
import Forms.TableRow;
import InputHelpers.InputHandler;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import registry.Nodes.KeyNode;
import registry.RegistryException;
import utils.WindowsDate;

/**
 *
 * @author 
 */
public class KeysPopupHandler extends JDialog implements AbstractKeyListener, TableMouseListener {

    private static final long serialVersionUID = 7999290747475917932L;

    private final SystemUSBDrivesHandler handler;
    private final DriveRecord driveRecord;
    private final String title = "USB Registry Keys";
    private JPanel panel;
    private JButton btOK;
    private JTable table;
    private CustomTableModel model;
    private int panelWidth = 0, panelHeight = 0;
    private InputHandler inputHandler;

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    private class KeyDate {

        private Date date;

        public KeyDate(Date date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return (df.format(date));
        }
        
        public Date date() {
            return(date);
        }
        
        public void date(Date date) {
            this.date = date;
        }
    }

    public KeysPopupHandler(SystemUSBDrivesHandler handler, DriveRecord driveRecord) {
        super(handler.frame());
        this.handler = handler;
        this.driveRecord = driveRecord;
        this.setTitle(title + " for " + driveRecord.name());
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        buildTable();
        add(panel, gbc);

        btOK = new JButton("OK");
        btOK.addActionListener(e -> {
            dispose();
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(btOK, gbc);

        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        pack();
    }

    private class PathCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1701871334010128740L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setToolTipText(c.getText());
            return c;
        }
    }

    private void buildTable() {
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setLayout(new BorderLayout());
        table = new JTable();
        TableData data = new TableData();
        for (DriveRecord.KeyEntry keyEntry : driveRecord.keyEntries()) {
            TableRow row = new TableRow();
            row.add(new KeyDate(new WindowsDate(keyEntry.keyNode.timestamp()).toDate()));
            row.add(keyEntry);
            data.add(row);
        }
        String[] columns = {"Timestamp", "Path"};
        model = new CustomTableModel(data, columns);
        table.setModel(model);
        table.addKeyListener(this);
        table.setDefaultRenderer(KeyEntry.class, new PathCellRenderer());
        table.addMouseListener(this);
        table.setCellSelectionEnabled(true);
        autoSizePanel(300);
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane(table);
        jScrollPane1.setViewportView(table);
        panel.setPreferredSize(new Dimension(panelWidth + (panel.getInsets().left * 2), panelHeight + (panel.getInsets().top * 2)));
        panel.add(jScrollPane1, BorderLayout.CENTER);
    }

    private void autoSizePanel(int maxWidth) {
        int height = 0;
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            height = 0;

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + (table.getIntercellSpacing().width * 2);
                height += c.getPreferredSize().height + (table.getIntercellSpacing().height * 2);
                preferredWidth = Math.max(preferredWidth, width);
                //preferredWidth = width;

                //We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
            tableColumn.setMinWidth(preferredWidth);
            panelWidth += preferredWidth;
            if (height > panelHeight) {
                panelHeight = height;
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                deleteCurrentRow();
                break;
        }
    }

    private void deleteCurrentRow() {
        try {
            int row = table.getSelectedRow();
            if (row >= 0) {
                driveRecord.keyEntries().get(row).delete();
                //driveRecord.keyEntries().get(row).keyNode.delete(true);
                model.removeRow(row);
                table.addNotify();
                handler.showInfo();
            }
        } catch (RegistryException e) {
            handler.frame().showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void singleLeftClick(CellRef cellRef) {
        editCell(cellRef);
    }

    private void editCell(CellRef cellRef) {
        if (cellRef.getCol() == 0) {
            if (cellRef.getValue() instanceof KeyDate) {
                inputHandler().filetimeInput().setValue(((KeyDate) cellRef.getValue()).date());
                if (inputHandler.handleInput(inputHandler().filetimeInput(), "Edit Timestamp")) {
                    ((KeyDate) cellRef.getValue()).date(inputHandler.filetimeInput().getValue());
                    KeyNode node = ((KeyEntry) model.getValueAt(cellRef.getRow(), 1)).keyNode;
                    node.timestamp(inputHandler.filetimeInput().getValue());
                    handler.showInfo();
                };
            }
        }
    }

    private InputHandler inputHandler() {
        if (inputHandler == null) {
            inputHandler = new InputHandler(handler.frame());
        }
        return (inputHandler);
    }

}
