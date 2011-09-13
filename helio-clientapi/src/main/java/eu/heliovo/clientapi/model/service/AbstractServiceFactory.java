package eu.heliovo.clientapi.model.service;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

public abstract class AbstractServiceFactory implements ServiceFactory {

    /**
     * The service registry client to use.
     */

    public AbstractServiceFactory() {
        super();
    }

    /**
     * Get the service descriptor from the registry client.
     * @param serviceName the name of the service.
     * @return the descriptor
     * @throws IllegalArgumentException if the descriptor does not exist.
     */
    protected ServiceDescriptor getServiceDescriptor(HelioServiceName serviceName) {
        ServiceRegistryClient serviceRegistry = getServiceRegistryClient();
        ServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceName);
        if (serviceDescriptor == null) {
            throw new ServiceResolutionException("Unable to find service with name " +  serviceName);
        }
        return serviceDescriptor;
    }
    
    /**
     * Get the service registry client.
     * @return the service registry client.
     */
    protected ServiceRegistryClient getServiceRegistryClient() {
        return ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
    }

}