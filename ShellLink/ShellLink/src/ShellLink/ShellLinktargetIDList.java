package ShellLink;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Defines a ShellLinkTargetIDList
 */
public class ShellLinktargetIDList {

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Inner class - defines an element of the LinkIdList
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public class LinkIdItem {

        public final ByteBuffer buffer;
        public final int offset;
        public short itemIdSize;
        public byte[] itemIdData;

        public LinkIdItem(ByteBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
        }

        /**
         * Load the item from the buffer If the item size is zero, we've reached
         * the end of the list
         *
         * @return
         */
        public int load() {
            itemIdSize = buffer.getShort();
            if (itemIdSize != 0) {
                itemIdData = new byte[itemIdSize - 2];  // itemIdSize includes the two bytes that make up the size field
                buffer.get(itemIdData);
            }
            return (buffer.position());
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private final ByteBuffer buffer;
    private final int offset;
    private final int idListSize;
    private final ArrayList<LinkIdItem> idArray = new ArrayList<>();
    
    /**
     * Constructor
     *
     * @param buffer
     * @param offset
     * @param idListSize
     * @throws ShellLink.ShellLinkException
     */
    public ShellLinktargetIDList(ByteBuffer buffer, int offset, int idListSize) throws ShellLinkException {
        this.buffer = buffer;
        this.offset = offset;
        this.idListSize = idListSize;
        load();
    }

    /**
     * Load the id list
     * The final buffer position should be equal to initial offset + idListSize
     * If not, raise exception
     * @throws ShellLink.ShellLinkException
     */
    private void load() throws ShellLinkException {
        buffer.position(offset);
        var position = offset;
        while (position < (offset + idListSize)) {
            var entry = new LinkIdItem(buffer, position);
            position = entry.load();
            idArray.add(entry);
        }
         if (buffer.position() != (offset + idListSize)) {
             throw new ShellLinkException("Misaligned idList structure");
         }
    }
    
    /**
     * Getter for idArray
     * @return 
     */
    public List<LinkIdItem> getIdArray() {
        return(idArray);
    }

}
