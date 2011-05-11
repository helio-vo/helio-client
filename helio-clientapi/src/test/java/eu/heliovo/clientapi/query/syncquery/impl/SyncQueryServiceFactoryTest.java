package eu.heliovo.clientapi.query.syncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.clientapi.registry.impl.LocalHelioServiceRegistryDao;

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
	HelioServiceDescriptor testDescriptor = new GenericHelioServiceDescriptor("testService", "test service", "a test service descriptor", HelioServiceCapability.SYNC_QUERY_SERVICE);
	
	@Before
	public void setUp() {
		instance = SyncQueryServiceFactory.getInstance();
		assertNotNull(instance);
		String wsdlPath = "/wsdl/helio_full_query.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		assertNotNull(wsdlUrl);
		LocalHelioServiceRegistryDao.getInstance().registerServiceInstance(testDescriptor, HelioServiceCapability.SYNC_QUERY_SERVICE, wsdlUrl);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testGetSyncQueryService() {
		HelioQueryService queryService = instance.getSyncQueryService("testService");
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		//HelioServiceDescriptor invalidDescriptor = new GenericHelioServiceDescriptor("test", HelioServiceCapability.UNKNOWN, "invalid service", "invalid service");
		try {
			instance.getSyncQueryService("unknown");
			fail(ServiceResolutionException.class.getName() + " expected.");
		} catch (ServiceResolutionException e) {
			// fine
		}
	}
}
