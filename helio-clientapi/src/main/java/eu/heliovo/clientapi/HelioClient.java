package eu.heliovo.clientapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.model.service.ServiceFactory;
import eu.heliovo.clientapi.processing.context.ContextServiceFactory;
import eu.heliovo.clientapi.processing.hps.ProcessingServiceFactory;
import eu.heliovo.clientapi.query.asyncquery.impl.AsyncQueryServiceFactory;
import eu.heliovo.clientapi.query.syncquery.impl.SyncQueryServiceFactory;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

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
     * Map holding the factories that provide a specific capability.
     */
    private static Map<ServiceCapability, ServiceFactory> factoryMap = new HashMap<ServiceCapability, ServiceFactory>();
    
    static {
        factoryMap.put(ServiceCapability.ASYNC_QUERY_SERVICE, AsyncQueryServiceFactory.getInstance());
        factoryMap.put(ServiceCapability.SYNC_QUERY_SERVICE, SyncQueryServiceFactory.getInstance());
        factoryMap.put(ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, ContextServiceFactory.getInstance());
        factoryMap.put(ServiceCapability.LINK_PROVIDER_SERVICE, LinkProviderFactory.getInstance());
        factoryMap.put(ServiceCapability.HELIO_PROCESSING_SERVICE, ProcessingServiceFactory.getInstance());
    }
    
    /**
     * Is the client initialized.
     */
    private boolean init = false;
    
    /**
     * initialize the system. 
     * If this method is not called by the user it will be called by the first call to any methods in this 
     */
    public synchronized void init() {
        if (init)
            return;
        
        // load the stil utils
        @SuppressWarnings("unused")
        Class<STILUtils> x = STILUtils.class;

        // init the registry.
        ServiceRegistryClient registryClient = (ServiceRegistryClient) ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
        
        // do some hardcoded init stuff
        ServiceDescriptor desDescriptor = getServiceDescriptorByName(HelioServiceName.DES);
        if (desDescriptor != null) {
            desDescriptor.addCapability(ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
        registryClient.registerServiceInstance(desDescriptor, 
            new AccessInterfaceImpl(
                AccessInterfaceType.SOAP_SERVICE, 
                ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, 
                HelioFileUtil.asURL("http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService.wsdl"))
            );
        }
        
        // register the HPS
        ServiceDescriptor hpsDescriptor = getServiceDescriptorByName(HelioServiceName.HPS);
        if (hpsDescriptor == null) {
            hpsDescriptor = new GenericServiceDescriptor(HelioServiceName.HPS, "Locally registered HPS", ServiceCapability.HELIO_PROCESSING_SERVICE);
            registryClient.registerServiceDescriptor(hpsDescriptor);
        }
        
        registryClient.registerServiceInstance(hpsDescriptor, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, 
                ServiceCapability.HELIO_PROCESSING_SERVICE, 
                HelioFileUtil.asURL("http://cagnode58.cs.tcd.ie:8080/helio-hps-server/hpsService?wsdl")));
        

        init = true;
    }
    
	/**
	 * Get all known service names of HELIO.
	 * @return all services registered with HelioClient or empty array if none are found.
	 */
	public HelioServiceName[] getAllServiceNames() {
	    if (!init)
	        init();
	    
	    // init the registry
	    ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
	    return HelioServiceName.values().toArray(new HelioServiceName[0]);
	}
	
	/**
	 * Get all services that implement a given capability. E.g. you can find all registered sync query serivces.
	 * @param serviceCapability the capability to look for.
	 * @return a list of service names or empty array if nothing has been found.
	 */
	public HelioServiceName[] getServiceNamesByCapability(ServiceCapability serviceCapability) {
	    if (!init)
            init();
	    
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
	 * @param serviceCapability the desired capability of the service.
	 * @param serviceVariant optional argument for services that offer different variants.
	 * @return a HELIO service instance or null if nothing has been found.
	 */
	public HelioService getServiceInstance(HelioServiceName helioServiceName, ServiceCapability serviceCapability, String serviceVariant) {
	    if (!init)
            init();
	    
	    AssertUtil.assertArgumentNotNull(serviceCapability, "serviceCapability");
	    ServiceDescriptor descriptor = getServiceDescriptorByName(helioServiceName);
	    if (descriptor == null) {
	        return null;
	    }

	    // check if the service implements the required capability
	    Set<ServiceCapability> capabilites = descriptor.getCapabilities();
	    if (capabilites.contains(serviceCapability)) {
	        // get the factory, if registered.
	        if (factoryMap.containsKey(serviceCapability)) {
	            ServiceFactory factory = factoryMap.get(serviceCapability);
	            HelioService service = factory.getHelioService(helioServiceName, serviceVariant, (AccessInterface[]) null);
	            if (service != null) {
	                return service;
	            } else {
	                _LOGGER.warn("Unable to load service with name " + helioServiceName + " and service variant name " + serviceVariant + " from factory " + factory);
	            }
	        } else {
	            _LOGGER.warn("Unable to load service service factory for capability " + serviceCapability + ". This is most likely due to a limitation of the clientapi.");
	        }
	    } else {
	        _LOGGER.warn("Service " + helioServiceName + " does not provide the capabilty " + serviceCapability);
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
