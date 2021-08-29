package ShellLink;

import java.nio.ByteBuffer;

/**
 *
 * Implement a VolumeID structure This structure conmtains information about the
 * volume that the link target was on at time of creation
 */
public class ShellLinkVolumeID {

    private final ByteBuffer buffer;
    private final int offset;

    private int volumeIDSize;
    private int driveType;
    private int driveSerialNumber;
    private int volumeLabelOffset;
    private int volumeLabelOffsetUnicode;
    private boolean hasUnicodeLabel = false;
    private String volumeLabel;

    public ShellLinkVolumeID(ByteBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        load();
    }

    private void load() {
        buffer.position(offset);

        volumeIDSize = buffer.getInt();
        driveType = buffer.getInt();
        driveSerialNumber = buffer.getInt();
        volumeLabelOffset = buffer.getInt();
        // volumeLabelOffsetUnicode is present only if the value of volumeLabelOffset is 0x14
        if (volumeLabelOffset == 0x14) {
            volumeLabelOffsetUnicode = buffer.getInt();
            hasUnicodeLabel = true;
        }
        readVolumeLabel();
    }

    private void readVolumeLabel() {
        int ch = 0;
        var sb = new StringBuilder();
        // position buffer
        buffer.position(offset + volumeLabelOffset);
        do {
            if (hasUnicodeLabel) {
                ch = buffer.getShort();
            } else {
                ch = buffer.get();
            }
            if (ch != 0) {
                sb.append((char) ch);
            }
        } while (ch != 0);
        volumeLabel = sb.toString();
    }

}
