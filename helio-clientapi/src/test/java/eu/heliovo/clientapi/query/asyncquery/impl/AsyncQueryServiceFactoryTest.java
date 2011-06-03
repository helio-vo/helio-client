package eu.heliovo.clientapi.query.asyncquery.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.impl.HelioDummyServiceRegistryClient;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

/**
 * Test {@link AsyncQueryServiceFactory}
 * @author MarcoSoldati
 *
 */
public class AsyncQueryServiceFactoryTest {

	AsyncQueryServiceFactory instance;
	ServiceDescriptor testDescriptor = new GenericServiceDescriptor("testService", "test service", "a test service descriptor", ServiceCapability.ASYNC_QUERY_SERVICE);
	
	@Before
	public void setUp() throws Exception {
		String wsdlPath = "/wsdl/long_runningquery.wsdl";
		URL wsdlUrl = getClass().getResource(wsdlPath);
		assertNotNull(wsdlUrl);

		HelioDummyServiceRegistryClient ServiceRegistryClient = HelioDummyServiceRegistryClient.getInstance();
		ServiceRegistryClient.registerServiceInstance(testDescriptor, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE, wsdlUrl));
		ServiceRegistryClientFactory.getInstance().setServiceRegistryClient(ServiceRegistryClient);      
		instance = AsyncQueryServiceFactory.getInstance();
		assertNotNull(instance);
	}
	
	/**
	 * Test {@link AsyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.ServiceDescriptor)}
	 */
	@Test public void testGetLongRunningQueryService() {	
		AsyncQueryService queryService = instance.getAsyncQueryService("testService");
		assertNotNull(queryService);
	}
	
	/**
	 * Test {@link AsyncQueryServiceFactory#getAsyncQueryService(eu.heliovo.clientapi.registry.ServiceDescriptor)}
	 */
	@Test public void testInvalidRequest() {
		//ServiceDescriptor invalidDescriptor = new GenericServiceDescriptor("bad", "invalid service", "invalid service", ServiceCapability.UNKNOWN);
		try {
			instance.getAsyncQueryService("bad");
			fail(ServiceResolutionException.class.getName() + " expected.");
		} catch (ServiceResolutionException e) {
			// fine
		}
	}
}
