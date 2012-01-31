package eu.heliovo.clientapi.model.service;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.clientapi.loadbalancing.impl.LoadBalancerFactory;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Base implementation with some helper methods for all services that want to implement a {@link HelioService}.
 *
 */
public abstract class AbstractServiceImpl implements HelioService {

    /**
     * The load balancer component to use.
     */
    protected LoadBalancer loadBalancer = LoadBalancerFactory.getInstance().getLoadBalancer();
    
    /**
     * The location of the target WSDL file
     */
    protected final AccessInterface[] accessInterfaces;
    
    /**
     * Name of the service
     */
    protected final HelioServiceName serviceName;
    
    /**
     * The optional variant
     */
    protected final String serviceVariant;

    
    /**
     * Description of this service.
     */
    protected final String description;


    /**
     * Constructor
     * @param serviceName the name of the service. Must not be null.
     * @param serviceVariant the service variant. May be null.
     * @param description a short description. Optional.
     * @param accessInterfaces interface to use. Must not be null or empty.
     */
    public AbstractServiceImpl(HelioServiceName serviceName, String serviceVariant, String description, AccessInterface[] accessInterfaces) {
        this.serviceVariant = serviceVariant;
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");
        this.serviceName = serviceName;
        this.accessInterfaces = accessInterfaces;
        this.description = description;
    }

    @Override
    public HelioServiceName getServiceName() {
    	return serviceName;
    }
    
    @Override
    public String getServiceVariant() {
        return serviceVariant;
    }

    @Override
    public String getDescription() {
    	return description;
    }

    /**
     * Get the best access interface.
     * @return the best known access interface
     */
    protected AccessInterface getBestAccessInterface() {
        AccessInterface bestAccessInterface = loadBalancer.getBestEndPoint(accessInterfaces);
        return bestAccessInterface;
    }

}