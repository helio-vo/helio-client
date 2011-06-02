package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

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
	public static synchronized SyncQueryServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Map to cache the client stubs to the sync query service implementations.
	 */
	private final Map<AccessInterface, SyncQueryServiceImpl> serviceImplCache = new HashMap<AccessInterface, SyncQueryServiceImpl>();
	
	/**
	 * Get a new instance of the "best" service provider for a given descriptor
	 * @param serviceDescriptor the service descriptor to use
	 * @return a {@link SyncQueryService} implementation to send out queries to this service.
	 */
	public HelioQueryService getSyncQueryService(String serviceName) {
	    ServiceRegistryClient serviceRegistry = ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
	    
	    ServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceName);
	    if (serviceDescriptor == null) {
	        throw new ServiceResolutionException("Unable to find service with name " +  serviceName);
	    }
	    
	    AccessInterface accessInterface = serviceRegistry.getBestEndpoint(serviceDescriptor, ServiceCapability.SYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
	    if (accessInterface == null) {
			throw new IllegalArgumentException("Unable to find any endpoint for service " + serviceName + " and capabilty " + ServiceCapability.SYNC_QUERY_SERVICE);
		}
		
		_LOGGER.info("Using service at: " + accessInterface);
		SyncQueryServiceImpl queryService = serviceImplCache.get(accessInterface);
		if (queryService == null) {
			queryService = new SyncQueryServiceImpl(accessInterface, serviceDescriptor.getName(), serviceDescriptor.getLabel());
			serviceImplCache.put(accessInterface, queryService);
		}
		return queryService;
	}
}
