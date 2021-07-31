package registry.Verify;

/**
 *
 * @author 
 */
public class OffsetRange {
    
    private final int startOffset, endOffset;
    
    public OffsetRange(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }
    
    public int startOffset() {
        return(startOffset);
    }
    
    public int endOffset() {
        return(endOffset);
    }
    
    public int size() {
        return(endOffset - startOffset);
    }
    
    public boolean contains(int offset) {
        return((offset >= startOffset) && (offset <= endOffset));
    }
    
}
