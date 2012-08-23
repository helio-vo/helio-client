package eu.heliovo.clientapi.config.service;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Factory to get specific implementations of a HelioService.
 * @author MarcoSoldati
 */
public interface ServiceFactory {
    /**
     * Get a new instance of the "best" service provider for a desired service.
     * @param serviceName the service name to look for. Must not be null.
     * @return a proxy to a service implementation.
     */
    public HelioService[] getHelioServices(HelioServiceName serviceName);    

    /**
     * Get a new instance of the "best" service provider for a desired service.
     * @param serviceName the service name to look for. Must not be null.
     * @param serviceVariant optional identifier for a variant of a service. If null, a default variant of the service will be returned. Not all services define variants. 
     * @return a proxy to a service implementation.
     */
    public HelioService[] getHelioServices(HelioServiceName serviceName, String serviceVariant);    
    
    /**
     * Get a new instance of the "best" service provider for a desired service.
     * @param serviceName the service name to look for. Must not be null.
     * @param serviceVariant optional identifier for a variant of a service. If null, a default variant of the service will be returned. Not all services define variants.
     * @param serviceCapability the capability offered by the service. May be null if a service has only one capability.
     * @param accessInterfaces list of access interface to use. If null, the registry will be queried for the best matching interface. 
     * If multiple interfaces are given, the {@link LoadBalancer} will be used to choose the best matching interface.
     * @return a proxy to a service implementation.
     */
    public HelioService[] getHelioServices(HelioServiceName serviceName, String serviceVariant, ServiceCapability serviceCapability);    

    /**
     * Get a new instance of all service provider for a desired service.
     * @param serviceName the service name to look for. Must not be null.
     * @param serviceVariant optional identifier for a variant of a service. If null, a default variant of the service will be returned. Not all services define variants.
     * @param serviceCapability the capability offered by the service. May be null if a service has only one capability.
     * @param accessInterfaces list of access interface to use. If null, the registry will be queried for the best matching interface. 
     * If multiple interfaces are given, the {@link LoadBalancer} will be used to choose the best matching interface.
     * @return a proxy to a service implementation.
     */
    public HelioService[] getHelioServices(HelioServiceName serviceName, String serviceVariant, ServiceCapability serviceCapability, AccessInterface... accessInterfaces);    
}