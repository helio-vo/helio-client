package eu.heliovo.clientapi.model.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;


/**
 * Utility class to register the implementation for a helioServiceName-serviceVariant tuple.
 * The class is internally used in implementations of the {@link ServiceFactory}
 * @author MarcoSoldati
 *
 */
public class ServiceVariantRegistry {
    /**
     * the logger
     */
    private static final Logger _LOGGER = Logger.getLogger(ServiceVariantRegistry.class);
    
    /**
     * The internal registry
     */
    private final Map<HelioServiceName, Collection<ServiceVariant>> registry = new HashMap<HelioServiceName, Collection<ServiceVariant>>();
    
    /**
     * Create an empty registry
     */
    public ServiceVariantRegistry() {
    }
    
    /**
     * Register an implementation of a service for a given serviceName-serviceVariant-capability tripple. 
     * @param serviceName the name of the service. May be null as placeholder for all service names.
     * @param serviceVariant the service variant. May be null for the default variant.
     * @param capability the capability of this specific variant. May be null if a service has only one capability.
     * @param beanName name of the bean in the Spring configuration. Must not be null.
     * @throws IllegalArgumentException if such a service has been previously registered or if the first three arguments are all null.
     */
    public void register(HelioServiceName serviceName, String serviceVariant, ServiceCapability capability, String beanName) throws IllegalArgumentException {
        AssertUtil.assertArgumentNotNull(beanName, "beanName");
        if (serviceName == null && serviceVariant == null && capability == null) {
            throw new IllegalArgumentException("At least one of the following arguments must not be null: serviceName, serviceVariant, capability");
        }
        ServiceVariant variant = new ServiceVariant(serviceName, serviceVariant, capability, beanName);
        Collection<ServiceVariant> variants = registry.get(serviceName);
        if (variants == null) {
            variants = new HashSet<ServiceVariantRegistry.ServiceVariant>();
            registry.put(serviceName, variants);
        }
        if (variants.contains(variant)) {
            throw new IllegalArgumentException("Internal Error: ServiceVariant with service name '" + serviceName + 
                    "' and service variant '" + variant + "' has been previously registered.");
        }
        variants.add(variant);
    }
    
    
    /**
     * Get the implementation of a serviceName-serviceVariant-capability triple.
     * @param serviceName the name of the service. Must not be null.
     * @param serviceVariant the variant of the service. May be null for the default variant.
     * @param capability the capability of this specific variant. May be null if a service has only one capability.
     * @return the service that implements the serviceVariant or null if no matching service has been registered.
     * @throws ServiceResolutionException if service resolution fails.
     */
    public String getServiceImpl(HelioServiceName serviceName, String serviceVariant, ServiceCapability capability) throws ServiceResolutionException {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        Collection<ServiceVariant> variants = new HashSet<ServiceVariantRegistry.ServiceVariant>();
        Collection<ServiceVariant> tmp = registry.get(serviceName);
        if (tmp != null) {
            variants.addAll(tmp);
        }
        tmp = registry.get(null);
        if (tmp != null) {
            variants.addAll(tmp);
        }
        
        // find the match with the highest score
        int bestScore = 0;
        List<String> bestBean = new ArrayList<String>();
        
        for (ServiceVariant variant : variants) {
            // compute score per variant
            int score = 0; 
            score += score(serviceName, variant.serviceName);
            score += 3 * score(serviceVariant, variant.serviceVariant);
            score += 3 * score(capability, variant.capability);
            
            // and check the highest
            if (score > bestScore) {
                bestBean.clear();
                bestBean.add(variant.beanName);
                bestScore = score;
            } else if (score == bestScore) {
                bestBean.add(variant.beanName);                
            } 
        }
        if (bestBean.isEmpty()) {
            return null;
        } else {
            if (bestBean.size() > 1) {
                throw new ServiceResolutionException("Multiple implementations found for " +
                		"serviceName: '" + serviceName + "', serviceVariant: '" + serviceVariant + "', capability:'" + capability + 
                		":"  + bestBean.toString());
            }
            return bestBean.get(0);
        }
    }
    
    /**
     * Compute the score for a variant property.
     * <pre>
     * +-----------+--------------+-------+
     * | propInput | propRegistry | score |
     * +-----------+--------------+-------+
     * | null      | null         | 2     |
     * | null      | y            | 1     |
     * | x         | null         | 0     |
     * | x         | y            | -10   |
     * | x         | x            | 2     |
     * +-----------+--------------+-------+
     * </pre>
     * @param propInput the property submitted by the caller
     * @param propRegistry the property value from the registry
     * @return the score according to the above table
     * 
     */
    private int score(Object propInput, Object propRegistry) {
        int score = 0;
        if (propInput == null) {
            if (propRegistry == null) {
                score = 2;
            } else {
                score = 1;
            }
        } else {
            if (propRegistry == null) {
                score = 0;
            } else if (propInput.equals(propRegistry)) {
                score = 2;
            } else {
                score = -10;
            }
        }
        return score;
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
         * The capability of this variant.
         */
        private final ServiceCapability capability;
        
        /**
         * Name of the spring bean
         */
        private final String beanName;

        /**
         * Create the service variant
         * @param serviceName the name of the service. May be null.
         * @param serviceVariant the service variant. May be null.
         * @param capability the capability of this variant. May be null.
         * @param beanName name of the spring bean.
         */
        public ServiceVariant(HelioServiceName serviceName, String serviceVariant, ServiceCapability capability, String beanName) {
            this.serviceName = serviceName;
            this.serviceVariant = serviceVariant;
            this.capability = capability;
            this.beanName = beanName;
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ServiceVariant)) {
                return false;
            }
            ServiceVariant otherVariant = (ServiceVariant)other;
            return (this.serviceName == null ? otherVariant.serviceName == null    : this.serviceName.equals(otherVariant.serviceName)) && 
                (this.serviceVariant == null ? otherVariant.serviceVariant == null : this.serviceVariant.equals(otherVariant.serviceVariant)) && 
                (this.capability == null ? otherVariant.capability == null : this.capability.equals(otherVariant.capability));
        }
        
        @Override
        public int hashCode() {
            return (serviceName == null ? 0 : serviceName.hashCode()) 
                + 7 * (serviceVariant == null ? 0 : serviceVariant.hashCode())
                + 13 * (capability == null ? 0 : capability.hashCode());
        }
    }
}
