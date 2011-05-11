package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.longrunningquery.AsyncQueryService;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistryDao;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.clientapi.registry.impl.LocalHelioServiceRegistryDao;

/**
 * Factory to get async running query service instances.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncQueryServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(AsyncQueryServiceFactory.class);
	
	/**
	 * Hold the instance
	 */
	private static AsyncQueryServiceFactory instance = new AsyncQueryServiceFactory();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static AsyncQueryServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Map to cache the client stubs to the async running query service impls.
	 */
	private final Map<URL, AsyncQueryServiceImpl> serviceImplCache = new HashMap<URL, AsyncQueryServiceImpl>();
	
	/**
	 * the service registry bean.
	 */
	private HelioServiceRegistryDao serviceRegistry = LocalHelioServiceRegistryDao.getInstance();
	
	/**
	 * Get a new instance of the "best" service provider for a given descriptor
	 * @param serviceDescriptor the service descriptor to use
	 * @return a AsyncQueryService implementation to send out queries to this service.
	 */
	public AsyncQueryService getAsyncQueryService(String serviceName) {
	    HelioServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceName);
	    if (serviceDescriptor == null) {
	        throw new ServiceResolutionException("Unable to find service with name " +  serviceName);
	    }

	    URL bestWsdlLocation = serviceRegistry.getBestEndpoint(serviceDescriptor, HelioServiceCapability.ASYNC_QUERY_SERVICE);
	    if (bestWsdlLocation == null) {
	        throw new IllegalArgumentException("Unable to find any endpoint for service " + serviceName + " and capabilty " + HelioServiceCapability.ASYNC_QUERY_SERVICE);
	    }

	    _LOGGER.info("Using service at: " + bestWsdlLocation);
	    AsyncQueryServiceImpl queryService = serviceImplCache.get(bestWsdlLocation);
	    if (queryService == null) {
	        queryService = new AsyncQueryServiceImpl(bestWsdlLocation, serviceDescriptor.getName(), serviceDescriptor.getLabel());
	        serviceImplCache.put(bestWsdlLocation, queryService);
	    }
	    return queryService;
	}
}
