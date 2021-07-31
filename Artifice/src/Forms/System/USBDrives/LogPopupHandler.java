/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import Forms.CellRef;
import Forms.CustomTableModel;
import Forms.TableData;
import Forms.TableMouseListener;
import Forms.TableRow;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
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

/**
 *
 * @author 
 */
public class LogPopupHandler extends JDialog implements TableMouseListener {
    private static final long serialVersionUID = -7647362344052747875L;
    
    private final SystemUSBDrivesHandler handler;
    private final DriveRecord driveRecord;
    private final LogFile logFile;
    private final String title = "Logfile Entries";
    private JButton btOK;
    private JPanel panel;
    private JTable table;
    private CustomTableModel model;
    private int panelWidth = 0, panelHeight = 0;
    
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
        
    public LogPopupHandler(SystemUSBDrivesHandler handler, DriveRecord driveRecord, LogFile logFile) {
        super(handler.frame());
        setTitle(title + " " + logFile.name());
        this.handler = handler;
        this.driveRecord = driveRecord;
        this.logFile = logFile;
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
        autoSizePanel(400);
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
    
    private void buildTable() {
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setLayout(new BorderLayout());
        table = new JTable();
        TableData data = new TableData();
        for (Section range: driveRecord.ranges()) {
            TableRow row = new TableRow();
            row.add(range.start());
            row.add(range.end());
            row.add(range.date());
            row.add(logFile.get(range.start()));
            data.add(row);
        }
        String[] columns = {"Start", "End", "Date", "First Line"};
        model = new CustomTableModel(data, columns);
        table.setModel(model);
        table.setDefaultRenderer(String.class, new PathCellRenderer());
        table.addMouseListener(this);
        table.setCellSelectionEnabled(true);
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane(table);
        jScrollPane1.setViewportView(table);        
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
    
    @Override
    public void singleLeftClick(CellRef cellRef) {
        if (cellRef.getCol() == 3) {
            LogViewer logViewer = new LogViewer(handler, driveRecord.ranges().get(cellRef.getRow()), logFile);
        }
    }
    
}
