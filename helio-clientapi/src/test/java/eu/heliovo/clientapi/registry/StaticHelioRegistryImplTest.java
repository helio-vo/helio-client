package eu.heliovo.clientapi.registry;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

import eu.heliovo.clientapi.registry.impl.StaticHelioRegistryImpl;

/**
 * Unit test for the {@link StaticHelioRegistryImpl}.
 */
public class StaticHelioRegistryImplTest {

	@Test public void initialize() {
		StaticHelioRegistryImpl helioRegistry = StaticHelioRegistryImpl.getInstance();
		
		assertNotNull(helioRegistry);
		HelioServiceDescriptor[] serviceDescriptors = helioRegistry.getAllServiceDescriptors();
		assertNotNull(serviceDescriptors);
		for (HelioServiceDescriptor helioServiceDescriptor : serviceDescriptors) {
			URL[] endpoints = helioRegistry.getAllEndpoints(helioServiceDescriptor);
			assertNotNull(endpoints);
			for (URL url : endpoints) {
				assertNotNull(url);
			}
		}
	}
	
	/**
	 * Test {@link StaticHelioRegistryImpl#registerServiceDescriptor(GenericHelioServiceDescriptor)}.
	 */
	@Test public void testRegisterServiceDescriptor() {
		StaticHelioRegistryImpl helioRegistry = StaticHelioRegistryImpl.getInstance();
		
		// register new service
		GenericHelioServiceDescriptor descriptor = new GenericHelioServiceDescriptor("test", HelioServiceType.UNKNOWN_SERVICE, "test service", "a test service");
		assertTrue(helioRegistry.registerServiceDescriptor(descriptor));
		
		assertNotNull(helioRegistry.getServiceDescriptor("test", HelioServiceType.UNKNOWN_SERVICE));
		
		// register a service with the same name and type
		GenericHelioServiceDescriptor descriptor2 = new GenericHelioServiceDescriptor("test", HelioServiceType.UNKNOWN_SERVICE, "test service", "a test service");
		assertFalse(helioRegistry.registerServiceDescriptor(descriptor2));
		
	}
	
	/**
	 * Test {@link StaticHelioRegistryImpl#registerServiceInstanceDescriptor(HelioServiceInstanceDescriptor)}
	 * @throws Exception if anything goes wrong
	 */
	@Test public void testRegisterServiceInstanceDescriptor() throws Exception {
		StaticHelioRegistryImpl helioRegistry = StaticHelioRegistryImpl.getInstance();
		
		// create service descriptor
		GenericHelioServiceDescriptor descriptor = new GenericHelioServiceDescriptor("test2", HelioServiceType.UNKNOWN_SERVICE, "test service", "a test service");
		
		// create service instance descriptor
		HelioServiceInstanceDescriptor instanceDescriptor = new HelioServiceInstanceDescriptor(descriptor, new URL("http://www.example.com/test.wsdl"));
		try {
			helioRegistry.registerServiceInstanceDescriptor(instanceDescriptor);
			fail(ServiceResolutionException.class.getName() + " expected.");
		} catch (ServiceResolutionException e) {
			// fine
		}
		
		// now register the descriptor and then try again.
		assertTrue(helioRegistry.registerServiceDescriptor(descriptor));
		assertTrue(helioRegistry.registerServiceInstanceDescriptor(instanceDescriptor));

		
		
		assertNotNull(helioRegistry.getServiceDescriptor("test", HelioServiceType.UNKNOWN_SERVICE));
		
		// register a service with the same name and type
		HelioServiceInstanceDescriptor instanceDescriptor2 = new HelioServiceInstanceDescriptor(descriptor, new URL("http://www.example.com/test.wsdl"));
		assertFalse(helioRegistry.registerServiceInstanceDescriptor(instanceDescriptor2));
	}
	
	/**
	 * Test method {@link StaticHelioRegistryImpl#getBestEndpoint(GenericHelioServiceDescriptor)}.
	 * @throws Exception in case of an error
	 */
	@Test public void testGetBestEndpoint() throws Exception {
		StaticHelioRegistryImpl helioRegistry = StaticHelioRegistryImpl.getInstance();
		GenericHelioServiceDescriptor descriptor = new GenericHelioServiceDescriptor("test3", HelioServiceType.UNKNOWN_SERVICE, "test service", "a test service");
		assertTrue(helioRegistry.registerServiceDescriptor(descriptor));
		assertTrue(helioRegistry.registerServiceInstanceDescriptor(new HelioServiceInstanceDescriptor(descriptor, new URL("http://www.example.com/test2.wsdl"))));
		assertTrue(helioRegistry.registerServiceInstanceDescriptor(new HelioServiceInstanceDescriptor(descriptor, new URL("http://www.example.com/test3.wsdl"))));
		assertTrue(helioRegistry.registerServiceInstanceDescriptor(new HelioServiceInstanceDescriptor(descriptor, new URL("http://www.example.com/test1.wsdl"))));
		
		URL bestEndPoint = helioRegistry.getBestEndpoint(descriptor);
		assertNotNull(bestEndPoint);
		assertEquals(new URL("http://www.example.com/test2.wsdl"), bestEndPoint);
		
		bestEndPoint = helioRegistry.getBestEndpoint("test3", HelioServiceType.UNKNOWN_SERVICE);
		assertNotNull(bestEndPoint);
		assertEquals(new URL("http://www.example.com/test2.wsdl"), bestEndPoint);		
		
		URL[] bestEndPoints = helioRegistry.getAllEndpoints(descriptor);
		assertNotNull(bestEndPoints);
		assertArrayEquals(new URL[] {new URL("http://www.example.com/test2.wsdl"), new URL("http://www.example.com/test3.wsdl"), new URL("http://www.example.com/test1.wsdl")}, bestEndPoints);
	}
}
