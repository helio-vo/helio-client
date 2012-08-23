package eu.heliovo.clientapi.linkprovider.impl;

import java.util.Calendar;
import java.util.Date;

import eu.heliovo.clientapi.config.service.ServiceFactory;
import eu.heliovo.clientapi.linkprovider.LinkProviderService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Link provider for solarmonitor.org.
 * Users should get an instance of this call through the {@link ServiceFactory}.
 * @author MarcoSoldati
 *
 */
public class SolarMonitorLinkProvider extends AbstractDailyLinkProvider implements LinkProviderService {

    /**
     * Template for the link. The date format is 20110915, for September 15, 2011. 
     */
    private static final String PROVIDER_TEMPLATE = "http://solarmonitor.org/index.php?date=%1$tY%1$tm%1$td";

    /**
     * Template for the title.  
     */
    private static final String TITLE_TEMPLATE = "SolarMonitor for %1$td-%1$tb-%1$tY";
    
    /**
     * Identifier of the concrete service instance
     */
    public static final String SERVICE_VARIANT = "helio://helio-vo.eu/lps/solarmonitor";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from SolarMonitor.org";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily solar activity page from www.solarmonitor.org";
    
    /**
     * Start date of the list: 16-January-1996.
     */
    private static final Date START_DATE = asDate(1996, Calendar.JANUARY, 15); 
    
    /**
     * Create the provider
     */
    public SolarMonitorLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }

    /**
     * Create the provider
     */
    public SolarMonitorLinkProvider(HelioServiceName serviceName, String serviceVariant) {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
        AssertUtil.assertArgumentEquals(HelioServiceName.LPS, serviceName, "serviceName");
        AssertUtil.assertArgumentEquals(serviceVariant, SERVICE_VARIANT, "serviceVariant");
    }
    
    @Override
    protected boolean pageExists(Date startTime, Date endTime) {
        return startTime.after(START_DATE);
    }
}
