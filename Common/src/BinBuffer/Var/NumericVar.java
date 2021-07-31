/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BinBuffer.Var;

/**
 *
 * @author 0
 */
public interface NumericVar {
    public byte getNumAsByte();
    public short getNumAsShort();
    public int getNumAsInt();
    public long getNumAsLong();
    public void setNumAsInt(int num);
    public void setNumAsLong(long num);
}
