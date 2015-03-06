package eu.heliovo.clientapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.config.service.ServiceFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;
import eu.heliovo.shared.util.FileUtil;

/**
 * Main object to access the HELIO API. This is implemented as facade to the underlying system.
 * Clients can either user these convenience methods or directly access the available services
 * @author marco soldati at fhnw ch
 *
 */
public class HelioClient {
    /**
     * The logger to use.
     */
    private static final Logger _LOGGER = Logger.getLogger(HelioClient.class);
    
    /**
     * The helio service factory
     */
    private ServiceFactory serviceFactory;

    // init the registry.
    private ServiceRegistryClient registryClient;

    /**
     * initialize the system. 
     * If this method is not called by the user it will be called by the first call to any methods in this 
     */
    public synchronized void init() {
        // do some hardcoded init stuff, if needed   
        // register the HPSL
        ServiceDescriptor hpsDescriptor = getServiceDescriptorByName(HelioServiceName.HPS);
        if (hpsDescriptor == null) {
            hpsDescriptor = new GenericServiceDescriptor(HelioServiceName.HPS, "Locally registered HPS", 
            				ServiceCapability.HELIO_PROCESSING_SERVICE);
            registryClient.registerServiceDescriptor(hpsDescriptor);
        }
        registryClient.registerServiceInstance(hpsDescriptor, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, 
                ServiceCapability.HELIO_PROCESSING_SERVICE, 
                FileUtil.asURL("http://cagnode58.cs.tcd.ie:8080/helio-hps-server/hpsService?wsdl")));
        
        registerLocalQueryServiceCapability(HelioServiceName.HEC);
        registerLocalQueryServiceCapability(HelioServiceName.ILS);
        registerLocalQueryServiceCapability(HelioServiceName.ICS);
        registerIesQueryServiceCapability(HelioServiceName.IES);
    }

	private void registerLocalQueryServiceCapability(HelioServiceName serviceName) {
		ServiceDescriptor serivceDescriptor = getServiceDescriptorByName(serviceName);
		if (serivceDescriptor != null) {
			serivceDescriptor.addCapability(ServiceCapability.LOCAL_QUERY_SERVICE);
		}
	}
	
	/**
	 * Register ServiceCapabilities of IES
	 * @param serviceName
	 * @author junia schoch at fhnw ch
	 */
	private void registerIesQueryServiceCapability(HelioServiceName serviceName) {
		ServiceDescriptor serivceDescriptor = getServiceDescriptorByName(serviceName);
		if (serivceDescriptor != null) {
			serivceDescriptor.addCapability(ServiceCapability.LOCAL_QUERY_SERVICE);
			serivceDescriptor.addCapability(ServiceCapability.ASYNC_QUERY_SERVICE);
		}
	}
    
	/**
	 * Get all known service names of HELIO.
	 * @return all services registered with HelioClient or empty array if none are found.
	 */
	public HelioServiceName[] getAllServiceNames() {
	    // init the registry
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
	 * @param serviceVariant optional argument for services that offer different variants.
	 * @param serviceCapability the desired capability of the service. May be null if only one capability is known.
	 * @return a HELIO service instance or null if nothing has been found.
	 */
	public HelioService getServiceInstance(HelioServiceName helioServiceName, String serviceVariant, ServiceCapability serviceCapability) {
	    ServiceDescriptor descriptor = getServiceDescriptorByName(helioServiceName);
	    if (descriptor == null) {
	        return null;
	    }

	    // check if the service implements the required capability
	    Set<ServiceCapability> capabilites = descriptor.getCapabilities();
	    if (serviceCapability == null || capabilites.contains(serviceCapability)) {
	        // get the service from the factory.
	        
	        HelioService[] services = serviceFactory.getHelioServices(helioServiceName, serviceVariant, serviceCapability);
            if (services != null && services.length == 1) {
                return services[0];
            } else {
                _LOGGER.warn("Unable to load service with name " + helioServiceName + " and service variant name " + serviceVariant + " and capabilty " + serviceCapability + " from serviceFactory: " + services);
            }
	    } else {
	        _LOGGER.warn("Service " + helioServiceName + " does not implement the requested capabilty " + serviceCapability);
	    }
	    
	    return null;
	}
	
	/**
     * Get a proxy to all service instances with a given capability.
     * @param serviceCapability the desired capability of the service. May be null if only one capability is known.
     * @return a list of HELIO service instances or an empty list if nothing has been found.
     */
    public HelioService[] getServiceInstances(ServiceCapability serviceCapability) {
        HelioService[] services = serviceFactory.getHelioServices(null, null, serviceCapability);
        if (services.length == 0) {
            _LOGGER.warn("Unable to load service with capabilty " + serviceCapability + " from serviceFactory.");
        }
        return services;
    }

	
	/**
	 * Get all descriptors of a service available in HELIO.
	 * @return the list of registered ServiceDescriptors.
	 */
	protected ServiceDescriptor[] getAllServiceDescriptors() {
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
	    return registryClient.getServiceDescriptor(serviceName);
	}

    /**
     * Get the registry client
     * @return the registry client
     */
    public ServiceRegistryClient getRegistryClient() {
        return registryClient;
    }

    /**
     * Set the registry client
     * @param registryClient the registry client
     */
    public void setRegistryClient(ServiceRegistryClient registryClient) {
        this.registryClient = registryClient;
    }
    
    /**
     * @return the serviceFactory
     */
    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    /**
     * @param serviceFactory the serviceFactory to set
     */
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }
}
