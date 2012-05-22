package eu.heliovo.clientapi.config.des;

/**
 * A compound that is connected to an argument
 * @author MarcoSoldati
 *
 */
public class DesFunctionArgumentCompound {

    private final String name;
    private final String operation;

    public DesFunctionArgumentCompound(String name, String operation) {
        this.name = name;
        this.operation = operation;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    
    
}
