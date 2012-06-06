package eu.heliovo.clientapi.linkprovider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import eu.heliovo.shared.util.FileUtil;

/**
 * Test the {@link RadioMonitorLinkProvider}
 * @author MarcoSoldati
 *
 */
public class RadioMonitorLinkProviderTest {

    /**
     * Test the provider.
     */
    @Test public void testLinkProvider() {
        RadioMonitorLinkProvider provider = new RadioMonitorLinkProvider();
        assertNotNull(provider.getServiceName());
        assertNotNull(provider.getDescription());
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2011, Calendar.SEPTEMBER, 15);
        Date startTime = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date endTime = cal.getTime();
        URL link = provider.getLink(startTime, endTime);
        assertNotNull(link);
        assertEquals(FileUtil.asURL("http://secchirh.obspm.fr/survey.php?hour=day&dayofyear=20110915&composite=1"), link);
        
        String title = provider.getTitle(startTime, endTime);
        assertNotNull(title);
        assertEquals("Radio monitor composites for 15-Sep-2011", title);
    }
    
    /**
     * Make sure that getLink returns null for unsupported dates.
     */
    @Test public void testOldDate() {
        RadioMonitorLinkProvider provider = new RadioMonitorLinkProvider();
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(1998, Calendar.JANUARY, 16);
        Date startTime = cal.getTime();
        Date endTime = cal.getTime();
        URL link = provider.getLink(startTime, endTime);
        assertNotNull(link);
        
        cal.set(Calendar.YEAR, -2);
        startTime = cal.getTime();
        endTime = cal.getTime();
        link = provider.getLink(startTime, endTime);
        assertNull(link);
        
        String title = provider.getTitle(startTime, endTime);
        assertNull(title);        
    }
}
