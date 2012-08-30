package eu.heliovo.clientapi.config;

import java.beans.PropertyDescriptor;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Handler for the configuration of a specific property.
 * @author MarcoSoldati
 *
 */
public interface HelioPropertyHandler {

    /**
     * Get the name of the associated service. Must not be null.
     * @return the service name. 
     */
    public HelioServiceName getHelioServiceName();
    
    /**
     *  Get the optional service variant name
     * @return the variant name
     */
    public String getServiceVariant();
    
    /**
     * Get the name of the handled property
     * @return the the property name
     */
    public String getPropertyName();
    
    /**
     * Init the property handler. Must be called before the property descriptor is loaded.
     * Can be handled by Spring.
     */
    public void init();

    /**
     * Get the descriptor for a specific property.
     * @param serviceClass Instance of the current service.
     * @return the property descriptor, potentially read from a cache.
     */
    public PropertyDescriptor getPropertyDescriptor(Class<? extends HelioService> serviceClass);
}
