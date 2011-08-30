package eu.heliovo.clientapi.loadbalancing.impl;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;

/**
 * Factory to get the load balancer
 * @author MarcoSoldati
 *
 */
public class LoadBalancerFactory {
    private static final LoadBalancerFactory instance = new LoadBalancerFactory();
    
    /**
     * The default balancer to use if none has been set.
     */
    private final static Class<? extends LoadBalancer> DEFAULT_LOAD_BALANCER = RoundRobinLoadBalancer.class;
    
    /**
     * Get the singleton instance of this factory
     * @return the factory instance
     */
    public static synchronized LoadBalancerFactory getInstance() {
        return instance;
    }
    
    /**
     * The load balancer to use
     */
    private LoadBalancer loadBalancer;

    /**
     * Hide the constructor
     */
    private LoadBalancerFactory() {
    }
    
    /**
     * Get the load balancer instance.
     * @return the load balancer.
     */
    public synchronized LoadBalancer getLoadBalancer() {
        if (this.loadBalancer == null) {
            try {
                this.loadBalancer = DEFAULT_LOAD_BALANCER.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to instantiate " + DEFAULT_LOAD_BALANCER.getName() + ". Cause: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to instantiate " + DEFAULT_LOAD_BALANCER.getName() + ". Cause: " + e.getMessage(), e);
            }
        }
        return loadBalancer;
    }
    
    /**
     * Set the LoadBalancer. This is only needed for testing.
     * @param loadBlancer the load balancer to use.
     */
    public synchronized void setLoadBalancer(LoadBalancer loadBlancer) {
        this.loadBalancer = loadBlancer;
    }
}
