package registry;

/**
 *
 * Subclass of Exception used to report registry errors
 */
public class RegistryException extends Exception {
    private static final long serialVersionUID = -8043423814085171163L;
    
    public RegistryException(String message) {
        super(message);
    }
    
}
