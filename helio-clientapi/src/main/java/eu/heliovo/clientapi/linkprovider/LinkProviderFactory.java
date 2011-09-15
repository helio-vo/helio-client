package eu.heliovo.clientapi.linkprovider;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.heliovo.clientapi.linkprovider.impl.SolarMonitorLinkProvider;

/**
 * Factory to get access to the link providers.
 * @author MarcoSoldati
 *
 */
public class LinkProviderFactory {
    
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
    private final Map<String, LinkProvider> linkProviderMap = new LinkedHashMap<String, LinkProvider>();

    
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
        linkProviderMap.put(linkProvider.getName(), linkProvider);
    }

    /**
     * Get the link provider with a given name.
     * @param name the name of the provider.
     * @return the link provider with a given name or null if not found.
     */
    public LinkProvider getLinkProvider(String name) {
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
    public LinkProvider[] getLinkProviders() {
        return linkProviderMap.values().toArray(new LinkProvider[linkProviderMap.size()]);
    }
    
}
