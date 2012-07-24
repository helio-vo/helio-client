package eu.heliovo.clientapi.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Base implementation with some helper methods for all services that want to
 * implement a {@link HelioService}.
 * 
 */
public abstract class AbstractServiceImpl implements HelioService {

    /**
     * The load balancer component to use.
     */
    protected LoadBalancer loadBalancer;

    /**
     * The location of the target WSDL file
     */
    protected AccessInterface[] accessInterfaces;

    /**
     * Name of the service
     */
    private HelioServiceName serviceName;

    /**
     * The optional variant
     */
    private String serviceVariant;

    /**
     * Default constructor
     */
    public AbstractServiceImpl() {
    }

    /**
     * Initialize the bean. Called by the HelioServiceFactory.
     */
    public void init() {
    }

    @Override
    public HelioServiceName getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(HelioServiceName serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getServiceVariant() {
        return serviceVariant;
    }

    /**
     * @param serviceVariant
     *            the serviceVariant to set
     */
    public void setServiceVariant(String serviceVariant) {
        this.serviceVariant = serviceVariant;
    }

    /**
     * Get access interfaces
     * 
     * @return the access interfaces
     */
    public AccessInterface[] getAccessInterfaces() {
        return accessInterfaces;
    }

    /**
     * Set access interfaces
     * 
     * @param accessInterfaces
     *            the access interfaces
     */
    public void setAccessInterfaces(AccessInterface... accessInterfaces) {
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");
        this.accessInterfaces = accessInterfaces;
    }

    /**
     * Get the load balancer
     * 
     * @return the load balancer
     */
    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * Set the load balancer
     * 
     * @param loadBalancer
     *            the load balancer
     */
    @Required
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * Get the best access interface.
     * 
     * @param queryDelegate
     *            the currently used query delegate.
     * @return the best known access interface
     */
    protected AccessInterface getBestAccessInterface(QueryDelegate queryDelegate) {
        List<AccessInterface> tmpAccessInterfaces = new ArrayList<AccessInterface>();
        for (AccessInterface accessInterface : accessInterfaces) {
            if (queryDelegate.supportsCapabilty(accessInterface.getCapability())) {
                tmpAccessInterfaces.add(accessInterface);
            }
        }
        AccessInterface bestAccessInterface = loadBalancer.getBestEndPoint(tmpAccessInterfaces
                .toArray(new AccessInterface[tmpAccessInterfaces.size()]));
        return bestAccessInterface;
    }

}