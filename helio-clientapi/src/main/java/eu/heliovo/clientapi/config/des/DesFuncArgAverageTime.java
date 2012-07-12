package eu.heliovo.clientapi.config.des;

import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.EQ;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * The average time function argument.
 * @author MarcoSoldati
 *
 */
public class DesFuncArgAverageTime extends DesFunctionArgument {
    
    /**
     * The timerange to search 
     */
    private int deltaT = 0; 
    
    /**
     * A compound for this function argument
     */
    DesFunctionArgumentCompound compound = new DesFunctionArgumentCompound("DELTAT", "*2");

    public DesFuncArgAverageTime() {
        super("AVERAGETIME", EQ);
    }
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] propertyDescriptors;
        try {
            propertyDescriptors = Introspector.getBeanInfo(getClass()).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException(e); // should not occur
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            
        } 
        // TODO Auto-generated method stub
        return super.getPropertyDescriptors();
    }
    
}
