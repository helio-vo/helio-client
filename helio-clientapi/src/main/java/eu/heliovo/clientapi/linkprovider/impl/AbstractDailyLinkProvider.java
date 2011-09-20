package eu.heliovo.clientapi.linkprovider.impl;

import java.net.URL;
import java.util.Date;

import javax.activation.MimeType;

import eu.heliovo.clientapi.linkprovider.LinkProviderService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Abstract base class for provision of daily links.
 * @author MarcoSoldati
 *
 */
abstract class AbstractDailyLinkProvider implements LinkProviderService {
    /**
     * Template to create the URL
     */
    private final String providerTemplate;
    /**
     * The simple title
     */
    private final String title;
    
    /**
     * Template for the context aware title.
     */
    private final String titleTemplate;
    
    /**
     * Name of the service variant. Should start with ivo:// and must be unique.
     */
    private final String serviceVariant;
    
    /**
     * Description
     */
    private final String description;

    /**
     * Create the daily provider.
     * @param providerTemplate the template to create the URL, will be passed the start and end time.
     * @param title the simple title
     * @param titleTemplate the context aware title, will be passed the start and end time.
     * @param serviceVariant the unique name of the link provider.
     * @param description a general purpose description.
     */
    public AbstractDailyLinkProvider(String providerTemplate, String title, String titleTemplate, String serviceVariant, String description) {
        this.providerTemplate = providerTemplate;
        this.title = title;
        this.titleTemplate = titleTemplate;
        this.serviceVariant = serviceVariant;
        this.description = description;
    }
    
    @Override
    public URL getLink(Date startTime, Date endTime) {
        AssertUtil.assertArgumentNotNull(startTime, "startTime");
        return HelioFileUtil.asURL(String.format(providerTemplate, startTime));
    }
    
    @Override
    public String getTitle() {
        return getTitle(null, null);
    }
    
    @Override
    public String getTitle(Date startTime, Date endTime) {
        if (startTime == null) {
            return title;
        }
        return String.format(titleTemplate, startTime);
    }
    
    @Override
    public String getServiceVariant() {
        return serviceVariant;
    }

    @Override
    public HelioServiceName getServiceName() {
        return HelioServiceName.LPS;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public MimeType getMimeType() {
        return MimeTypeConstants.TEXT_HTML;
    }


}