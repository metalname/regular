package registry.element;

/**
 *
 * NK cell flags data element
 */
public class NkFlagsDataElement extends ShortDataElement {

    protected static final int NKF_CompressedName = 0x0020;

    protected static final String[] flags = {
        " NKF_None ", " NKF_Unused ", " NKF_HiveExit ", " NKF_HiveEntry ", " NKF_NoDelete ", " NKF_SymbolicLink ",
        " NKF_CompressedName ", " NKF_PredefinedHandle ", " NKF_VirtMirrored ", " NKF_VirtTarget ", " NKF_VirtualStore "
    };

    public NkFlagsDataElement(final ElementType type,
            final int length,
            final int offset,
            final String label,
            final boolean follow) {
        super(type, length, offset, label, follow);
    }

    public static String[] getFlags() {
        return (flags);
    }

    public short getFlagsInt() {
        return (value);
    }

    @Override
    public String toString() {
        return(stringFromFlags((int) value));
    }
    
    public String stringFromFlags(int flagInt) {
        StringBuilder sb = new StringBuilder();
        int j = 1;
        for (int i = 1; i < flags.length; i++) {
            if ((flagInt & j) != 0) {
                if (sb.length() > 0) {
                    sb.append("|");
                }
                sb.append(flags[i]);
            }
            j *= 2;
        }
        return (sb.toString());        
    }

    public boolean isCompressed() {
        return ((value & NKF_CompressedName) != 0);
    }

    // set data from string
    // data assumed to be in the format " [flag] | [flag]"
    @Override
    public boolean setData(String data) {
        int newValue = flagsFromString(data);
        if (newValue == -1) {
            return (false);
        } else {
            value = (short) newValue;
            return (true);
        }
    }
    
    public void setData(int value) {
        this.value = (short) value;
    }
    
    public int getValue() {
        return((int) value);
    }

    public static int flagsFromString(String data) {
        final StringBuffer sb = new StringBuffer();
        short newValue = 0;
        for (int i = 0; i < data.length(); i++) {
            char c = Character.toUpperCase(data.charAt(i));
            if (c == '|') {
                // try to match flag value
                int v = getFlagValue(sb.toString());
                if (v != -1) {
                    newValue += v;
                    sb.setLength(0);
                } else {
                    return (-1);
                }
            } else {
                // append character to string, ignore spaces
                if (c != ' ') {
                    sb.append(c);
                }
            }
        }
        if (sb.length() > 0) {
            int v = getFlagValue(sb.toString());
            if (v != -1) {
                newValue += v;
            } else {
                return (-1);
            }
        }
        return ((int) newValue);
    }

    // return bit value for flag
    // -1 if unmatched
    protected static int getFlagValue(final String str) {
        for (int i = 0; i < flags.length; i++) {
            if (flags[i].equals(str)) {
                return (2 ^ i);
            }
        }
        return (-1);
    }
}
