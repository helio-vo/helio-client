package eu.heliovo.clientapi.config.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.clientapi.model.service.AbstractRemoteServiceImpl;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.ServiceResolutionException;

/**
 * Base implementation for a {@link ServiceFactory}.
 * @author MarcoSoldati
 */
public class HelioServiceFactory implements ServiceFactory, ApplicationContextAware {
    /**
     * The logger
     */
    protected final Logger _LOGGER = Logger.getLogger(getClass());

    /**
     * Create a new service variant registry.
     */
    private final ServiceVariantRegistry serviceVariantRegistry = new ServiceVariantRegistry();
    
    /**
     * store the spring application context. 
     */
    private ApplicationContext springApplicationContext;
    
    /**
     * The service registry client to use
     */
    private ServiceRegistryClient serviceRegistryClient;
    
    /**
     * The load balancer to use
     */
    private LoadBalancer loadBalancer;
    
    /**
     * List of service factory configuration beans.
     */
    private List<ServiceFactoryConfiguration> configuration = new ArrayList<ServiceFactoryConfiguration>();
    
    /**
     * The service registry client to use.
     */
    public HelioServiceFactory() {
    }

    /**
     * Initialize the service factory.
     */
    public void init() {
        for (ServiceFactoryConfiguration config : configuration) {
            config.registerVariants(this.serviceVariantRegistry);
        }
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
    
    @Override
    public HelioService[] getHelioServices(HelioServiceName serviceName) {
        return getHelioServices(serviceName, null, null);
    }

    @Override
    public HelioService[] getHelioServices(HelioServiceName serviceName,
            String serviceVariant) {
        return getHelioServices(serviceName, serviceVariant, null);
    }

    @Override
    public HelioService[] getHelioServices(HelioServiceName serviceName,
            String serviceVariant, ServiceCapability serviceCapability) {
        return getHelioServices(serviceName, serviceVariant, serviceCapability, (AccessInterface[])null);
    }

    @Override
    public HelioService[] getHelioServices(HelioServiceName serviceName, String serviceVariant, ServiceCapability serviceCapability, AccessInterface... accessInterfaces) {
        if (_LOGGER.isDebugEnabled()) {
            _LOGGER.debug("Found services at: " + Arrays.toString(accessInterfaces));
        }
        
        String[] beanNames = serviceVariantRegistry.getServiceBeans(serviceName, serviceVariant, serviceCapability);
        if (beanNames == null) { // fall back to default service
            beanNames = serviceVariantRegistry.getServiceBeans(serviceName, null, null);
        }
        if (beanNames == null) {
            throw new ServiceResolutionException("Unable to find '" + serviceName + "', variant '" + serviceVariant + "', capability '" + serviceCapability + "'");
        }
        
        HelioService[] serviceImpls = createServiceImpls(beanNames, serviceName, serviceVariant, accessInterfaces);
        return serviceImpls;
    }
    
    /**
     * Create the concrete instance of a HELIO service.
     * @param beanName the name of the bean as stored in the spring config.
     * @param serviceName the name of the service.
     * @param serviceVariant the name of the service variant.
     * @param accessInterfaces the access interfaces to use. If null, the factory will get all interfaces from the registry and
     * set them on the created service impl.
     * @return concrete instances of the requested service. Usually this is just one element, but in cases like LinkProviderService there might be multiple elements.
     * And empty array is returned in case no service has been found.
     */
    protected HelioService[] createServiceImpls(String[] beanNames, HelioServiceName serviceName, String serviceVariant, AccessInterface[] accessInterfaces) {
        HelioService[] serviceImpls = new HelioService[beanNames.length];
        for (int i = 0; i < beanNames.length; i++) {
            String beanName = beanNames[i];
            HelioService serviceImpl = (HelioService) springApplicationContext.getBean(beanName);
            configureHelioService(serviceImpl, serviceName, serviceVariant);
            configureHelioRemoteService(serviceImpl, accessInterfaces);
            serviceImpls[i] = serviceImpl;
        }
        return serviceImpls;
    }

	private void configureHelioService(HelioService serviceImpl, HelioServiceName serviceName, String serviceVariant) {
		if (serviceImpl instanceof AbstractServiceImpl) {
		    AbstractServiceImpl impl = (AbstractServiceImpl)serviceImpl;                
		    if (serviceName != null) {
		        impl.setServiceName(serviceName);
		    }
		    if (serviceVariant != null) {
		        impl.setServiceVariant(serviceVariant);
		    }
		}
	}
	
	private void configureHelioRemoteService(HelioService serviceImpl, AccessInterface[] accessInterfaces) {
		if (serviceImpl instanceof AbstractRemoteServiceImpl) {
			AbstractRemoteServiceImpl impl = (AbstractRemoteServiceImpl)serviceImpl;                
			
			if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
				impl.setAccessInterfaces(getAccessInterfaces(serviceImpl));
			} else {
				impl.setAccessInterfaces(accessInterfaces);
			}
			impl.setLoadBalancer(getLoadBalancer());
			impl.init();
		}
	}

    
    /**
     * Read all access interfaces from the registry. 
     * @param serviceImpl the current service implementation
     * @return the retrieved interfaces.
     */
    private AccessInterface[] getAccessInterfaces(HelioService serviceImpl) {
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceImpl.getServiceName());
        List<AccessInterface> tmpAccessInterfaces = new ArrayList<AccessInterface>();
        for (ServiceCapability capability : serviceDescriptor.getCapabilities()) {
            if (serviceImpl.supportsCapability(capability)) {
                Collections.addAll(tmpAccessInterfaces, 
                    getServiceRegistryClient().getAllEndpoints(serviceDescriptor, capability, AccessInterfaceType.SOAP_SERVICE)
                );
            }
        }
        if (tmpAccessInterfaces.isEmpty()) {
            throw new RuntimeException("Unable to find any access interfaces for service " + serviceImpl);
        }
        return tmpAccessInterfaces.toArray(new AccessInterface[tmpAccessInterfaces.size()]);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.springApplicationContext = applicationContext;
    }
    
    /**
     * Get the service registry client.
     * @return the service registry client.
     */
    public ServiceRegistryClient getServiceRegistryClient() {
        return serviceRegistryClient;
    }
    
    /**
     * Set the service registry client
     * @param serviceRegistryClient
     */
    @Required
    public void setServiceRegistryClient(ServiceRegistryClient serviceRegistryClient) {
        this.serviceRegistryClient = serviceRegistryClient;
    }

    /**
     * Get the load balancer.
     * @return the load balancer
     */
    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * Set the load balancer.
     * @param loadBalancer the load balancer
     */
    @Required
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * Get the configuration.
     * @return the configuration the config.
     */
    public List<ServiceFactoryConfiguration> getConfiguration() {
        return configuration;
    }

    /**
     * Set the configuration. 
     * @param configuration the configuration to set
     */
    public void setConfiguration(List<ServiceFactoryConfiguration> configuration) {
        this.configuration = configuration;
    }
}