package eu.heliovo.clientapi.query.syncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.registry.impl.HelioDummyServiceRegistryClient;
import eu.heliovo.registryclient.AccessInterfaceType;
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

	SyncQueryServiceFactory instance;

	/**
	 * Descriptor for testing purposes
	 */
	ServiceDescriptor testDescriptor = new GenericServiceDescriptor("testService", "test service", "a test service descriptor", ServiceCapability.SYNC_QUERY_SERVICE);
		
	@Before
	public void setUp() {
		String wsdlPath = "/wsdl/helio_full_query.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		assertNotNull(wsdlUrl);
		
		HelioDummyServiceRegistryClient ServiceRegistryClient = HelioDummyServiceRegistryClient.getInstance();
		ServiceRegistryClient.registerServiceInstance(testDescriptor, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.SYNC_QUERY_SERVICE, wsdlUrl));
		ServiceRegistryClientFactory.getInstance().setServiceRegistryClient(ServiceRegistryClient);		

		instance = SyncQueryServiceFactory.getInstance();
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.ServiceDescriptor)}
	 */
	@Test public void testGetSyncQueryService() {
		HelioQueryService queryService = instance.getSyncQueryService("testService");
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.ServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		//ServiceDescriptor invalidDescriptor = new GenericServiceDescriptor("test", ServiceCapability.UNKNOWN, "invalid service", "invalid service");
		try {
			instance.getSyncQueryService("unknown");
			fail(ServiceResolutionException.class.getName() + " expected.");
		} catch (ServiceResolutionException e) {
			// fine
		}
	}
}
