package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get instances of the SyncQueryService.
 * @author MarcoSoldati
 *
 */
public class SyncQueryServiceFactory extends AbstractServiceFactory {
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
	
	@Override
	public SyncQueryService getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = getServiceRegistryClient().getAllEndpoints(serviceDescriptor, ServiceCapability.SYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }
        if (accessInterfaces == null || accessInterfaces.length == 0) {
            throw new ServiceResolutionException("Unable to find any endpoint for service " + serviceName + ". Please check the registry settings.");
        }

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        SyncQueryServiceImpl queryService = new SyncQueryServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);
        return queryService;
	}

    /**
     * Get a new instance of the "best" service provider for a given descriptor
     * @param serviceName the name of the service.
     * @param accessInterfaces the interfaces where to find the endpoints. If null the registry will be asked.
     * @return a AsyncQueryService implementation to send out queries to this service.
     * @deprecated This method will be removed. Rather use the generic {@link #getHelioService(HelioServiceName, String, AccessInterface...)}
     */
	@Deprecated
    public SyncQueryService getSyncQueryService(HelioServiceName serviceName, AccessInterface ... accessInterfaces) {
	    return getHelioService(serviceName, null, accessInterfaces);
    }
}
