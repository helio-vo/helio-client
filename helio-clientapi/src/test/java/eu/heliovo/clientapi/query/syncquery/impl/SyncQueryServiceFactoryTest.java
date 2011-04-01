package eu.heliovo.clientapi.query.syncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.impl.StaticHelioRegistryImpl;

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
	HelioServiceDescriptor testDescriptor = new GenericHelioServiceDescriptor("testService", HelioServiceType.SYNC_QUERY_SERVICE, "test service", "a test service descriptor");
	
	@Before
	public void setUp() {
		instance = SyncQueryServiceFactory.getInstance();
		assertNotNull(instance);
		String wsdlPath = "/wsdl/helio_full_query.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		StaticHelioRegistryImpl.getInstance().registerServiceInstance(testDescriptor, wsdlUrl);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testGetSyncQueryService() {
		HelioQueryService queryService = instance.getSyncQueryService(testDescriptor);		
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		HelioServiceDescriptor invalidDescriptor = new GenericHelioServiceDescriptor("test", HelioServiceType.UNKNOWN_SERVICE, "invalid service", "invalid service");
		try {
			instance.getSyncQueryService(invalidDescriptor);
			fail(IllegalArgumentException.class.getName() + " expected.");
		} catch (IllegalArgumentException e) {
			// fine
		}
	}
}
