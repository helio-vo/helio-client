package eu.heliovo.clientapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.registry.impl.HelioDummyServiceRegistryClient;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;


/**
 * Unit tests for the {@link HelioClient}.
 * @author MarcoSoldati
 *
 */
public class HelioClientTest {
    /**
     * A test service name
     */
    private HelioServiceName testService = HelioServiceName.register("testService", "ivo://test");
    
    /** 
     * A test service descriptor
     */
    private ServiceDescriptor testDescriptor = new GenericServiceDescriptor(testService, "a test service descriptor", ServiceCapability.ASYNC_QUERY_SERVICE);

    /**
     * Setup dummy registry client
     */
    @Before public void setup() {
        String wsdlPath = "/wsdl/long_runningquery.wsdl";
        URL wsdlUrl = getClass().getResource(wsdlPath);
        assertNotNull(wsdlUrl);

        HelioDummyServiceRegistryClient serviceRegistryClient = new HelioDummyServiceRegistryClient();
        serviceRegistryClient.registerServiceInstance(testDescriptor, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE, wsdlUrl));
        ServiceRegistryClientFactory.getInstance().setServiceRegistryClient(serviceRegistryClient);      
    }
    
    /**
     * Test method {@link HelioClient#getAllServiceNames()}
     */
    @Test public void testGetAllServiceNames() {
        HelioClient helioClient = new HelioClient();
        HelioServiceName[] serviceNames = helioClient.getAllServiceNames();
        assertTrue(serviceNames.length > 1); // we have at least on service.
        assertTrue(Arrays.asList(serviceNames).contains(testService));
    }

    /**
     * Test method {@link HelioClient#getServiceNamesByCapability(ServiceCapability)}
     */
    @Test public void testGetServiceNamesByCapability() {
        HelioClient helioClient = new HelioClient();
        HelioServiceName[] serviceNames = helioClient.getServiceNamesByCapability(ServiceCapability.ASYNC_QUERY_SERVICE);
        assertEquals(1, serviceNames.length);
        assertEquals(testService, serviceNames[0]);
        
        serviceNames = helioClient.getServiceNamesByCapability(ServiceCapability.SYNC_QUERY_SERVICE);
        System.out.println(Arrays.toString(serviceNames));
        assertEquals(0, serviceNames.length);
    }

    /**
     * Test method {@link HelioClient#getServiceInstance(HelioServiceName, String, eu.heliovo.registryclient.AccessInterface...)}
     */
    @Test public void testGetServiceInstance() {
        HelioClient helioClient = new HelioClient();
        HelioService service = helioClient.getServiceInstance(testService, null, (AccessInterface[])null);
        assertNotNull(service);
    }
    
}
