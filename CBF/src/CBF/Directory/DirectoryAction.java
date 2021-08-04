/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.Directory;

/**
 *
 * Functional interface for action to be performed at each tree node
 */
public interface DirectoryAction {
    
    public void performAction(DirectoryNode node);
    
}
