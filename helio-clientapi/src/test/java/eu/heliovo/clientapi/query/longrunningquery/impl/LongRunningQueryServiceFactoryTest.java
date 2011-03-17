package eu.heliovo.clientapi.query.longrunningquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.longrunningquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.impl.LongRunningServiceDescriptor;

/**
 * Test {@link LongRunningQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class LongRunningQueryServiceFactoryTest {

	LongRunningQueryServiceFactory instance;
	
	@Before
	public void setUp() {
		instance = LongRunningQueryServiceFactory.getInstance();
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link LongRunningQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testGetLongRunningQueryService() {	
		AsyncQueryService queryService = instance.getLongRunningQueryService(LongRunningServiceDescriptor.ASYNC_HEC);
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link LongRunningQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		HelioServiceDescriptor invalidDescriptor = new GenericHelioServiceDescriptor("test", HelioServiceType.UNKNOWN_SERVICE, "invalid service", "invalid service");
		try {
			instance.getLongRunningQueryService(invalidDescriptor);
			fail(IllegalArgumentException.class.getName() + " expected.");
		} catch (IllegalArgumentException e) {
			// fine
		}
	}
}
