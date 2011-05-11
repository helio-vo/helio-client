package eu.heliovo.clientapi.query.syncquery.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistryDao;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.clientapi.registry.impl.LocalHelioServiceRegistryDao;

/**
 * Factory to get instances of the SyncQueryService.
 * @author MarcoSoldati
 *
 */
public class SyncQueryServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(SyncQueryServiceFactory.class);
	
	/**
	 * Hold the instance
	 */
	private static SyncQueryServiceFactory instance = new SyncQueryServiceFactory();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static SyncQueryServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Map to cache the client stubs to the sync query service implementations.
	 */
	private final Map<URL, SyncQueryServiceImpl> serviceImplCache = new HashMap<URL, SyncQueryServiceImpl>();
	
	/**
	 * the service registry bean.
	 */
	private HelioServiceRegistryDao serviceRegistry = LocalHelioServiceRegistryDao.getInstance();
	
	/**
	 * Get a new instance of the "best" service provider for a given descriptor
	 * @param serviceDescriptor the service descriptor to use
	 * @return a {@link SyncQueryService} implementation to send out queries to this service.
	 */
	public HelioQueryService getSyncQueryService(String serviceName) {
	    
	    HelioServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceName);
	    if (serviceDescriptor == null) {
	        throw new ServiceResolutionException("Unable to find service with name " +  serviceName);
	    }
	    
	    URL bestWsdlLocation = serviceRegistry.getBestEndpoint(serviceDescriptor, HelioServiceCapability.SYNC_QUERY_SERVICE);
	    if (bestWsdlLocation == null) {
			throw new IllegalArgumentException("Unable to find any endpoint for service " + serviceName + " and capabilty " + HelioServiceCapability.SYNC_QUERY_SERVICE);
		}
		
		_LOGGER.info("Using service at: " + bestWsdlLocation);
		SyncQueryServiceImpl queryService = serviceImplCache.get(bestWsdlLocation);
		if (queryService == null) {
			queryService = new SyncQueryServiceImpl(bestWsdlLocation, serviceDescriptor.getName(), serviceDescriptor.getLabel());
			serviceImplCache.put(bestWsdlLocation, queryService);
		}
		return queryService;
	}
}
