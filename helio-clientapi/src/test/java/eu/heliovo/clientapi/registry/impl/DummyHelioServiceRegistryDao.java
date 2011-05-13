package eu.heliovo.clientapi.registry.impl;

import eu.heliovo.clientapi.registry.HelioServiceRegistryDao;

/**
 * Dummy implementation of a test registry for testing purposes only
 * @author MarcoSoldati
 *
 */
public class DummyHelioServiceRegistryDao extends AbstractHelioServiceRegistryDao implements HelioServiceRegistryDao {

    private static final DummyHelioServiceRegistryDao instance = new DummyHelioServiceRegistryDao();
    
    /**
     * Get the singleton instance of this registry.
     * @return
     */
    public static DummyHelioServiceRegistryDao getInstance() {
        return instance;
    }
    
    private DummyHelioServiceRegistryDao() {
       
    }
    
}
