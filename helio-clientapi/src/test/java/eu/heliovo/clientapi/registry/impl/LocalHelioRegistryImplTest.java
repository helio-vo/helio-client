package eu.heliovo.clientapi.registry.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.registry.AccessInterface;
import eu.heliovo.clientapi.registry.AccessInterfaceType;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;

/**
 * Unit test for the {@link LocalHelioServiceRegistryDao}.
 */
public class LocalHelioRegistryImplTest {

    private AbstractHelioServiceRegistryDao helioRegistry;
    
    
    @Before public void setUp() {
        helioRegistry = new LocalHelioServiceRegistryDao();
        //HelioRegistryDaoFactory.getInstance().setHelioServiceRegistryDao(helioRegistry);
    }
    
	@Test public void initialize() {
		assertNotNull(helioRegistry);
		HelioServiceDescriptor[] serviceDescriptors = helioRegistry.getAllServiceDescriptors();
		assertNotNull(serviceDescriptors);
		for (HelioServiceDescriptor helioServiceDescriptor : serviceDescriptors) {
			AccessInterface[] endpoints = helioRegistry.getAllEndpoints(helioServiceDescriptor);
			assertNotNull(endpoints);
			for (AccessInterface url : endpoints) {
				assertNotNull(url);
			}
		}
	}
	
	/**
	 * Test {@link LocalHelioServiceRegistryDao#registerServiceDescriptor(GenericHelioServiceDescriptor)}.
	 */
	@Test public void testRegisterServiceDescriptor() {
		// register new service
		HelioServiceDescriptor descriptor = new GenericHelioServiceDescriptor("test", "test service", "a test service", HelioServiceCapability.UNKNOWN);
		HelioServiceDescriptor descriptor2 = helioRegistry.registerServiceDescriptor(descriptor);
		assertSame(descriptor, descriptor2);
		
		assertNotNull(helioRegistry.getServiceDescriptor("test"));
		
		// register a service with the same name and type
		descriptor2 = new GenericHelioServiceDescriptor("test", "test service", "a test service", HelioServiceCapability.UNKNOWN);
		HelioServiceDescriptor descriptor3 = helioRegistry.registerServiceDescriptor(descriptor2);
		assertNotSame(descriptor2, descriptor3);		
	}
	
	/**
	 * Test {@link LocalHelioServiceRegistryDao#registerServiceInstance(HelioServiceDescriptor, URL...)}
	 * @throws Exception if anything goes wrong
	 */
	@Test public void testRegisterServiceInstanceDescriptor() throws Exception {
		
		// create service descriptor
		HelioServiceDescriptor descriptor = new GenericHelioServiceDescriptor("test2", "test service", "a test service", HelioServiceCapability.UNKNOWN);
		
		// create service instance descriptor
		helioRegistry.registerServiceInstance(descriptor, HelioServiceCapability.UNKNOWN, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, new URL("http://www.example.com/test.wsdl")));
		
		assertNotNull(helioRegistry.getServiceDescriptor("test2"));
		
		// register a service with the same name and type
		assertFalse(helioRegistry.registerServiceInstance(descriptor, HelioServiceCapability.UNKNOWN, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, new URL("http://www.example.com/test.wsdl"))));
	}
	
	/**
	 * Test method {@link LocalHelioServiceRegistryDao#getBestEndpoint(GenericHelioServiceDescriptor)}.
	 * @throws Exception in case of an error
	 */
	@Test public void testGetBestEndpoint() throws Exception {
		HelioServiceDescriptor descriptor = new GenericHelioServiceDescriptor("test3", "test service", "a test service", HelioServiceCapability.UNKNOWN);
		assertNotNull(helioRegistry.registerServiceDescriptor(descriptor));
		assertTrue(helioRegistry.registerServiceInstance(descriptor, HelioServiceCapability.UNKNOWN, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, new URL("http://www.example.com/test2.wsdl"))));
		assertTrue(helioRegistry.registerServiceInstance(descriptor, HelioServiceCapability.UNKNOWN, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, new URL("http://www.example.com/test3.wsdl"))));
		assertTrue(helioRegistry.registerServiceInstance(descriptor, HelioServiceCapability.UNKNOWN, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, new URL("http://www.example.com/test1.wsdl"))));
		
		AccessInterface bestEndPoint = helioRegistry.getBestEndpoint(descriptor, HelioServiceCapability.UNKNOWN, AccessInterfaceType.SOAP_SERVICE);
		assertNotNull(bestEndPoint);
		assertEquals(new URL("http://www.example.com/test2.wsdl"), bestEndPoint.getUrl());
		
		bestEndPoint = helioRegistry.getBestEndpoint("test3", HelioServiceCapability.UNKNOWN, AccessInterfaceType.SOAP_SERVICE);
		assertNotNull(bestEndPoint);
		assertEquals(new URL("http://www.example.com/test2.wsdl"), bestEndPoint.getUrl());		
		
		AccessInterface[] allEndpoints = helioRegistry.getAllEndpoints(descriptor);
		URL[] bestEndPoints = new URL[allEndpoints.length];
		for (int i = 0; i < allEndpoints.length; i++) {
            bestEndPoints[i] = allEndpoints[i].getUrl();
        }
		assertNotNull(bestEndPoints);
		assertArrayEquals(new URL[] {new URL("http://www.example.com/test2.wsdl"), new URL("http://www.example.com/test3.wsdl"), new URL("http://www.example.com/test1.wsdl")}, bestEndPoints);
	}
}
