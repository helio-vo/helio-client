package eu.heliovo.clientapi.config.des;

/**
 * Argument to be used in a function
 * @author MarcoSoldati
 *
 */
public class DesFunctionArgument {
    /**
     * The supported operators
     * @author MarcoSoldati
     *
     */
    public enum DesFunctionOperator {
        /**
         * Less than
         */
        LT,
        /**
         * Equals
         */
        EQ,
        /**
         * Greater than
         */
        GT
    }
    
    private final String name;
    private final DesFunctionOperator[] operators;
    
    private DesFunctionArgumentCompound compound;
    
    public DesFunctionArgument(String name, DesFunctionOperator ... operators) {
        this.name = name;
        this.operators = operators;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the operators
     */
    public DesFunctionOperator[] getOperators() {
        return operators;
    }
    
    public DesFunctionArgumentCompound getCompound() {
        return compound;
    }
    
    public void setCompound(DesFunctionArgumentCompound compound) {
        this.compound = compound;
    }
    
}
