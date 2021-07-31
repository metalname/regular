/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.sector;

import BinBuffer.BinBuffer;
import BinBuffer.Unpacker.Unpacker;
import BinBuffer.Var.NumericVar;
import BinBuffer.Var.VarArray;
import BinBuffer.VarSet.LabelVarSet;

/**
 *
 * @author 
 */
public class HeaderSector {
    
    final private static int numFatEntries = 109;
    
    private BinBuffer data;
    private LabelVarSet vs;
    
    public HeaderSector(DataSector ds) {
        this(ds.getData());
    }
    
    public HeaderSector(byte[] buffer) {
        data = new BinBuffer(buffer);
    }
    
    public void load() {
        vs = Unpacker.UnpackLabelVarSet(data, 
                "_abSig=bytes 8 _clid=bytes 16 _uMinorVersion=short _uDllversion=short _uByteOrder=short " +
                "_uSectorShift=short _uMiniSectorShift=short _usreserved=short _ulReserved=long " +
                "_csectFat = int _sectDirStart=int _signature=int _ulMiniSectorCutoff=int " +
                "_sectMiniFatStart=int _sectDifStart=int _csectDif=int _difatArray=int[" + numFatEntries + "]");
        vs.load();
    }
        
    public String dump() {
        return(vs.dump());
    }
    
    public int getCountFATSectors() {
        return(((NumericVar) vs.getVar("_csectFat")).getNumAsInt());
    }
    
    public int getFATSector(int index) {
        return(((NumericVar) ((VarArray) vs.getVar("_difatArray")).getElement(index)).getNumAsInt());
    }
    
    public int getDirectoryStartSector() {
        return(((NumericVar) vs.getVar("_sectDirStart")).getNumAsInt());
    }
    
    public int getMiniFATStartSector() {
        return(((NumericVar) vs.getVar("_sectMiniFatStart")).getNumAsInt());        
    }
    
    public int getMiniSectorCutoff() {
        return(((NumericVar) vs.getVar("_ulMiniSectorCutoff")).getNumAsInt());                
    }
    
}
