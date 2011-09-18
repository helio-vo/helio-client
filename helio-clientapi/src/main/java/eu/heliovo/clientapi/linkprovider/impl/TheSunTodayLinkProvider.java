package eu.heliovo.clientapi.linkprovider.impl;

import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;

/**
 * Link provider for http://sdowww.lmsal.com/suntoday/
 * Users should get an instance of this call through the {@link LinkProviderFactory}.
 * @author MarcoSoldati
 *
 */
public class TheSunTodayLinkProvider extends AbstractDailyLinkProvider {

    /**
     * Template for the link. Date format is: 2011-09-12.
     */
    private static final String PROVIDER_TEMPLATE = "http://sdowww.lmsal.com/suntoday/index.html?suntoday_date=%1$tY-%1$tm-%1$td";

    /**
     * Template for the title.  
     */
    private static final String TITLE_TEMPLATE = "The Sun Today for %1$td-%1$tb-%1$tY";
    
    /**
     * Identifier of the concrete service instance
     */
    private static final String SERVICE_VARIANT = "ivo://helio-vo.eu/lps/thesuntoday";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from http://sdowww.lmsal.com/suntoday/";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily SDO images from http://sdowww.lmsal.com/suntoday/";

    /**
     * Create the provider
     */
    public TheSunTodayLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }

}
