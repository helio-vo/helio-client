package eu.heliovo.clientapi.registry.impl;

import eu.heliovo.clientapi.registry.HelioServiceCapability;

/**
 * Data access object to get access a local search registry implementation.
 * The local implementation is configured through a properties file.
 * @author MarcoSoldati
 *
 */
public class LocalHelioServiceRegistryDao extends AbstractHelioServiceRegistryDao {
	
    /**
     * The instance of this registry
     */
	private static LocalHelioServiceRegistryDao instance = null;
	
	/**
	 * Get the singleton instance of this registry.
	 * @return the singleton instance
	 */
	public synchronized static LocalHelioServiceRegistryDao getInstance() {
		if (instance == null) {
			instance = new LocalHelioServiceRegistryDao();
			instance.init();
		}
		return instance;
	}
	
	/**
	 * Hide the default constructor
	 */
	private LocalHelioServiceRegistryDao() {
	}

	/**
	 * Init the registry. Try to load local configuration file, if this does not exist create it
	 * with default values and then load it. 
	 */
	private void init() {
	    // populate registry
	    register("HEC", HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-hec/HelioLongQueryService?wsdl");
	    register("HEC", HelioServiceCapability.SYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-hec/HelioService?wsdl");

	    register("UOC", HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-uoc/HelioLongQueryService?wsdl");
	    register("UOC", HelioServiceCapability.SYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-uoc/HelioService?wsdl");

	    register("DPAS", HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
	    register("DPAS", HelioServiceCapability.SYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioService?wsdl");

	    register("ICS", HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioLongQueryService?wsdl");
	    register("ICS", HelioServiceCapability.SYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioService?wsdl");

	    register("ILS", HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioLongQueryService?wsdl");
	    register("ILS", HelioServiceCapability.SYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioService?wsdl");

	    register("MDES", HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService.wsdl");      
	    register("MDES", HelioServiceCapability.SYNC_QUERY_SERVICE, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioService.wsdl");       
	}

    private void register(String serviceName, HelioServiceCapability capability, String wsdlFile) {
        registerServiceInstance(serviceName, serviceName, null, null, capability, wsdlFile);
    }
}
