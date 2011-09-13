package eu.heliovo.clientapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.model.service.ServiceFactory;
import eu.heliovo.clientapi.processing.context.ContextServiceFactory;
import eu.heliovo.clientapi.query.asyncquery.impl.AsyncQueryServiceFactory;
import eu.heliovo.clientapi.query.syncquery.impl.SyncQueryServiceFactory;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

/**
 * Main object to access the HELIO API. This is implemented as facade to the underlying system.
 * Clients can either user these convenience methods or directly access the available services
 * @author marco soldati at fhnw ch
 *
 */
public class HelioClient {
    /**
     * Map holding the factories that provide a specific capability.
     */
    private static Map<ServiceCapability, ServiceFactory> factoryMap = new HashMap<ServiceCapability, ServiceFactory>();
    
    static {
        factoryMap.put(ServiceCapability.ASYNC_QUERY_SERVICE, AsyncQueryServiceFactory.getInstance());
        factoryMap.put(ServiceCapability.SYNC_QUERY_SERVICE, SyncQueryServiceFactory.getInstance());
        factoryMap.put(ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, ContextServiceFactory.getInstance());
    }
    
	/**
	 * Get all known service names of HELIO.
	 * @return all services registered with HelioClient or empty array if none are found.
	 */
	public HelioServiceName[] getAllServiceNames() {
	    return HelioServiceName.values().toArray(new HelioServiceName[0]);
	}
	
	/**
	 * Get all services that implement a given capability. E.g. you can find all registered sync query serivces.
	 * @param serviceCapability the capability to look for.
	 * @return a list of service names or empty array if nothing has been found.
	 */
	public HelioServiceName[] getServiceNamesByCapability(ServiceCapability serviceCapability) {
	    List<HelioServiceName> ret = new ArrayList<HelioServiceName>();
	    ServiceDescriptor[] descriptors = getServiceDescriptorByCapability(serviceCapability);
	    for (ServiceDescriptor serviceDescriptor : descriptors) {
	        ret.add(serviceDescriptor.getName());
        }
	    return ret.toArray(new HelioServiceName[ret.size()]);
	}

	/**
	 * Get a proxy to a particular service instance
	 * @param helioServiceName the name of the service to be loaded.
	 * @param serviceType optional argument for processing services that need to identify the specific process to be called.
	 * @param accessInterfaces end points of known services. If null, this value will be retrieved from the registry.  
	 * @return a HELIO service instance or null if nothing has been found.
	 */
	public HelioService getServiceInstance(HelioServiceName helioServiceName,  String serviceType, AccessInterface ... accessInterfaces) {
	    ServiceDescriptor descriptor = getServiceDescriptorByName(helioServiceName);
	    if (descriptor == null) {
	        return null;
	    }
	    Set<ServiceCapability> capabilites = descriptor.getCapabilities();
	    
	    for (ServiceCapability capability : capabilites) {
            if (factoryMap.containsKey(capability)) {
                ServiceFactory factory = factoryMap.get(capability);
                HelioService service = factory.getHelioService(helioServiceName, serviceType, accessInterfaces);
                if (service != null) {
                    return service;
                }
            }
        }
	    return null;
	}
	
	/**
	 * Get all descriptors of a service available in HELIO.
	 * @return the list of registered ServiceDescriptors.
	 */
	protected ServiceDescriptor[] getAllServiceDescriptors() {
	    ServiceRegistryClient registryClient = ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
	    return registryClient.getAllServiceDescriptors();
	}
	
	/**
	 * Get a collection of service descriptors by a capability.
	 * @param serviceCapability the service capability.
	 * @return descriptors that implement the given capability.
	 */
	protected ServiceDescriptor[] getServiceDescriptorByCapability(ServiceCapability serviceCapability) {
	    ServiceDescriptor[] descriptors = getAllServiceDescriptors();
	    List<ServiceDescriptor> ret = new ArrayList<ServiceDescriptor>();
	    for (ServiceDescriptor serviceDescriptor : descriptors) {
	        if (serviceDescriptor.getCapabilities().contains(serviceCapability)) {
	            ret.add(serviceDescriptor);
	        }
        }
	    return ret.toArray(new ServiceDescriptor[ret.size()]);
	}

	/**
	 * Get the registered descriptors for known service names.
	 * @param serviceName name of the service to find corresponding descriptor
	 * @return descriptor 
	 */
	protected ServiceDescriptor getServiceDescriptorByName(HelioServiceName serviceName) {
	    ServiceRegistryClient registryClient = ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
	    return registryClient.getServiceDescriptor(serviceName);
	}	
}
