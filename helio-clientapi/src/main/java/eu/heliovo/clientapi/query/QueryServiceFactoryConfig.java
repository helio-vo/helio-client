package eu.heliovo.clientapi.query;

import eu.heliovo.clientapi.config.service.ServiceFactoryConfiguration;
import eu.heliovo.clientapi.config.service.ServiceVariantRegistry;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.query.impl.IcsPatQueryServiceImpl;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Factory to get async running query service instances.
 * @author marco soldati at fhnw ch
 *
 */
public class QueryServiceFactoryConfig implements ServiceFactoryConfiguration {

    /**
	 * Create the factory and register the variants. 
	 */
	public QueryServiceFactoryConfig() {
    }

	/**
	 * Register specific variants of the async query services.
	 */
	public void registerVariants(ServiceVariantRegistry serviceVariantRegistry) {
	    // default impl
	    serviceVariantRegistry.register(null, null, ServiceCapability.SYNC_QUERY_SERVICE, "baseQueryService");
	    serviceVariantRegistry.register(null, null, ServiceCapability.ASYNC_QUERY_SERVICE, "baseQueryService");
	    serviceVariantRegistry.register(HelioServiceName.HEC, null, ServiceCapability.LOCAL_QUERY_SERVICE, "localHecQueryService");
	    serviceVariantRegistry.register(HelioServiceName.ILS, null, ServiceCapability.LOCAL_QUERY_SERVICE, "localIlsQueryService");
	    serviceVariantRegistry.register(HelioServiceName.ICS, null, ServiceCapability.LOCAL_QUERY_SERVICE, "localIcsQueryService");
	    serviceVariantRegistry.register(HelioServiceName.IES, null, ServiceCapability.EXPERIMENTAL_QUERY_SERVICE, "iesQueryService");
	    
	    serviceVariantRegistry.register(HelioServiceName.DES, null, ServiceCapability.ASYNC_QUERY_SERVICE, "desQueryService");
	    serviceVariantRegistry.register(HelioServiceName.DPAS, null, ServiceCapability.ASYNC_QUERY_SERVICE, "dpasQueryService");
	    serviceVariantRegistry.register(HelioServiceName.DES, DesPlotterService.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "baseQueryService");
	    serviceVariantRegistry.register(HelioServiceName.ICS, IcsPatQueryServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "icsPatQueryService");
	}
}
