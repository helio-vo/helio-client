package eu.heliovo.clientapi.linkprovider;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.heliovo.clientapi.linkprovider.impl.SolarMonitorLinkProvider;
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
     * Map that holdes the link providers
     */
    private final Map<String, LinkProviderService> linkProviderMap = new LinkedHashMap<String, LinkProviderService>();

    
    /**
     * Hide the default constructor.
     */
    LinkProviderFactory() {
        register(new SolarMonitorLinkProvider());
    }
    
    /**
     * Register a link provider.
     * @param linkProvider a link provider.
     */
    private void register(SolarMonitorLinkProvider linkProvider) {
        linkProviderMap.put(linkProvider.getSubServiceName(), linkProvider);
    }

    @Override
    public LinkProviderService getHelioService(HelioServiceName serviceName, String subServiceName, AccessInterface... accessInterfaces) {
        AssertUtil.assertArgumentEquals(HelioServiceName.LPS, serviceName, "serviceName");
        return getLinkProvider(subServiceName);
    }

    /**
     * Get the link provider with a given name.
     * @param name the name of the provider.
     * @return the link provider with a given name or null if not found.
     */
    public LinkProviderService getLinkProvider(String name) {
        return linkProviderMap.get(name);
    };
    
    /**
     * Get the list of known linkprovider names.
     * @return the names of the link providers or empty array if none have been found.
     */
    public String[] getLinkProviderNames() {
        return linkProviderMap.keySet().toArray(new String[linkProviderMap.size()]);
    }
    
    /**
     * Get the list of known link providers.
     * @return list of known link providers or empty array if none have been found.
     */
    public LinkProviderService[] getLinkProviders() {
        return linkProviderMap.values().toArray(new LinkProviderService[linkProviderMap.size()]);
    }
    
}
