package eu.heliovo.clientapi.linkprovider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Test the {@link TheSunTodayLinkProvider}
 * @author MarcoSoldati
 *
 */
public class TheSunTodayLinkProviderTest {

    /**
     * Test the provider.
     */
    @Test public void testLinkProvider() {
        TheSunTodayLinkProvider provider = new TheSunTodayLinkProvider();
        assertNotNull(provider.getName());
        assertNotNull(provider.getDescription());
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2011, Calendar.SEPTEMBER, 15);
        Date startTime = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date endTime = cal.getTime();
        URL link = provider.getLink(startTime, endTime);
        assertNotNull(link);
        assertEquals(HelioFileUtil.asURL("http://sdowww.lmsal.com/suntoday/index.html?suntoday_date=2011-09-15"), link);
        
        String title = provider.getTitle(startTime, endTime);
        assertNotNull(title);
        assertEquals("The Sun Today for 15-Sep-2011", title);
    }
}
