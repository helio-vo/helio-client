package eu.heliovo.clientapi.linkprovider.impl;

import java.util.Calendar;
import java.util.Date;

import eu.heliovo.clientapi.linkprovider.LinkProviderFactoryConfig;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Link provider for spaceweather.com.
 * Users should get an instance of this call through the {@link LinkProviderFactoryConfig}.
 * @author MarcoSoldati
 *
 */
public class SpaceWeatherLinkProvider extends AbstractDailyLinkProvider {

    /**
     * Template for the link.  
     */
    private static final String PROVIDER_TEMPLATE = "http://spaceweather.com/archive.php?view=1&day=%1$td&month=%1$tm&year=%1$tY";

    /**
     * Template for the title.  
     */
    private static final String TITLE_TEMPLATE = "Space Weather for %1$td-%1$tb-%1$tY";
    
    /**
     * Identifier of the concrete service instance
     */
    public static final String SERVICE_VARIANT = "helio://helio-vo.eu/lps/spaceweather";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from spaceweather.com";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily space weather from www.spaceweather.com";

    /**
     * Start date of the list: 1-November-2000
     */
    private static final Date START_DATE = asDate(2000, Calendar.OCTOBER, 31); 

    /**
     * Create the provider
     */
    public SpaceWeatherLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }
    
    /**
     * Create the provider
     */
    public SpaceWeatherLinkProvider(HelioServiceName serviceName, String serviceVariant) {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
        AssertUtil.assertArgumentEquals(HelioServiceName.LPS, serviceName, "serviceName");
        AssertUtil.assertArgumentEquals(serviceVariant, SERVICE_VARIANT, "serviceVariant");
    }
    
    @Override
    protected boolean pageExists(Date startTime, Date endTime) {
        return startTime.after(START_DATE);
    }


}
