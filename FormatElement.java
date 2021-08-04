package registry.element;

/**
 *
 * Helper functions to format elements as hex
 */
public class FormatElement {
    
    private static final String hexChars = "0123456789abcdef";
    private static final StringBuilder sb = new StringBuilder();
    
    private static String prependZero(String s, int digits) {
        sb.setLength(0);
        for (int i = 0; i < digits - s.length(); i++) {
            sb.append('0');
        }
        sb.append(s);
        return(sb.toString());
    }
    
    public static String toHexString(int value) {
        return(prependZero(Integer.toHexString(value), 8));
    }
    
    public static String toHexString(long value) {
        return(prependZero(Long.toHexString(value), 16));
    }    
    
    public static String toHexString(short value) {
        return(prependZero(Integer.toHexString(value), 4));
    }       
    
    public static String toHexString(byte b) {
        char upper = hexChars.charAt((b & 0xf0) / 0x10);
        char lower = hexChars.charAt(b & 0x0f);
        return ("" + upper + lower);
    }    
}
