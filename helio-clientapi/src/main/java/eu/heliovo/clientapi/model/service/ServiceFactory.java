package eu.heliovo.clientapi.model.service;

import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;

public interface ServiceFactory {
    /**
     * Get a new instance of the "best" service provider for a desired service.
     * @param serviceName the service name to use.
     * @param subType optional subtype for a service. 
     * @param accessInterfaces list of access interface to use. The service uses a load balancer to choose the best matching interface.
     * @return a Service implementation.
     */
    public HelioService getHelioService(HelioServiceName serviceName, String subType, AccessInterface... accessInterfaces);
}