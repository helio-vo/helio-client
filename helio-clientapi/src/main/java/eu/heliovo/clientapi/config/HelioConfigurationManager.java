package eu.heliovo.clientapi.config;

import java.beans.PropertyDescriptor;

import eu.heliovo.clientapi.model.service.HelioService;

/**
 * Helper bean to read the configuration for several property descriptors. 
 * @author MarcoSoldati
 *
 */
public interface HelioConfigurationManager {

    /**
     * Get a specific property for a given service
     * @param helioService the new service. Must not be null.
     * @param propertyName the name of the property to lookup.
     * @return a property descriptor for the property. May be null in case no corresponding property descriptor has been found.
     */
    public PropertyDescriptor getPropertyDescriptor(HelioService helioService, String propertyName);
}
