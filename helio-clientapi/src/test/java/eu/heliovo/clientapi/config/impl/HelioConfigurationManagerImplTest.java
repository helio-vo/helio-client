package eu.heliovo.clientapi.config.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.config.HelioPropertyHandler;
import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

public class HelioConfigurationManagerImplTest {
    
    private static final HelioServiceName TEST_SERVICE = HelioServiceName.register("TEST", "test"); 
    
    /**
     * Init the tests
     */
    @Before public void setup() {
        
    }
    
    @Test public void testCreateInstance() {
        HelioConfigurationManagerImpl impl = new HelioConfigurationManagerImpl();
        Collection<HelioPropertyHandler> propertyHandlers = createMockPropertyHandlers(); 
        impl.setPropertyHandlers(propertyHandlers);
        
        List<? extends HelioCatalogueDescriptor> catalogueDescriptors = impl.getCatalogueDescriptors(TEST_SERVICE, null);
        assertNotNull(catalogueDescriptors);
        assertEquals(0, catalogueDescriptors.size());
    }

    private Collection<HelioPropertyHandler> createMockPropertyHandlers() {
        List<HelioPropertyHandler> ret = new ArrayList<HelioPropertyHandler>();
        ret.add(new MockPropertyHandler());
        return ret;
    }
        
    static class MockPropertyHandler implements HelioPropertyHandler {

        @Override
        public HelioServiceName getHelioServiceName() {
            return TEST_SERVICE;
        }

        @Override
        public String getServiceVariant() {
            return null;
        }

        @Override
        public String getPropertyName() {
            return "foo";
        }

        @Override
        public void init() {
        }

        @Override
        public PropertyDescriptor getPropertyDescriptor(Class<? extends HelioService> serviceClass) {
            try {
                return new ConfigurablePropertyDescriptor<String>("foo", TestService.class);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static class TestService implements HelioService{

        private String foo;
        
        @Override
        public HelioServiceName getServiceName() {
            return TEST_SERVICE;
        }

        @Override
        public boolean supportsCapability(ServiceCapability capability) {
            return true;
        }

        @Override
        public String getServiceVariant() {
            return null;
        }

        /**
         * @return the foo
         */
        public String getFoo() {
            return foo;
        }

        /**
         * @param foo the foo to set
         */
        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}
