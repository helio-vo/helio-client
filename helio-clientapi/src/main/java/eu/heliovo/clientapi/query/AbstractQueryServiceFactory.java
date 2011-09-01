package eu.heliovo.clientapi.query;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

public class AbstractQueryServiceFactory {

    /**
     * The service registry client to use.
     */
    protected final ServiceRegistryClient serviceRegistry = ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();

    public AbstractQueryServiceFactory() {
        super();
    }

    /**
     * Get the service descriptor from the registry client.
     * @param serviceName the name of the service.
     * @return the descriptor
     * @throws IllegalArgumentException if the descriptor does not exist.
     */
    protected ServiceDescriptor getServiceDescriptor(HelioServiceName serviceName) {
        ServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceName);
        if (serviceDescriptor == null) {
            throw new ServiceResolutionException("Unable to find service with name " +  serviceName);
        }
        return serviceDescriptor;
    }

}