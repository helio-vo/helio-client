package eu.heliovo.clientapi.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Template class for {@link HelioService} classes that access a remote resource.
 * 
 */
public abstract class AbstractRemoteServiceImpl extends AbstractServiceImpl {

    /**
     * The load balancer component to use.
     */
    protected LoadBalancer loadBalancer;

    /**
     * The location of the target WSDL file
     */
    protected AccessInterface[] accessInterfaces;

    /**
     * Default constructor
     */
    public AbstractRemoteServiceImpl() {
    }

    /**
     * Initialize the bean. Called by the HelioServiceFactory.
     */
    public void init() {
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