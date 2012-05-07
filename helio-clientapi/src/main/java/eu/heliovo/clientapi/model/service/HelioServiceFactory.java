package eu.heliovo.clientapi.model.service;

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
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;

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
    public HelioService getHelioService(HelioServiceName serviceName) {
        return getHelioService(serviceName, null, null);
    }

    @Override
    public HelioService getHelioService(HelioServiceName serviceName,
            String serviceVariant) {
        return getHelioService(serviceName, serviceVariant, null);
    }

    @Override
    public HelioService getHelioService(HelioServiceName serviceName,
            String serviceVariant, ServiceCapability serviceCapability) {
        return getHelioService(serviceName, serviceVariant, serviceCapability, (AccessInterface[])null);
    }

    @Override
    public HelioService getHelioService(HelioServiceName serviceName, String serviceVariant, ServiceCapability serviceCapability, AccessInterface... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            List<AccessInterface> tmpAccessInterfaces = new ArrayList<AccessInterface>();
            for (ServiceCapability capability : serviceDescriptor.getCapabilities()) {
                if (serviceCapability == null || serviceCapability.equals(capability)) {
                    Collections.addAll(tmpAccessInterfaces, 
                            getServiceRegistryClient().getAllEndpoints(serviceDescriptor, capability, AccessInterfaceType.SOAP_SERVICE)
                    );
                }
            }
            accessInterfaces = tmpAccessInterfaces.toArray(new AccessInterface[tmpAccessInterfaces.size()]);
        }
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");

        if (_LOGGER.isDebugEnabled()) {
            _LOGGER.debug("Found services at: " + Arrays.toString(accessInterfaces));
        }
        
        String beanName = serviceVariantRegistry.getServiceImpl(serviceName, serviceVariant, serviceCapability);
        if (beanName == null) { // fall back to default service
            beanName = serviceVariantRegistry.getServiceImpl(serviceName, null, null);
        }
        if (beanName == null) {
            throw new ServiceResolutionException("Unable to find '" + serviceName + "', variant '" + serviceVariant + "', capability '" + serviceCapability + "'");
        }
        
        HelioService serviceImpl = createServiceImpl(beanName, serviceName, serviceVariant, accessInterfaces);
        return serviceImpl;
    }
    
    /**
     * Create the concrete instance of a HELIO service.
     * @param beanName the name of the bean as stored in the spring config.
     * @param serviceName the name of the service.
     * @param serviceVariant the name of the service variant.
     * @param accessInterfaces the access interfaces will not be null.
     * @return concrete instance of the requested service.
     */
    protected final HelioService createServiceImpl(String beanName, HelioServiceName serviceName, String serviceVariant, AccessInterface[] accessInterfaces) {
        HelioService serviceImpl = (HelioService) springApplicationContext.getBean(beanName, serviceName, serviceVariant);
        if (serviceImpl instanceof AbstractServiceImpl) {
            AbstractServiceImpl impl = (AbstractServiceImpl)serviceImpl;
            impl.setAccessInterfaces(accessInterfaces);
            impl.setLoadBalancer(getLoadBalancer());
        }
        return serviceImpl;
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