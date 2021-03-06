package eu.heliovo.clientapi.linkprovider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import eu.heliovo.shared.util.FileUtil;

/**
 * Test the {@link SpaceWeatherLinkProvider}
 * @author MarcoSoldati
 *
 */
public class SpaceWeatherLinkProviderTest {

    /**
     * Test the provider.
     */
    @Test public void testLinkProvider() {
        SpaceWeatherLinkProvider provider = new SpaceWeatherLinkProvider();
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
        assertEquals(FileUtil.asURL("http://spaceweather.com/archive.php?view=1&day=15&month=09&year=2011"), link);
        
        String title = provider.getTitle(startTime, endTime);
        assertNotNull(title);
        assertEquals("Space Weather for 15-Sep-2011", title);
    }
}
