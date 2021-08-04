package registry;

import registry.Cell.RegistryCell;

/**
 *
 * Simple interface for a hive function that accepts and modifies a registry cell
 */
public interface HiveFunction {
    
    // return value: true = break loop, false = continue loop
    public boolean process(RegistryCell cell);
    
}
