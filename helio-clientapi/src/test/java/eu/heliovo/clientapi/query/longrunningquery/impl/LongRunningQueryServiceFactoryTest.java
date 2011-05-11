package eu.heliovo.clientapi.query.longrunningquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.longrunningquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.clientapi.registry.impl.LocalHelioServiceRegistryDao;

/**
 * Test {@link AsyncQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class LongRunningQueryServiceFactoryTest {

	AsyncQueryServiceFactory instance;
	HelioServiceDescriptor testDescriptor = new GenericHelioServiceDescriptor("testService", "test service", "a test service descriptor", HelioServiceCapability.ASYNC_QUERY_SERVICE);
	
	@Before
	public void setUp() throws Exception {
		instance = AsyncQueryServiceFactory.getInstance();
		String wsdlPath = "/wsdl/long_runningquery.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		LocalHelioServiceRegistryDao.getInstance().registerServiceInstance(testDescriptor, HelioServiceCapability.ASYNC_QUERY_SERVICE, wsdlUrl);
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link AsyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testGetLongRunningQueryService() {	
		AsyncQueryService queryService = instance.getAsyncQueryService("testService");
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link AsyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.HelioServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		//HelioServiceDescriptor invalidDescriptor = new GenericHelioServiceDescriptor("bad", "invalid service", "invalid service", HelioServiceCapability.UNKNOWN);
		try {
			instance.getAsyncQueryService("bad");
			fail(ServiceResolutionException.class.getName() + " expected.");
		} catch (ServiceResolutionException e) {
			// fine
		}
	}
}
