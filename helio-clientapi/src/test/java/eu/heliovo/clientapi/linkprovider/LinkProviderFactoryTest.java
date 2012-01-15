package eu.heliovo.clientapi.linkprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.activation.MimeType;

import org.junit.Test;

import eu.heliovo.clientapi.linkprovider.impl.MimeTypeConstants;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;

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
        LinkProviderFactory factory = LinkProviderFactory.getInstance();
        assertNotNull(factory);
        
        String[] names = factory.getServiceVariants(HelioServiceName.LPS);
        assertNotNull(names);
        assertTrue(names.length > 0);
        
        for (String name : names) {
            LinkProviderService provider = factory.getLinkProvider(name);
            assertEquals(name, provider.getServiceVariant());
        }
        
        LinkProviderService[] linkProviders = factory.getLinkProviders();
        assertNotNull(linkProviders);
        assertEquals(names.length, linkProviders.length);
        
    }
    
    /**
     * This is a sample how to use solar monitor as service.
     */
    @Test public void testSolarMonitor() {
        // get the factory
        LinkProviderFactory factory = LinkProviderFactory.getInstance();
        assertNotNull(factory);
        
        // and get solar monitor link provider
        LinkProviderService solarmonitor = factory.getHelioService(HelioServiceName.LPS, "helio://helio-vo.eu/lps/solarmonitor", (AccessInterface[])null);
        assertNotNull(solarmonitor);
        
        // now display the generic title
        assertNotNull(solarmonitor.getTitle());

        // and the more verbose description
        assertNotNull(solarmonitor.getDescription());
        
        // now init a date range
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2011, Calendar.SEPTEMBER, 15);
        Date startTime = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date endTime = cal.getTime();

        // get the specific title for the given date range (only the start date will be taken into account)
        String title = solarmonitor.getTitle(startTime, endTime);
        assertNotNull(title);
        assertEquals("SolarMonitor for 15-Sep-2011", title);
        
        // get the mime type
        MimeType mimeType = solarmonitor.getMimeType();
        assertNotNull(mimeType);
        assertEquals(MimeTypeConstants.TEXT_HTML, mimeType);
        
        // get the actual link pointing to the page.
        URL link = solarmonitor.getLink(startTime, endTime);
        assertNotNull(link);
        assertEquals(HelioFileUtil.asURL("http://solarmonitor.org/index.php?date=20110915"), link);
    }
    
}
