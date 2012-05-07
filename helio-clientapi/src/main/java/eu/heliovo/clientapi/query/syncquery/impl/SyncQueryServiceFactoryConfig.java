package eu.heliovo.clientapi.query.syncquery.impl;

import eu.heliovo.clientapi.model.service.ServiceFactoryConfiguration;
import eu.heliovo.clientapi.model.service.ServiceVariantRegistry;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Factory configuration for SyncQueryService.
 * @author MarcoSoldati
 *
 */
public class SyncQueryServiceFactoryConfig implements ServiceFactoryConfiguration {

    @Override
    public void registerVariants(
            ServiceVariantRegistry serviceVariantRegistry) {
        serviceVariantRegistry.register(null, null, ServiceCapability.SYNC_QUERY_SERVICE, "syncQueryService");
    }
}
