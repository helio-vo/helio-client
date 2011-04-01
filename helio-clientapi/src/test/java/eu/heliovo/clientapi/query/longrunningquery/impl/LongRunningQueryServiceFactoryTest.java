package eu.heliovo.clientapi.query.longrunningquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.longrunningquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.impl.StaticHelioRegistryImpl;

/**
 * Test {@link LongRunningQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class LongRunningQueryServiceFactoryTest {

	LongRunningQueryServiceFactory instance;
	HelioServiceDescriptor testDescriptor = new GenericHelioServiceDescriptor("testService", HelioServiceType.LONGRUNNING_QUERY_SERVICE, "test service", "a test service descriptor");
	
	@Before
	public void setUp() throws Exception {
		instance = LongRunningQueryServiceFactory.getInstance();
		String wsdlPath = "/wsdl/long_runningquery.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		StaticHelioRegistryImpl.getInstance().registerServiceInstance(testDescriptor, wsdlUrl);
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link LongRunningQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testGetLongRunningQueryService() {	
		AsyncQueryService queryService = instance.getLongRunningQueryService(testDescriptor);
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link LongRunningQueryServiceFactory#getLongRunningQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		HelioServiceDescriptor invalidDescriptor = new GenericHelioServiceDescriptor("bad", HelioServiceType.UNKNOWN_SERVICE, "invalid service", "invalid service");
		try {
			instance.getLongRunningQueryService(invalidDescriptor);
			fail(IllegalArgumentException.class.getName() + " expected.");
		} catch (IllegalArgumentException e) {
			// fine
		}
	}
}
