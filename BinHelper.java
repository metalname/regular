/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer;

import java.nio.ByteOrder;

/**
 *
 * @author 
 */
public interface BinHelper {

    public abstract ByteOrder order();

    public abstract void order(ByteOrder order);

    public abstract void position(int position);

    public abstract int position();

    public abstract boolean hasRemaining();

    public abstract byte get();

    public abstract int size();

    public abstract void put(byte b);

    default public void put(byte[] b) {
        put(b, b.length);
    }

    default public void get(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            b[i] = get();
        }
    }
    
    default public void put (byte[] b, int size) {
        for (int i = 0; i < size; i++) {
            put(b[i]);
        }        
    }

    default public byte getByte() {
        return(get());
    }
    
    default public void putByte(byte b) {
        put(b);
    }
    
    default public void putInt(int num) {
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            putIntLE(num);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            putIntBE(num);
        }
    }

    default public void putIntLE(int num) {
        for (int i = 0; i < 4; i++) {
            put((byte) (num & 0xff));
            num = num >> 8;
        }
    }

    default public void putIntBE(int num) {
        byte[] b = {0, 0, 0, 0};
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (num & 0xff);
            num = num >> 8;
        }
        put(b);
    }

    default public int getInt() {
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            return (getIntLE());
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (getIntBE());
        }
        return (0);
    }

    default public int getIntLE() {
        byte[] b = {0, 0, 0, 0};
        get(b);
        int num = 0;
        for (int i = 3; i >= 0; i--) {
            num = (num << 8) + ((b[i] & 0xff));
        }
        return (num);
    }

    default public int getIntBE() {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            num = (num << 8) + ((get() & 0xff));
        }
        return (num);
    }

    default public void putShort(short num) {
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            putShortLE(num);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            putShortBE(num);
        }
    }

    default public void putShortLE(short num) {
        for (int i = 0; i < 2; i++) {
            put((byte) (num & 0xff));
            num = (short) (num >> 8);
        }
    }

    default public void putShortBE(short num) {
        byte[] b = {0, 0};
        get(b);
        for (int i = 1; i >= 0; i--) {
            put((byte) (num & 0xff));
            num = (short) (num >> 8);
        }
    }

    default public short getShort() {
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            return (getShortLE());
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (getShortBE());
        }
        return (0);
    }

    default public short getShortLE() {
        short num = (short) (get() & 0xff);
        num += (get() & 0xff) << 8;
        return (num);
    }

    default public short getShortBE() {
        short num = (short) ((get() & 0xff) << 8);
        num += (short) (get() & 0xff);
        return (num);
    }

    default public void putLong(long num) {
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            putLongLE(num);
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            putLongBE(num);
        }
    }

    default public void putLongLE(long num) {
        for (int i = 0; i < 8; i++) {
            put((byte) (num & 0xff));
            num = num >> 8;
        }
    }

    default public void putLongBE(long num) {
        byte[] b = {0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 7; i >= 0; i--) {
            b[i] = ((byte) (num & 0xff));
            num = num >> 8;
        }
        put(b);
    }

    default public long getLong() {
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            return (getLongLE());
        } else if (order() == ByteOrder.BIG_ENDIAN) {
            return (getLongBE());
        }
        return (0);
    }

    default public long getLongLE() {
        byte[] b = {0, 0, 0, 0, 0, 0, 0, 0};
        get(b);
        long num = 0;
        for (int i = 7; i >= 0; i--) {
            num = (num << 8) + ((b[i] & 0xff));
        }
        return (num);
    }

    default public long getLongBE() {
        long num = 0;
        for (int i = 0; i < 8; i++) {
            num = (num << 8) + ((get() & 0xff));
        }
        return (num);
    }

    default public char getChar() {
        return ((char) getShort());
    }

    default public void putChar(char c) {
        putShort((short) c);
    }

    // write ASCII string with specified length
    default public void putAsciiStringL(String s, int length) {
        for (int i = 0; i < length; i++) {
            put((byte) s.charAt(i));
        }
    }

    default public void putAsciiStringL(String s) {
        putAsciiStringL(s, s.length());
    }

    // read ASCII string with specified length
    default public String getAsciiStringL(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) get());
        }
        return (sb.toString());
    }

    // write UNICODE string with specified length
    default public void putUnicodeStringL(String s, int length) {
        for (int i = 0; i < length; i++) {
            putChar(s.charAt(i));
        }
    }

    default public void putUnicodeStringL(String s) {
        putUnicodeStringL(s, s.length());
    }

    // read UNICODE string with specified length
    default public String getUnicodeStringL(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(getChar());
        }
        return (sb.toString());
    }

    // write zero-terminated ASCII string
    // of maximum length (0 for unlimited)
    default public void putAsciiStringZ(String s, int maxlength) {
        if (maxlength == 0) {
            for (int i = 0; i < s.length(); i++) {
                put((byte) s.charAt(i));
            }
            put((byte) 0);
        } else {
            for (int i = 0; i < maxlength; i++) {
                if (i < s.length()) {
                    put((byte) s.charAt(i));
                } else {
                    put((byte) 0);
                }
            }
        }
    }

    // read zero-terminated ASCII string
    // up to maxlength characters (unlimited if 0)
    default public String getAsciiStringZ(int maxlength) {
        StringBuilder sb = new StringBuilder();
        byte b = 0;
        do {
            if ((maxlength > 0) && (sb.length() >= maxlength)) {
                break;
            }
            b = get();
            if (b != 0) {
                sb.append((char) b);
            }
        } while (b != 0);
        return (sb.toString());
    }

    // write zero-terminated ASCII string
    // of maximum length (0 for unlimited)
    default public void putUnicodeStringZ(String s, int maxlength) {
        if (maxlength == 0) {
            for (int i = 0; i < s.length(); i++) {
                putChar(s.charAt(i));
            }
            putChar((char) 0);
        } else {
            for (int i = 0; i < maxlength; i++) {
                if (i < s.length()) {
                    putChar(s.charAt(i));
                } else {
                    putChar((char) 0);
                }
            }
        }
    }

    // read zero-terminated ASCII string
    // up to maxlength characters (unlimited if 0)
    default public String getUnicodeStringZ(int maxlength) {
        StringBuilder sb = new StringBuilder();
        char c = 0;
        do {
            if ((maxlength > 0) && (sb.length() >= maxlength)) {
                break;
            }
            c = getChar();
            if (c != 0) {
                sb.append(c);
            }
        } while (c != 0);
        return (sb.toString());
    }

}
