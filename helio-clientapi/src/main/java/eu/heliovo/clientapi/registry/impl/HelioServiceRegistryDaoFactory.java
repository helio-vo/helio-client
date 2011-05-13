package eu.heliovo.clientapi.registry.impl;

import eu.heliovo.clientapi.registry.HelioServiceRegistryDao;

/**
 * Factory to get the registry DAO
 * @author MarcoSoldati
 *
 */
public class HelioServiceRegistryDaoFactory {
    private static final HelioServiceRegistryDaoFactory instance = new HelioServiceRegistryDaoFactory();
    
    /**
     * The default DAO to use if none has been set.
     */
    //private final static Class<? extends HelioServiceRegistryDao> DEFAULT_REGISTRY_DAO = RemoteHelioServiceRegistryDao.class;
    private final static Class<? extends HelioServiceRegistryDao> DEFAULT_REGISTRY_DAO = LocalHelioServiceRegistryDao.class;
    
    /**
     * Get the singleton instance of this factory
     * @return the factory instance
     */
    public static HelioServiceRegistryDaoFactory getInstance() {
        return instance;
    }
    
    /**
     * The registry dao to use
     */
    private HelioServiceRegistryDao helioServiceRegistryDao;

    /**
     * Hide the constructor
     */
    private HelioServiceRegistryDaoFactory() {
    }
    
    /**
     * Get the service registry dao to access the registry.
     * @return the registry dao
     */
    public HelioServiceRegistryDao getHelioServiceRegistryDao() {
        if (this.helioServiceRegistryDao == null) {
            try {
                this.helioServiceRegistryDao = DEFAULT_REGISTRY_DAO.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to instantiate " + DEFAULT_REGISTRY_DAO.getName() + ". Cause: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to instantiate " + DEFAULT_REGISTRY_DAO.getName() + ". Cause: " + e.getMessage(), e);
            }
        }
        return helioServiceRegistryDao;
    }
    
    /**
     * Set the HelioServiceRegistryDao. This is only needed for testing.
     * @param helioServiceRegistryDao
     */
    public void setHelioServiceRegistryDao(HelioServiceRegistryDao helioServiceRegistryDao) {
//        if (this.helioServiceRegistryDao != null && !this.helioServiceRegistryDao.equals(helioServiceRegistryDao)) {
//            throw new IllegalArgumentException("Argument 'helioServiceRegistryDao' must not be changed once it has been set.");
//        }
        this.helioServiceRegistryDao = helioServiceRegistryDao;
    }
}
