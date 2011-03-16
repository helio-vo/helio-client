package eu.heliovo.clientapi.query.syncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.impl.SyncServiceDescriptor;

/**
 * Test {@link SyncQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class SyncQueryServiceFactoryTest {

	SyncQueryServiceFactory instance;
	
	@Before
	public void setUp() {
		instance = SyncQueryServiceFactory.getInstance();
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link SyncQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testGetSyncQueryService() {
		SyncQueryService queryService = instance.getSyncQueryService(SyncServiceDescriptor.SYNC_ICS);
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
