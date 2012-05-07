package eu.heliovo.clientapi.query.asyncquery.impl;

import eu.heliovo.clientapi.model.service.ServiceFactoryConfiguration;
import eu.heliovo.clientapi.model.service.ServiceVariantRegistry;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Factory to get async running query service instances.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncQueryServiceFactoryConfig implements ServiceFactoryConfiguration {

    /**
	 * Create the factory and register the variants. 
	 */
	public AsyncQueryServiceFactoryConfig() {
    }

	/**
	 * Register specific variants of the async query services.
	 */
	public void registerVariants(ServiceVariantRegistry serviceVariantRegistry) {
	    // default impl
	    serviceVariantRegistry.register(null, null, ServiceCapability.ASYNC_QUERY_SERVICE, "asyncQueryService");
	    serviceVariantRegistry.register(HelioServiceName.DES, null, ServiceCapability.ASYNC_QUERY_SERVICE, "desAsyncQueryService");
	    serviceVariantRegistry.register(HelioServiceName.DPAS, null, ServiceCapability.ASYNC_QUERY_SERVICE, "dpasAsyncQueryService");
	    serviceVariantRegistry.register(HelioServiceName.DES, DesPlotterService.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "asyncQueryService");
	    serviceVariantRegistry.register(HelioServiceName.ICS, IcsPatAsyncQueryServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "icsPatAsyncQueryService");
	}
}
