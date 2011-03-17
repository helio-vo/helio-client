package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.longrunningquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.impl.StaticHelioRegistryImpl;

/**
 * Factory to get long running query service instances.
 * @author marco soldati at fhnw ch
 *
 */
public class LongRunningQueryServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(LongRunningQueryServiceFactory.class);
	
	/**
	 * Hold the instance
	 */
	private static LongRunningQueryServiceFactory instance = new LongRunningQueryServiceFactory();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static LongRunningQueryServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Map to cache the client stubs to the long running query service impls.
	 */
	private final Map<URL, LongRunningQueryServiceImpl> serviceImplCache = new HashMap<URL, LongRunningQueryServiceImpl>();
	
	/**
	 * the service registry bean.
	 */
	private HelioServiceRegistry serviceRegistry = StaticHelioRegistryImpl.getInstance();
	
	/**
	 * Get a new instance of the "best" service provider for a given descriptor
	 * @param serviceDescriptor the service descriptor to use
	 * @return a LongRunningQueryService implementation to send out queries to this service.
	 */
	public AsyncQueryService getLongRunningQueryService(HelioServiceDescriptor serviceDescriptor) {
		if (serviceDescriptor.getType() != HelioServiceType.LONGRUNNING_QUERY_SERVICE) {
			throw new IllegalArgumentException("serviceDescriptor should be of type " + HelioServiceType.LONGRUNNING_QUERY_SERVICE + ", but is " + serviceDescriptor.getType() + ": " + serviceDescriptor);
		}
		URL bestWsdlLocation = serviceRegistry.getBestEndpoint(serviceDescriptor);
		_LOGGER.info("Using service at: " + bestWsdlLocation);
		LongRunningQueryServiceImpl queryService = serviceImplCache.get(bestWsdlLocation);
		if (queryService == null) {
			queryService = new LongRunningQueryServiceImpl(bestWsdlLocation, serviceDescriptor.getName(), serviceDescriptor.getLabel());
			serviceImplCache.put(bestWsdlLocation, queryService);
		}
		return queryService;
	}
}
