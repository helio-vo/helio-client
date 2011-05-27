package eu.heliovo.clientapi.registry.impl;

import eu.heliovo.clientapi.model.service.HelioServiceName;
import eu.heliovo.clientapi.registry.AccessInterfaceType;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Data access object to get access a local search registry implementation.
 * The local implementation is configured through a properties file.
 * @author MarcoSoldati
 *
 */
class LocalHelioServiceRegistryDao extends AbstractHelioServiceRegistryDao {
    /**
	 * Public constructor is package local for testing
	 */
	LocalHelioServiceRegistryDao() {
	    init();
	}

	/**
	 * Init the registry. Try to load local configuration file, if this does not exist create it
	 * with default values and then load it. 
	 */
	private void init() {
	    // populate registry
	    register(HelioServiceName.HEC, HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-hec/HelioLongQueryService?wsdl");
	    register(HelioServiceName.HEC, HelioServiceCapability.SYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-hec/HelioService?wsdl");

	    register(HelioServiceName.UOC, HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-uoc/HelioLongQueryService?wsdl");
	    register(HelioServiceName.UOC, HelioServiceCapability.SYNC_QUERY_SERVICE, "http://festung1.oats.inaf.it:8080/helio-uoc/HelioService?wsdl");

	    register(HelioServiceName.DPAS, HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
	    register(HelioServiceName.DPAS, HelioServiceCapability.SYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioService?wsdl");

	    register(HelioServiceName.ICS, HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioLongQueryService?wsdl");
	    register(HelioServiceName.ICS, HelioServiceCapability.SYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioService?wsdl");

	    register(HelioServiceName.ILS, HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioLongQueryService?wsdl");
	    register(HelioServiceName.ILS, HelioServiceCapability.SYNC_QUERY_SERVICE, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioService?wsdl");

	    register(HelioServiceName.MDES, HelioServiceCapability.ASYNC_QUERY_SERVICE, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService.wsdl");      
	    register(HelioServiceName.MDES, HelioServiceCapability.SYNC_QUERY_SERVICE, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioService.wsdl");       
	}

    private void register(HelioServiceName serviceName, HelioServiceCapability capability, String wsdlFile) {
        registerServiceInstance(serviceName.getName(), serviceName.getName(), null, capability, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, HelioFileUtil.asURL(wsdlFile)));
    }
}
