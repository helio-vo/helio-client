package eu.heliovo.clientapi.linkprovider.impl;

import java.net.URL;
import java.util.Date;

import javax.activation.MimeType;

import eu.heliovo.clientapi.linkprovider.LinkProviderService;
import eu.heliovo.clientapi.linkprovider.LinkProviderFactory;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Link provider for solarmonitor.org.
 * Users should get an instance of this call through the {@link LinkProviderFactory}.
 * @author MarcoSoldati
 *
 */
public class SolarMonitorLinkProvider implements LinkProviderService {

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
    private static final String SUB_SERVICE_NAME = "ivo://helio-vo.eu/lps/solarmonitor";

    /**
     * Name of the link provider.
     */
    private static final String NAME = "Daily page from SolarMonitor.org";
    
    /**
     * Description of the service.
     */
    private static final String DESC = "Access to the daily solar activity page from www.solarmonitor.org";

    /**
     * The mime type.
     */
    private static final MimeType MIME_TYPE = MimeTypeConstants.TEXT_HTML;
    
    @Override
    public URL getLink(Date startTime, Date endTime) {
        AssertUtil.assertArgumentNotNull(startTime, "startTime");
        return HelioFileUtil.asURL(String.format(PROVIDER_TEMPLATE, startTime));
    }
    
    @Override
    public String getTitle() {
        return getTitle(null, null);
    }
    
    @Override
    public String getTitle(Date startTime, Date endTime) {
        if (startTime == null) {
            return NAME;
        }
        return String.format(TITLE_TEMPLATE, startTime);
    }
    
    @Override
    public String getSubServiceName() {
        return SUB_SERVICE_NAME;
    }

    @Override
    public HelioServiceName getName() {
        return HelioServiceName.LPS;
    }

    @Override
    public String getDescription() {
        return DESC;
    }
    
    @Override
    public MimeType getMimeType() {
        return MIME_TYPE;
    }
}
