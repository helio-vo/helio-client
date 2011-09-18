package eu.heliovo.clientapi.model.service;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Factory to get specific implementations of a HelioService.
 * @author MarcoSoldati
 *
 */
public interface ServiceFactory {
    /**
     * Get a new instance of the "best" service provider for a desired service.
     * @param serviceName the service name to use.
     * @param serviceVariant optional identifier for a variant of a service. If null, a default variant of the service will be returned. Not all services define variants. 
     * @param accessInterfaces list of access interface to use. If null, the registry will be queried for the best matching interface. 
     * If multiple interfaces are given, the {@link LoadBalancer} will be used to choose the best matching interface.
     * @return a Service implementation.
     */
    public HelioService getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces);
    
    // TODO: add setLoadBalancer, getLoadBalancer, setRegistryClient, getRegistryClient
    //TODO: add  public String[] getServiceVariants(HelioServiceName serviceName);
}