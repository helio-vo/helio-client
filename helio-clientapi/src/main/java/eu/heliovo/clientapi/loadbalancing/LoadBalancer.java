package eu.heliovo.clientapi.loadbalancing;

import eu.heliovo.registryclient.AccessInterface;

/**
 * Helper service to return the best endpoint for a given set of services endpoints.
 * The interface is designed for several different implementations of a load balancer.
 * @author MarcoSoldati
 *
 */
public interface LoadBalancer {

    /**
     * Return the "best" service interface for a collection of given service interfaces.
     * @param accessInterfaces the service interfaces. In case only one accessInterface 
     * is submitted this interface will be returned. Must not be null or empty.
     * @return the best interface.
     */
    public AccessInterface getBestEndPoint(AccessInterface ... accessInterfaces);
    
    /**
     * Users of a load balancer should update its statistics after executing a service.
     * This can help certain implementations to guess the load of an instance.
     * @param accessInterface the use interface
     * @param timeInMillis the required time in millis. in case of a timeout this should be set to a value &lt; 0.
     */
    public void updateAccessTime(AccessInterface accessInterface, long timeInMillis);
}
