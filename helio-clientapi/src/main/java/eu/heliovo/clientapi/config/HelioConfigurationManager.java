package eu.heliovo.clientapi.config;

import java.beans.PropertyDescriptor;

import eu.heliovo.registryclient.HelioServiceName;

/**
 * Helper bean to read the configuration for several property descriptors. 
 * @author MarcoSoldati
 *
 */
public interface HelioConfigurationManager {
    /**
     * Get a specific property for a given service
     * @param serviceName the name of the service. Must not be null.
     * @param serviceVariant the variant. May be null.
     * @param propertyName the actual name of the property.
     * @param beanClass the bean class. Must not be null.
     * @return a property descriptor for the property. May be null in case no corresponding property descriptor has been found.
     */
    public PropertyDescriptor getPropertyDescriptor(HelioServiceName serviceName, String serviceVariant, String propertyName, Class<?> beanClass);
    
}
