package eu.heliovo.clientapi.linkprovider;

import java.net.URL;
import java.util.Date;

/**
 * A generic interface for a given provider
 * @author MarcoSoldati
 *
 */
public interface LinkProvider {
    
    /**
     * Get the link for a given date range.
     * @param startTime the start value of the range.
     * @param endTime the end value of the range.
     * @return URL pointing to the remote page. 
     */
    public URL getLink(Date startTime, Date endTime);
    
    /**
     * Get a context specific title of this provider to be shown to the end user. The title should not take too much space such that 
     * it can be used in a web form inside a dropdown box or as a link.  
     * @param startTime the start time of the date range. May be ignored.
     * @param endTime the end time of the date range. May be ignored.
     * @return the title for the provider.
     */
    public String getTitle(Date startTime, Date endTime);
    
    /**
     * Get the generic name of this provider.
     * This name can be used to query for a known link provider in the {@link LinkProviderFactory}.
     * @return the name of the link provider. Must not be null.
     */
    public String getName();
    
    /**
     * Return a general purpose description for help texts.
     * @return the description.
     */
    public String getDescription();
}
