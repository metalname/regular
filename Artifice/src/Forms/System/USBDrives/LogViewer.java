/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;

/**
 *
 * @author 
 */
public class LogViewer extends JDialog {
    private static final long serialVersionUID = -6846679994263763050L;
    
    private final SystemUSBDrivesHandler handler;
    private final Section range;
    private final LogFile logFile;
    private final String title = "Log Excerpt";
    private JPanel panel;
    private JTextPane textPane;
    private SimpleAttributeSet attr;
    private JButton btOK;
    
    public LogViewer (SystemUSBDrivesHandler handler, Section range, LogFile logFile) {
        super(handler.frame());
        this.handler = handler;
        this.range = range;
        this.logFile = logFile;
        initComponents();
        setVisible(true);
    }
    
    /*
    private void initComponents() {
        setLayout(new BorderLayout());
        buildPanel();
        add(panel, BorderLayout.CENTER);
        
        btOK = new JButton("OK");
        btOK.addActionListener(e -> {
            dispose();
        });
        add(btOK, BorderLayout.AFTER_LAST_LINE);
        
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        pack();        
    }
    */
        
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
        buildPanel();
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
    
    private void buildPanel() {
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setLayout(new BorderLayout());
        
        textPane = new JTextPane();
        StringBuilder sb = new StringBuilder();
        for (int line = range.start(); line <= range.end(); line++) {
            sb.append(line).append(": ").append(logFile.get(line)).append("\n");
        }
        attr = new SimpleAttributeSet();
        textPane.setText(sb.toString());
        textPane.setEditable(false);
        JScrollPane sp = new JScrollPane(textPane);
        sp.setViewportView(textPane);
        textPane.setCaretPosition(0);
        panel.setPreferredSize(new Dimension(1000, 400));
        panel.add(sp, BorderLayout.CENTER);
    }
}
