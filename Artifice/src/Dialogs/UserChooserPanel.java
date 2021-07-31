/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogs;

import java.awt.GridLayout;
import javax.swing.JList;

/**
 *
 * @author 
 */
public class UserChooserPanel extends ChooserPanel {
    
    private final JList<String> userList;
    private final String drive;
    
    public UserChooserPanel(String drive, String[] users) {
        super();
        this.drive = drive;
        setLayout(new GridLayout(0, 1));
        userList = new JList<>(users);
        add(userList);
    }
    
    @Override
    public String getTitle() {
        return("Select User");
    }

    
    @Override
    public String getSelected() {
        return(userList.getSelectedValue());
    }
    
    public JList<String> getList() {
        return(userList);
    }

    private static final long serialVersionUID = 8601676645258498826L;
    
}
