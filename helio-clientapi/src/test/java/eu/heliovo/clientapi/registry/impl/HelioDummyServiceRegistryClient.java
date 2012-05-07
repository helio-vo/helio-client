package eu.heliovo.clientapi.registry.impl;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.AbstractHelioServiceRegistryClient;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;


/**
 * Dummy implementation of a test registry for testing purposes only
 * @author MarcoSoldati
 *
 */
public class HelioDummyServiceRegistryClient extends AbstractHelioServiceRegistryClient implements ServiceRegistryClient {
    /**
     * A test service name
     */
    public static final HelioServiceName TEST_SERVICE = HelioServiceName.register("testService", "ivo://test");
    
    /** 
     * A test service descriptor
     */
    public static final ServiceDescriptor TEST_DESCRIPTOR = new GenericServiceDescriptor(TEST_SERVICE, "a test service descriptor", ServiceCapability.SYNC_QUERY_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE);

    /**
     * Create the dummy service registry client.
     */
    public HelioDummyServiceRegistryClient() {
       
    }
    
    /**
     * Register some test services
     */
    public void init() {
        String wsdlPath = "/wsdl/long_runningquery.wsdl";
        URL wsdlUrl = getClass().getResource(wsdlPath);
        assertNotNull(wsdlUrl);
        registerServiceInstance(TEST_DESCRIPTOR, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE, wsdlUrl));
        
        wsdlPath = "/wsdl/helio_full_query.wsdl";
        wsdlUrl = getClass().getResource(wsdlPath);
        assertNotNull(wsdlUrl);
        registerServiceInstance(TEST_DESCRIPTOR, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.SYNC_QUERY_SERVICE, wsdlUrl));
    }
}