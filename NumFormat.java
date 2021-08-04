/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

public class NumFormat {

    protected final static String hexChars = "0123456789abcdef";

    public static String numToHex(byte b) {
        return ("" + hexChars.charAt((b & 0xf0) >> 4) + hexChars.charAt((b & 0xf)));
    }

    public static String numToHex(short s) {

        byte l = (byte) ((s & 0xff));
        byte u = (byte) ((s & 0xff00) >> 8);
        return ("" + numToHex(u) + numToHex(l));
    }

    public static String numToHex(int i) {

        String s = "";

        for (int index = 0; index < 4; index++) {
            byte b = (byte) (i & 0xff);
            s = numToHex(b) + s;
            i = i >>> 8;
        }

        return (s);
    }
    
    public static String numToHex(long l) {

        String s = "";

        for (int index = 0; index < 8; index++) {
            byte b = (byte) (l & 0xff);
            s = numToHex(b) + s;
            l = l >>> 8;
        }

        return (s);
    }
    
    public static String stripLeadingZeros(String s) {
        int i;
        for (i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0') {
                break;
            }
        }
        return(s.substring(i, s.length()));
    }

}
