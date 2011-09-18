package eu.heliovo.clientapi.linkprovider.impl;

import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;
import eu.heliovo.clientapi.linkprovider.LinkProviderService;

/**
 * Link provider for solarmonitor.org.
 * Users should get an instance of this call through the {@link LinkProviderFactory}.
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
    public static final String SERVICE_VARIANT = "ivo://helio-vo.eu/lps/solarmonitor";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from SolarMonitor.org";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily solar activity page from www.solarmonitor.org";
    
    /**
     * Create the provider
     */
    public SolarMonitorLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }
    
}
