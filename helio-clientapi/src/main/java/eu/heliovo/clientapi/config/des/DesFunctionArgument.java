package eu.heliovo.clientapi.config.des;

import java.beans.SimpleBeanInfo;

import eu.heliovo.clientapi.model.DomainValueDescriptor;

/**
 * Argument to be used in a function
 * @author MarcoSoldati
 *
 */
public class DesFunctionArgument extends SimpleBeanInfo {
    /**
     * The supported operators
     * @author MarcoSoldati
     *
     */
    public enum DesFunctionOperator implements DomainValueDescriptor<String> {
        /**
         * Less than
         */
        LT("<"),
        /**
         * Equals
         */
        EQ("="),
        /**
         * Greater than
         */
        GT(">");
        
        private final String symbol;

        private DesFunctionOperator(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String getValue() {
            return this.toString();
        }

        @Override
        public String getLabel() {
            return symbol;
        }

        @Override
        public String getDescription() {
            return null;
        }
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
