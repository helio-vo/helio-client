package eu.heliovo.clientapi.config;

import java.beans.PropertyDescriptor;
import java.util.List;

import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Helper bean to read the configuration for several property descriptors. 
 * @author MarcoSoldati
 *
 */
public interface HelioConfigurationManager {
    
    /**
     * Get the list of catalogue descriptors for a given service.
     * The catalogue descriptor contains the available catalogues and their configuration.
     * @param serviceName the name of the service. Must be a HELIO query service. Must not be null.
     * @param serviceVariant the variant name. Ignored if null.
     * @return the list of catalog descriptor or and empty list if not available.
     */
    public List<? extends HelioCatalogueDescriptor> getCatalogueDescriptors(HelioServiceName serviceName, String serviceVariant);

    /**
     * Get the property descriptor for a given service.
     * @param serviceName the name of the service. Must not be null.
     * @param serviceVariant the variant name. Ignored if null.
     * @param serviceClass the according service class. Must not be null.
     * @param propertyName the name of the property to load. Must not be null.
     * @return a property descriptor. May be null if the property is not contained in the serviceClass.
     */
    public PropertyDescriptor getPropertyDescriptor(HelioServiceName serviceName, String serviceVariant, Class<? extends HelioService> serviceClass, String propertyName);

    /**
     * Get a specific property for a given service
     * @param helioService the new service. Must not be null.
     * @param propertyName the name of the property to lookup.
     * @return a property descriptor for the property. May be null in case no corresponding property descriptor has been found.
     */
    public PropertyDescriptor getPropertyDescriptor(HelioService helioService, String propertyName);
}
