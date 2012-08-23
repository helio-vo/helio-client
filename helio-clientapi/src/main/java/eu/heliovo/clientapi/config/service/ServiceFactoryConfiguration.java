package eu.heliovo.clientapi.config.service;

/**
 * Configuration bean for the service factory.
 * @author MarcoSoldati
 *
 */
public interface ServiceFactoryConfiguration {
    /**
     * Register services in the given service variant registry. 
     * @param serviceVariantRegistry the service variant registry.
     */
    public void registerVariants(ServiceVariantRegistry serviceVariantRegistry);
}
