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
 * Test the {@link SolarMonitorLinkProvider}
 * @author MarcoSoldati
 *
 */
public class SolarMonitorLinkProviderTest {

    /**
     * Test the provider.
     */
    @Test public void testLinkProvider() {
        SolarMonitorLinkProvider provider = new SolarMonitorLinkProvider();
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
        assertEquals(FileUtil.asURL("http://solarmonitor.org/index.php?date=20110915"), link);
        
        String title = provider.getTitle(startTime, endTime);
        assertNotNull(title);
        assertEquals("SolarMonitor for 15-Sep-2011", title);
    }
    
    /**
     * Make sure that getLink returns null for unsupported dates.
     */
    @Test public void testOldDate() {
        SolarMonitorLinkProvider provider = new SolarMonitorLinkProvider();
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(1996, Calendar.JANUARY, 16);
        Date startTime = cal.getTime();
        Date endTime = cal.getTime();
        URL link = provider.getLink(startTime, endTime);
        assertNotNull(link);
        
        cal.set(Calendar.DATE, -1);
        startTime = cal.getTime();
        endTime = cal.getTime();
        link = provider.getLink(startTime, endTime);
        assertNull(link);
        
        String title = provider.getTitle(startTime, endTime);
        assertNull(title);        
    }
}
