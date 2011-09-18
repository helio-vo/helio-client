package eu.heliovo.clientapi.query.asyncquery.impl;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get async running query service instances.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncQueryServiceFactory extends AbstractServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(AsyncQueryServiceFactory.class);
	
	/**
	 * Hold the instance
	 */
	private static AsyncQueryServiceFactory instance = new AsyncQueryServiceFactory();
	
	/**
	 * Map of potential implementations of a service. The entry with null will be used as default service impl.
	 */
	private static final Map<HelioServiceName, Class<? extends AsyncQueryService>> implClassMap = 
	    new HashMap<HelioServiceName, Class<? extends AsyncQueryService>>();
	
	static {
	    implClassMap.put(null, AsyncQueryServiceImpl.class);
	    implClassMap.put(HelioServiceName.DES, DesAsyncQueryServiceImpl.class);
	}
	
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static synchronized AsyncQueryServiceFactory getInstance() {
		return instance;
	}
	
	@Override
	public AsyncQueryService getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = getServiceRegistryClient().getAllEndpoints(serviceDescriptor, ServiceCapability.ASYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        
        Class<? extends AsyncQueryService> serviceImpl = implClassMap.get(serviceName);
        if (serviceImpl == null) {
            serviceImpl = implClassMap.get(null);
        }
        
        AsyncQueryService queryService;
        try {
            Constructor<? extends AsyncQueryService> constructor = serviceImpl.getConstructor(new Class<?>[] {HelioServiceName.class, String.class, AccessInterface[].class});
            queryService = constructor.newInstance(new Object[] {serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces});
        } catch (Exception e) {
            throw new RuntimeException("Unable to instanciate " + serviceImpl.getName() + ": " + e.getMessage(), e);
        }
        return queryService;
	}
	
	/**
	 * Get a new instance of the "best" service provider for a given descriptor
	 * @param serviceName the service descriptor to use
	 * @param accessInterfaces 
	 * @return a AsyncQueryService implementation to send out queries to this service.
	 * @deprecated This method will be removed. Rather use the generic {@link #getHelioService(HelioServiceName, String, AccessInterface...)}
	 */
	@Deprecated
	public AsyncQueryService getAsyncQueryService(HelioServiceName serviceName, AccessInterface ... accessInterfaces) {
	    return getHelioService(serviceName, null, accessInterfaces);
	}
}
