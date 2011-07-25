package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.AbstractQueryServiceFactory;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get async running query service instances.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncQueryServiceFactory extends AbstractQueryServiceFactory {
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
	public AsyncQueryService getAsyncQueryService(String serviceName, AccessInterface ... accessInterfaces) {
	    AssertUtil.assertArgumentHasText(serviceName, "serviceName");
	    ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
	    if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
	        accessInterfaces = serviceRegistry.getAllEndpoints(serviceDescriptor, ServiceCapability.ASYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
	    }
	    AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");

	    _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
	    AsyncQueryServiceImpl queryService = new AsyncQueryServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);
	    return queryService;
	}
}
