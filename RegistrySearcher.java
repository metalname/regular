package registry;

import java.util.Stack;
import registry.Nodes.KeyList;
import registry.Nodes.KeyNode;

/**
 *
 * Defines a RegistrySearcher class
 */
public class RegistrySearcher {
    
    private boolean ignoreCase;                     // case sensitive flag
    private String searchText;                      // test to search for
    private Stack<SearchNode> searchStack;          // stack - maintains state of search
    private KeyList rootNodeArray = new KeyList();
    
    //*********************************************************************
    
    /**
     * Inner class
     */
    protected class SearchNode {

        public KeyList cellNodeArray;
        public int index;

        protected SearchNode(KeyList cellNodeArray, int index) {
            this.cellNodeArray = cellNodeArray;
            this.index = index;
        }

        protected KeyNode getLastNode() {
            return (cellNodeArray.get(index));
        }

        protected boolean nextIndex() {
            if (index < (cellNodeArray.size() - 1)) {
                index++;
                return (true);
            } else {
                return (false);
            }
        }
    }    
    
    //*********************************************************************
    
    /**
     * Constructor
     * @param root root key of registry
     * @throws RegistryException 
     */
    public RegistrySearcher(KeyNode root) throws RegistryException {
        rootNodeArray = root.children();
    }
    
    /**
     * Find first match
     * @param searchText
     * @param ignoreCase
     * @return matching key node
     * @throws RegistryException 
     */
    public KeyNode findFirst(String searchText, boolean ignoreCase) throws RegistryException {
        this.ignoreCase = ignoreCase;
        if (ignoreCase) {
            this.searchText = searchText.toLowerCase();
        } else {
            this.searchText = searchText;
        }
        searchStack = new Stack();
        searchStack.push(new SearchNode(rootNodeArray, -1));
        if (resumeSearch()) {
            return (searchStack.peek().getLastNode());
        } else {
            return (null);
        }
    }

    /**
     * Find next match
     * @return matching key node
     * @throws RegistryException 
     */
    public KeyNode findNext() throws RegistryException {
        if (resumeSearch()) {
            return (searchStack.peek().getLastNode());
        } else {
            return (null);
        }
    }

    protected boolean resumeSearch() throws RegistryException {
        boolean found = false;
        while (!found) {
            if (searchStack.peek().nextIndex()) {
                if (searchStack.peek().getLastNode().matches(searchText, ignoreCase)) {
                    found = true;
                } else {
                    if (searchStack.peek().getLastNode().children() != null) {
                        searchStack.push(new SearchNode(searchStack.peek().getLastNode().children(), -1));
                    }
                }
            } else {
                searchStack.pop();
                if (searchStack.empty()) {
                    return (false);
                }
            }
        }
        return (found);
    }    
}
