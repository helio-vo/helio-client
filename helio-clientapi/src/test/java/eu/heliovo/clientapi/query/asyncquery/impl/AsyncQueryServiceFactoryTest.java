package eu.heliovo.clientapi.query.asyncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.AccessInterfaceType;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.clientapi.registry.impl.AccessInterfaceImpl;
import eu.heliovo.clientapi.registry.impl.DummyHelioServiceRegistryDao;
import eu.heliovo.clientapi.registry.impl.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.impl.HelioServiceRegistryDaoFactory;

/**
 * Test {@link AsyncQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class AsyncQueryServiceFactoryTest {

	AsyncQueryServiceFactory instance;
	HelioServiceDescriptor testDescriptor = new GenericHelioServiceDescriptor("testService", "test service", "a test service descriptor", HelioServiceCapability.ASYNC_QUERY_SERVICE);
	
	@Before
	public void setUp() throws Exception {
		String wsdlPath = "/wsdl/long_runningquery.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		assertNotNull(wsdlUrl);

		DummyHelioServiceRegistryDao helioServiceRegistryDao = DummyHelioServiceRegistryDao.getInstance();
		helioServiceRegistryDao.registerServiceInstance(testDescriptor, HelioServiceCapability.ASYNC_QUERY_SERVICE, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, wsdlUrl));
		HelioServiceRegistryDaoFactory.getInstance().setHelioServiceRegistryDao(helioServiceRegistryDao);      
		instance = AsyncQueryServiceFactory.getInstance();
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
