package eu.heliovo.clientapi.linkprovider.impl;

import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;

/**
 * Link provider for spaceweather.com.
 * Users should get an instance of this call through the {@link LinkProviderFactory}.
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
    private static final String SERVICE_VARIANT = "ivo://helio-vo.eu/lps/spaceweather";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from spaceweather.com";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily space weather from www.spaceweather.com";

    /**
     * Create the provider
     */
    public SpaceWeatherLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }

}
