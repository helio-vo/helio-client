package eu.heliovo.clientapi.query.syncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.registry.impl.HelioDummyServiceRegistryClient;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

/**
 * Test {@link SyncQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class SyncQueryServiceFactoryTest {

    /**
     * Factory instance
     */
	SyncQueryServiceFactory instance;

	/**
	 * a test service.
	 */
	HelioServiceName testService = HelioServiceName.register("test", "ivo://test");

	/**
	 * Descriptor for testing purposes
	 */
	ServiceDescriptor testDescriptor;
	
	/**
	 * Init dummy registry.
	 */
	@Before
	public void setUp() {
	    testDescriptor = new GenericServiceDescriptor(testService, "a test service descriptor", ServiceCapability.SYNC_QUERY_SERVICE);
        
		String wsdlPath = "/ws/wsdl/helio_full_query.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		assertNotNull(wsdlUrl);
		
		HelioDummyServiceRegistryClient serviceRegistryClient = new HelioDummyServiceRegistryClient();
		serviceRegistryClient.registerServiceInstance(testDescriptor, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.SYNC_QUERY_SERVICE, wsdlUrl));
		ServiceRegistryClientFactory.getInstance().setServiceRegistryClient(serviceRegistryClient);		

		instance = SyncQueryServiceFactory.getInstance();
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getHelioService(HelioServiceName, String, eu.heliovo.registryclient.AccessInterface...)}
	 */
	@Test public void testGetSyncQueryService() {
		HelioQueryService queryService = instance.getSyncQueryService(testService);
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getSyncQueryService(HelioServiceName, eu.heliovo.registryclient.AccessInterface...)}
	 */
	@Test public void testInvalidRequest() {
		//ServiceDescriptor invalidDescriptor = new GenericServiceDescriptor("test", ServiceCapability.UNKNOWN, "invalid service", "invalid service");
	    HelioServiceName unknownService = HelioServiceName.register("unknown", "ivo://unknown");
		try {
			instance.getSyncQueryService(unknownService);
			fail(ServiceResolutionException.class.getName() + " expected.");
		} catch (ServiceResolutionException e) {
			// fine
		}
	}
}
