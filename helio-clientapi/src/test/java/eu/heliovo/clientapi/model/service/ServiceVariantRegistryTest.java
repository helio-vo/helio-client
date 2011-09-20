package eu.heliovo.clientapi.model.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import eu.heliovo.registryclient.HelioServiceName;

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
        registry.register(HelioServiceName.ICS, "testVariant1", HelioService.class);
        registry.register(HelioServiceName.ICS, "testVariant2", HelioService.class);
        registry.register(HelioServiceName.ICS, null, HelioService.class);
        registry.register(null, null, HelioService.class);
        registry.register(null, "testVariant3", HelioService.class);
        
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant1"));
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant2"));
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, null));
        assertNotNull(registry.getServiceImpl(null, null));
        assertNotNull(registry.getServiceImpl(null, "testVariant3"));
        assertNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant3"));
    }
    
    /**
     * Test duplicate registration.
     */
    @Test public void testInvalidRegistration() {
        ServiceVariantRegistry registry = new ServiceVariantRegistry();
        registry.register(HelioServiceName.ICS, "testVariant1", HelioService.class);
        registry.register(HelioServiceName.ICS, "testVariant2", HelioService.class);
        
        // try duplicate registration
        try {
            registry.register(HelioServiceName.ICS, "testVariant2", HelioService.class);
            fail(IllegalArgumentException.class.getName() + " expected.");
        } catch (IllegalArgumentException e) {
            // ignore
        }

        
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant1"));
        assertNotNull(registry.getServiceImpl(HelioServiceName.ICS, "testVariant2"));

        
    }
}
