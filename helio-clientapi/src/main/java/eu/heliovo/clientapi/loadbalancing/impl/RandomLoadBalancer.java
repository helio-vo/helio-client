package eu.heliovo.clientapi.loadbalancing.impl;

import java.util.Random;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.registryclient.AccessInterface;

public class RandomLoadBalancer implements LoadBalancer {
    private Random random = new Random();
    
    @Override
    public AccessInterface getBestEndPoint(AccessInterface... accessInterfaces) {
        return accessInterfaces[random.nextInt(accessInterfaces.length)];
    }

    @Override
    public void updateAccessTime(AccessInterface accessInterface,
            long timeInMillis) {
        // ignore
    }

}
