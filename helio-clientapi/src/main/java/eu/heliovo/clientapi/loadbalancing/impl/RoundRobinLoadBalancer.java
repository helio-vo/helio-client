package eu.heliovo.clientapi.loadbalancing.impl;

import java.util.HashMap;
import java.util.Map;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.registryclient.AccessInterface;

/**
 * Simple load balancer that follows the round robin scheme. 
 * Services that are down are fined with a penalty and are skipped for a number of rounds.
 * @author MarcoSoldati
 *
 */
class RoundRobinLoadBalancer implements LoadBalancer {
    /**
     * Number of rounds to skip a service that is down.
     */
    private static final int PENALTY = 1000;
    
    /**
     * count how many time a specific service has been accessed
     */
    private Map<AccessInterface, Integer> accessPerService = new HashMap<AccessInterface, Integer>();
    
    /**
     * always get the service with the least number of accesses.
     */
    @Override
    public AccessInterface getBestEndPoint(AccessInterface... accessInterfaces) {
        int bestCount = Integer.MAX_VALUE;
        AccessInterface best = null;
        for (AccessInterface accessInterface : accessInterfaces) {
            int currentCount = getAccessPerService(accessInterface);
            if (currentCount < bestCount) {
                best = accessInterface;
                bestCount = currentCount;
            }
        }
        return best;
    }

    /**
     * Get the access counts per interface
     * @param accessInterface the access interface
     * @return the current count
     */
    private int getAccessPerService(AccessInterface accessInterface) {
        Integer count = accessPerService.get(accessInterface);
        if (count == null) {
            count = new Integer(0);
            accessPerService.put(accessInterface, count);
        }
        return count;
    }

    @Override
    public void updateAccessTime(AccessInterface accessInterface, long timeInMillis) {
        int inc;
        if (timeInMillis < 0) {
            inc = PENALTY;  // penalty for timed out services
        } else {
            inc = 1;
        }
        incAccessPerService(accessInterface, inc);
    }

    /**
     * Increase the number or accesses per service.
     * @param accessInterface the access interface
     * @param inc the increase factor.
     */
    private void incAccessPerService(AccessInterface accessInterface, int inc) {
        Integer count = accessPerService.get(accessInterface);
        if (count == null) {
            count = new Integer(0);
        }
        count += inc;
        accessPerService.put(accessInterface, count);
    }
}
