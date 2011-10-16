package eu.heliovo.clientapi.model.service;

import java.util.HashMap;
import java.util.Map;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;


/**
 * Utility class to register the implementation for a helioServiceName-serviceVariant tuple.
 * The class is internally used in implementations of the {@link ServiceFactory}
 * @author MarcoSoldati
 *
 */
public class ServiceVariantRegistry {

    /**
     * The internal registry
     */
    private final Map<ServiceVariant, Class<? extends HelioService>> registry = new HashMap<ServiceVariant, Class<? extends HelioService>>();
    
    /**
     * Create an empty registry
     */
    public ServiceVariantRegistry() {
    }
    
    /**
     * Register an implementation of a service for a given serviceName-serviceVariant tuple. 
     * @param serviceName the name of the service. May be null as placeholder for all service names.
     * @param serviceVariant the service variant. May be null for the default variant.
     * @param serviceImpl the implementing class. Must not be null.
     * @throws IllegalArgumentException if such a service has been previously registered.
     */
    public void register(HelioServiceName serviceName, String serviceVariant, Class<? extends HelioService> serviceImpl) throws IllegalArgumentException {
        ServiceVariant variant = new ServiceVariant(serviceName, serviceVariant);
        if (registry.containsKey(variant)) {
            throw new IllegalArgumentException("Internal Error: ServiceVariant with service name '" + serviceName + 
                    "' and service variant '" + variant + "' has been previously registered.");
        }
        AssertUtil.assertArgumentNotNull(serviceImpl, "serviceImpl");
        registry.put(variant, serviceImpl);
    }
    
    
    /**
     * Get the implementation of a serviceName-serviceVariant tuple.
     * @param serviceName the name of the service. Must not be null.
     * @param serviceVariant the variant of the service. May be null for the default variant.
     * @return the service that implements the serviceVariant or null if no service has been registered.
     */
    public Class<? extends HelioService> getServiceImpl(HelioServiceName serviceName, String serviceVariant) {
        ServiceVariant variant = new ServiceVariant(serviceName, serviceVariant);
        Class<? extends HelioService> impl = registry.get(variant);
        return impl;
    }
    
    /**
     * Object to hold a HelioServiceName-serviceVariant tuple.
     */
    private static class ServiceVariant {
        /**
         * name of the service
         */
        private final HelioServiceName serviceName;
        /**
         * variant name
         */
        private final String serviceVariant;

        /**
         * Create the service variant
         * @param serviceName the name of the service. May be null.
         * @param serviceVariant the service variant. May be null.
         */
        public ServiceVariant(HelioServiceName serviceName, String serviceVariant) {
            this.serviceName = serviceName;
            this.serviceVariant = serviceVariant;
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ServiceVariant)) {
                return false;
            }
            ServiceVariant otherVariant = (ServiceVariant)other;
            return (this.serviceName == null ? otherVariant.serviceName == null    : this.serviceName.equals(otherVariant.serviceName)) && 
                (this.serviceVariant == null ? otherVariant.serviceVariant == null : this.serviceVariant.equals(otherVariant.serviceVariant));
        }
        
        @Override
        public int hashCode() {
            return (serviceName == null ? 0 : serviceName.hashCode()) + 7 * (serviceVariant == null ? 0 : serviceVariant.hashCode());
        }
    }
}
