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
public class CdawStereoLinkProviderTest {

    /**
     * Test the provider.
     */
    @Test public void testLinkProvider() {
        CdawStereoLinkProvider provider = new CdawStereoLinkProvider();
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
        assertEquals(FileUtil.asURL("http://cdaw.gsfc.nasa.gov/movie/make_javamovie.php?img1=stb_e171&img2=sta_e171&stime=20110915_0109&etime=20110918_0109"), link);

        String title = provider.getTitle(startTime, endTime);
        assertNotNull(title);
        assertEquals("Stereo EUVI 171 movies for 15-Sep-2011 - 18-Sep-2011", title);
    }
}
