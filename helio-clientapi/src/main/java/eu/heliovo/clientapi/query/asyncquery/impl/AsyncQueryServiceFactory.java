package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

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
	public static synchronized AsyncQueryServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Get a new instance of the "best" service provider for a given descriptor
	 * @param serviceDescriptor the service descriptor to use
	 * @return a AsyncQueryService implementation to send out queries to this service.
	 */
	public AsyncQueryService getAsyncQueryService(String serviceName) {
	    ServiceRegistryClient serviceRegistry = ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();

	    ServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceName);
	    if (serviceDescriptor == null) {
	        throw new ServiceResolutionException("Unable to find service with name " +  serviceName);
	    }

	    AccessInterface[] accessInterfaces = serviceRegistry.getAllEndpoints(serviceDescriptor, ServiceCapability.ASYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
	    if (accessInterfaces == null) {
	        throw new IllegalArgumentException("Unable to find any endpoint for service " + serviceName + " and capabilty " + ServiceCapability.ASYNC_QUERY_SERVICE);
	    }

	    _LOGGER.info("Using service at: " + accessInterfaces);
	    AsyncQueryServiceImpl queryService = new AsyncQueryServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);
	    return queryService;
	}
}
