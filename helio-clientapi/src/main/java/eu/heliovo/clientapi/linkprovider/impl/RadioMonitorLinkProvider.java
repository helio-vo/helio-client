package eu.heliovo.clientapi.linkprovider.impl;

import java.util.Calendar;
import java.util.Date;

import eu.heliovo.clientapi.config.service.ServiceFactory;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Link provider for http://secchirh.obspm.fr/
 * Users should get an instance of this call through the {@link ServiceFactory}.
 * @author MarcoSoldati
 *
 */
public class RadioMonitorLinkProvider extends AbstractDailyLinkProvider {

    /**
     * Template for the link. Date format is: 20110912_2359.
     */
    private static final String PROVIDER_TEMPLATE = "http://secchirh.obspm.fr/survey.php?hour=day&dayofyear=%1$tY%1$tm%1$td&composite=1";
    
    /**
     * Template for the title.  
     */
    private static final String TITLE_TEMPLATE = "Radio monitor composites for %1$td-%1$tb-%1$tY";
    
    /**
     * Identifier of the concrete service instance
     */
    public static final String SERVICE_VARIANT = "helio://helio-vo.eu/lps/radio_monitor";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from http://secchirh.obspm.fr/";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily Radio plots from http://secchirh.obspm.fr/";

    /**
     * Start date of the list: 16-April-2010
     */
    private static final Date START_DATE = asDate(1997, Calendar.OCTOBER, 1); 
    
    /**
     * Create the provider
     */
    public RadioMonitorLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }
    
    /**
     * Create the provider
     */
    public RadioMonitorLinkProvider(HelioServiceName serviceName, String serviceVariant) {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
        AssertUtil.assertArgumentEquals(HelioServiceName.LPS, serviceName, "serviceName");
        AssertUtil.assertArgumentEquals(serviceVariant, SERVICE_VARIANT, "serviceVariant");
    }
    
    @Override
    protected boolean pageExists(Date startTime, Date endTime) {
        return startTime.after(START_DATE);
    }
}
