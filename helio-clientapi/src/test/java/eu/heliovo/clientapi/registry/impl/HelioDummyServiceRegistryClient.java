package eu.heliovo.clientapi.registry.impl;

import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.AbstractHelioServiceRegistryClient;


/**
 * Dummy implementation of a test registry for testing purposes only
 * @author MarcoSoldati
 *
 */
public class HelioDummyServiceRegistryClient extends AbstractHelioServiceRegistryClient implements ServiceRegistryClient {

    private static final HelioDummyServiceRegistryClient instance = new HelioDummyServiceRegistryClient();
    
    /**
     * Get the singleton instance of this registry.
     * @return
     */
    public static HelioDummyServiceRegistryClient getInstance() {
        return instance;
    }
    
    private HelioDummyServiceRegistryClient() {
       
    }
    
}
