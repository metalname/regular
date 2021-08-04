/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

/**
 *
 * @author 
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Forms.TableForm;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author 
 */
public class DriveForm extends TableForm {
    private static final long serialVersionUID = 8573014801906521738L;
    
    protected JButton btKeyTimestamps;
    protected JButton btLogEntries;
    
    public DriveForm(String title, SystemUSBDrivesHandler handler) {
        super(title, handler);
    }
    
    @Override
    protected void addToolBar() {
        toolBar = new JToolBar();        
        addSaveButton();
        addKeyTimestampsButton();
        addLogEntriesButton();
        add(toolBar, BorderLayout.PAGE_START);        
    }
    
    protected void addKeyTimestampsButton() {
        btKeyTimestamps = new JButton();
        btKeyTimestamps.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Forms/resources/key.png"))); 
        btKeyTimestamps.setFocusable(false);
        btKeyTimestamps.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btKeyTimestamps.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btKeyTimestamps.addActionListener(this::propertiesButtonActionPerformed);
        btKeyTimestamps.setToolTipText("Registry Keys");
        toolBar.add(btKeyTimestamps);        
    }    
    
    protected void addLogEntriesButton() {
        btLogEntries = new JButton();
        btLogEntries.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Forms/resources/notepad.png"))); 
        btLogEntries.setFocusable(false);
        btLogEntries.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btLogEntries.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btLogEntries.addActionListener(this::logEntriesButtonActionPerformed);
        btLogEntries.setToolTipText("Logfile Entries");
        toolBar.add(btLogEntries);        
    }    
    
    public void propertiesButtonActionPerformed(ActionEvent e) {
        ((SystemUSBDrivesHandler) handler).showKeyTimestamps(e);
    }
    
    public void logEntriesButtonActionPerformed(ActionEvent e) {
        ((SystemUSBDrivesHandler) handler).showLogEntries(e);
    }
        
}

