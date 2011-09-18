package eu.heliovo.clientapi.linkprovider;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.heliovo.clientapi.linkprovider.impl.SolarMonitorLinkProvider;
import eu.heliovo.clientapi.linkprovider.impl.SpaceWeatherLinkProvider;
import eu.heliovo.clientapi.linkprovider.impl.TheSunTodayLinkProvider;
import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get access to the link providers.
 * @author MarcoSoldati
 *
 */
public class LinkProviderFactory extends AbstractServiceFactory {
    
    /**
     * Hold the singleton instance of this factory.
     */
    private static final LinkProviderFactory instance = new LinkProviderFactory();

    /**
     * Get the singleton instance of this factory.
     * @return the singleton.
     */
    public static LinkProviderFactory getInstance() {
        return instance;
    }
    
    /**
     * Map that holds the link providers (value) and the serviceVariant name (key). 
     */
    private final Map<String, LinkProviderService> linkProviderMap = new LinkedHashMap<String, LinkProviderService>();

    
    /**
     * Hide the default constructor.
     */
    LinkProviderFactory() {
        register(new SolarMonitorLinkProvider());
        register(new SpaceWeatherLinkProvider());
        register(new TheSunTodayLinkProvider());
    }
    
    /**
     * Register a link provider.
     * @param linkProvider a link provider.
     */
    private void register(LinkProviderService linkProvider) {
        linkProviderMap.put(linkProvider.getServiceVariant(), linkProvider);
    }

    /**
     * @param serviceName the name of the service. Must match {@link HelioServiceName#LPS}.
     * @param serviceVariant the service variant defaults to {@link SolarMonitorLinkProvider}.
     */
    @Override
    public LinkProviderService getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces) {
        AssertUtil.assertArgumentEquals(HelioServiceName.LPS, serviceName, "serviceName");
        return getLinkProvider(serviceVariant == null ? SolarMonitorLinkProvider.SERVICE_VARIANT : serviceVariant);
    }

    /**
     * Get the link provider with a given name.
     * @param serviceVariant the name of the provider.
     * @return the link provider with a given name or null if not found.
     */
    public LinkProviderService getLinkProvider(String serviceVariant) {
        return linkProviderMap.get(serviceVariant);
    };
    
    /**
     * Get the list of known LinkProviderService variant names.
     * @param serviceName the name of the service. Must match {@link HelioServiceName#LPS}.
     * @return the names of the link providers or empty array if none have been found.
     */
    public String[] getServiceVariants(HelioServiceName serviceName) {
        AssertUtil.assertArgumentEquals(HelioServiceName.LPS, serviceName, "serviceName");
        return linkProviderMap.keySet().toArray(new String[linkProviderMap.size()]);
    }
    
    /**
     * Get the list of all known link providers.
     * @return list of known link providers or empty array if none have been found.
     */
    public LinkProviderService[] getLinkProviders() {
        return linkProviderMap.values().toArray(new LinkProviderService[linkProviderMap.size()]);
    }
}
