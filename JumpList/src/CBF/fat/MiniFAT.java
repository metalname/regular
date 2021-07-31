/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.fat;

import BinBuffer.Unpacker.Unpacker;
import BinBuffer.Var.NumericVar;
import BinBuffer.Var.VarArray;
import BinBuffer.VarSet.VarSet;
import CBF.file.FileBuffer;
import CBF.sector.DataSector;
import CBF.sector.MiniDataSector;
import utils.NumFormat;

/**
 *
 * @author 
 */
public class MiniFAT implements FAT {
    
    private final FileBuffer buffer;
    private final VarSet vs;
    public static final int freeEntry = -1;
    
    public MiniFAT(FileBuffer buffer) {
        this.buffer = buffer;
        int n = buffer.size() / DataSector.FATEntrySize;
        vs = Unpacker.UnpackVarSet(buffer, "int[" + n + "]");
        vs.load();
    }
    
    @Override
    public int getFATEntry(int index) {
        return(((NumericVar) ((VarArray) vs.getVar(0)).getElement(index)).getNumAsInt());
    }
    
    @Override
    public void setFATEntry(int index, int entry) {
        ((NumericVar) ((VarArray) vs.getVar(0)).getElement(index)).setNumAsInt(entry);
    }

    @Override
    public int getNumEntries() {
        return(buffer.size() / DataSector.FATEntrySize);
    }
    
    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumEntries() / 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(NumFormat.numToHex(getFATEntry(i * 16 + j)));
            }
            if (sb.length() > 0) {
                sb.append('\n');
            }
        }
        return(sb.toString());
    }
    
    @Override
    public int getNumSectors() {
        return(buffer.maxSize() / MiniDataSector.secSize);
    }
    
    public FileBuffer getBuffer() {
        return(buffer);
    }
}
