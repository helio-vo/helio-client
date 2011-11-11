package eu.heliovo.clientapi.linkprovider.impl;

import java.util.Calendar;
import java.util.Date;

import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;

/**
 * Link provider for http://sdowww.lmsal.com/suntoday/
 * Users should get an instance of this call through the {@link LinkProviderFactory}.
 * @author MarcoSoldati
 *
 */
public class CdawStereoLinkProvider extends AbstractDailyLinkProvider {

    /**
     * Template for the link. Date format is: 20110912_2359.
     */
    private static final String PROVIDER_TEMPLATE = "http://cdaw.gsfc.nasa.gov/movie/make_javamovie.php?img1=stb_e171&img2=sta_e171&stime=%1$tY%1$tm%1$td_&1$tH%1$tm&etime=%2$tY%2$tm%2$td_&2$tH%2$tm";
    /**
     * Template for the title.  
     */
    private static final String TITLE_TEMPLATE = "Stereo EUVI 171 movies for %1$td-%1$tb-%1$tY - %2$td-%2$tb-%2$tY";
    
    /**
     * Identifier of the concrete service instance
     */
    private static final String SERVICE_VARIANT = "ivo://helio-vo.eu/lps/cdaw_stereo_e171";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from http://sdowww.lmsal.com/suntoday/";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily SDO images from http://sdowww.lmsal.com/suntoday/";

    /**
     * Start date of the list: 16-April-2010
     */
    private static final Date START_DATE = asDate(2010, Calendar.APRIL, 15); 
    
    /**
     * Create the provider
     */
    public CdawStereoLinkProvider() {
        super(PROVIDER_TEMPLATE, NAME, TITLE_TEMPLATE, SERVICE_VARIANT, DESC);
    }
    
    @Override
    protected boolean pageExists(Date startTime, Date endTime) {
        return startTime.after(START_DATE);
    }


}
