/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dialogs;

import java.awt.GridLayout;
import javax.swing.JFileChooser;

/**
 *
 * @author 
 */
public class RootChooserPanel extends ChooserPanel {

    private JFileChooser chooser;

    public RootChooserPanel() {
        super();
        setLayout(new GridLayout(0, 1));
        init();
    }

    private void init() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("/"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        add(chooser);
    }

    @Override
    public String getTitle() {
        return ("Select Root");
    }

    @Override
    public String getSelected() {
        String root = chooser.getCurrentDirectory().getAbsolutePath();
        if ((root.charAt(root.length()-1)) != '/') {
            root = root + '/';
        }
        return (root.replace('\\', '/'));
    }
    
    private static final long serialVersionUID = -1069880277100087064L;

}
