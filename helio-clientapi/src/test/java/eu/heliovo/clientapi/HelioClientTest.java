package eu.heliovo.clientapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.registry.impl.HelioDummyServiceRegistryClient;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;


/**
 * Unit tests for the {@link HelioClient}.
 * @author MarcoSoldati
 *
 */
public class HelioClientTest {
    /**
     * The helio client to test
     */
    private HelioClient helioClient;

    /**
     * the context
     */

    /**
     * Setup dummy registry client
     */
    @Before public void setup() {

        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        helioClient = (HelioClient) context.getBean("helioClient");
    }
    
    /**
     * Test method {@link HelioClient#getAllServiceNames()}
     */
    @Test public void testGetAllServiceNames() {
        HelioServiceName[] serviceNames = helioClient.getAllServiceNames();
        assertTrue(serviceNames.length > 1); // we have at least on service.
        assertTrue(Arrays.asList(serviceNames).contains(HelioDummyServiceRegistryClient.TEST_SERVICE));
    }

    /**
     * Test method {@link HelioClient#getServiceNamesByCapability(ServiceCapability)}
     */
    @Test public void testGetServiceNamesByCapability() {
        HelioServiceName[] serviceNames = helioClient.getServiceNamesByCapability(ServiceCapability.ASYNC_QUERY_SERVICE);
        assertEquals(1, serviceNames.length);
        assertEquals(HelioDummyServiceRegistryClient.TEST_SERVICE, serviceNames[0]);
        
        serviceNames = helioClient.getServiceNamesByCapability(ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
        assertEquals(0, serviceNames.length);
    }

    /**
     * Test method {@link HelioClient#getServiceInstance(HelioServiceName, ServiceCapability, String)}
     */
    @Test public void testGetServiceInstance() {
        HelioService service = helioClient.getServiceInstance(HelioDummyServiceRegistryClient.TEST_SERVICE, null, ServiceCapability.SYNC_QUERY_SERVICE);
        assertNotNull(service);
        assertTrue("Service should be of type " + SyncQueryService.class, service instanceof SyncQueryService);
        
        service = helioClient.getServiceInstance(HelioDummyServiceRegistryClient.TEST_SERVICE, null, ServiceCapability.ASYNC_QUERY_SERVICE);
        assertNotNull(service);
        assertTrue("Service should be of type " + AsyncQueryService.class, service instanceof AsyncQueryService);
    }
    
    /**
     * Test method {@link HelioClient#getServiceInstance(HelioServiceName, ServiceCapability, String)}
     */
    @Test public void testGetLinkProviders() {
        HelioService[] services = helioClient.getServiceInstances(ServiceCapability.LINK_PROVIDER_SERVICE);
        assertNotNull(services);
        assertEquals(4, services.length);
    }
}
