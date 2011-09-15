package eu.heliovo.clientapi.linkprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the {@link LinkProviderFactory}.
 * @author MarcoSoldati
 *
 */
public class LinkProviderFactoryTest {

    /**
     * Test the provider factory
     */
    @Test public void testLinkProviderFactory() {
        LinkProviderFactory instance = LinkProviderFactory.getInstance();
        assertNotNull(instance);
        
        String[] names = instance.getLinkProviderNames();
        assertNotNull(names);
        assertTrue(names.length > 0);
        
        for (String name : names) {
            LinkProvider provider = instance.getLinkProvider(name);
            assertEquals(name, provider.getName());
        }
        
        LinkProvider[] linkProviders = instance.getLinkProviders();
        assertNotNull(linkProviders);
        assertEquals(names.length, linkProviders.length);
    }
    
}
