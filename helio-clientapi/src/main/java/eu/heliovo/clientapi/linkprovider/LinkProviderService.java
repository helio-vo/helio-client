package eu.heliovo.clientapi.linkprovider;

import java.net.URL;
import java.util.Date;

import javax.activation.MimeType;

import eu.heliovo.clientapi.model.service.HelioService;

/**
 * A generic interface for a given provider
 * @author MarcoSoldati
 *
 */
public interface LinkProviderService extends HelioService {
    
    /**
     * Get the link for a given date range.
     * @param startTime the start value of the range.
     * @param endTime the end value of the range.
     * @return URL pointing to the remote page. 
     */
    public URL getLink(Date startTime, Date endTime);
    
    /**
     * Get a generic title for this provider.
     * This will return the same as calling {@link #getTitle(Date, Date) getTitle(null, null)}. 
     * @return the title of the link provider. Will not be null.
     */
    public String getTitle();

    /**
     * Get a context specific title of this provider to be shown to the end user. The title should not take too much space such that 
     * it can be used in a web form inside a dropdown box or as a link.  
     * @param startTime the start time of the date range. May be ignored.
     * @param endTime the end time of the date range. May be ignored.
     * @return the title for the provider. Will not be null.
     */
    public String getTitle(Date startTime, Date endTime);
    
    /**
     * Get the internal identifier of a concrete implementation of a LinkProviderService.
     * @return the internal name of the service.
     */
    public String getSubServiceName();
    
    /**
     * Return a general purpose description for help texts.
     * @return the description.
     */
    public String getDescription();
    
    /**
     * Get the Mime type (Multipurpose Internet Mail Extension (MIME), as defined in RFC 2045 and 2046) 
     * of the returned URL or null if unknown.
     * @return the mime type or null if not known.
     */
    public MimeType getMimeType();
}
