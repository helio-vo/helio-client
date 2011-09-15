package eu.heliovo.clientapi.linkprovider.impl;

import java.net.URL;
import java.util.Date;

import eu.heliovo.clientapi.linkprovider.LinkProvider;
import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Link provider for solarmonitor.org.
 * Users should get an instance of this call through the {@link LinkProviderFactory}.
 * @author MarcoSoldati
 *
 */
public class SolarMonitorLinkProvider implements LinkProvider {

    /**
     * Template for the link. The date format is 20110915, for September 15, 2011. 
     */
    private static final String PROVIDER_TEMPLATE = "http://solarmonitor.org/index.php?date=%1$tY%1$tm%1$td";

    /**
     * Template for the title.  
     */
    private static final String TITLE_TEMPLATE = "SolarMonitor for %1$td-%1$tb-%1$tY";
    
    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from SolarMonitor.org";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily solar activity page from www.solarmonitor.org";
    
    @Override
    public URL getLink(Date startTime, Date endTime) {
        AssertUtil.assertArgumentNotNull(startTime, "startTime");
        return HelioFileUtil.asURL(String.format(PROVIDER_TEMPLATE, startTime));
    }

    @Override
    public String getTitle(Date startTime, Date endTime) {
        if (startTime == null) {
            return NAME;
        }
        return String.format(TITLE_TEMPLATE, startTime);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESC;
    }
}
