package eu.heliovo.clientapi.model.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * 
 * Unit tests for the {@link ServiceVariantRegistry}.
 * @author MarcoSoldati
 *
 */
public class ServiceVariantRegistryTest {
    
    /**
     * Test the valid registration process.
     */
    @Test public void testValidRegistration() {
        ServiceVariantRegistry registry = new ServiceVariantRegistry();
        registry.register(HelioServiceName.ICS, "testVariant1", ServiceCapability.ASYNC_QUERY_SERVICE, "bean1");
        registry.register(HelioServiceName.ICS, "testVariant2", ServiceCapability.ASYNC_QUERY_SERVICE, "bean2");
        registry.register(HelioServiceName.ICS, null, ServiceCapability.ASYNC_QUERY_SERVICE, "bean3");
        registry.register(null, "testVariant3", ServiceCapability.ASYNC_QUERY_SERVICE, "bean5");
        
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant1", ServiceCapability.ASYNC_QUERY_SERVICE));
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant2", ServiceCapability.ASYNC_QUERY_SERVICE));
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, null, ServiceCapability.ASYNC_QUERY_SERVICE));
        assertEquals("bean5", registry.getServiceImpl(HelioServiceName.ICS, "testVariant3", ServiceCapability.ASYNC_QUERY_SERVICE));
    }
    
    /**
     * Test duplicate registration.
     */
    @Test public void testInvalidRegistration() {
        ServiceVariantRegistry registry = new ServiceVariantRegistry();
        registry.register(HelioServiceName.ICS, "testVariant1", ServiceCapability.ASYNC_QUERY_SERVICE, "bean1");
        registry.register(HelioServiceName.ICS, "testVariant2", ServiceCapability.ASYNC_QUERY_SERVICE, "bean2");
        
        // try duplicate registration
        try {
            registry.register(HelioServiceName.ICS, "testVariant2", ServiceCapability.ASYNC_QUERY_SERVICE, "bean2");
            fail(IllegalArgumentException.class.getName() + " expected.");
        } catch (IllegalArgumentException e) {
            // ignore
        }
        // try invalid registration
        try {
            registry.register(null, null, null, "bean4");
            fail(IllegalArgumentException.class.getName() + " expected.");
        } catch (IllegalArgumentException e) {
            // ignore
        }
        
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant1", ServiceCapability.ASYNC_QUERY_SERVICE));
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant2", ServiceCapability.ASYNC_QUERY_SERVICE));
    }
}
