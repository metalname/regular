/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.fat;

/**
 *
 * @author 
 */
public interface FAT {
    
    public int getFATEntry(int index);
    public void setFATEntry(int index, int entry);
    public int getNumEntries();
    public String dump();
    public int getNumSectors();
    
}
