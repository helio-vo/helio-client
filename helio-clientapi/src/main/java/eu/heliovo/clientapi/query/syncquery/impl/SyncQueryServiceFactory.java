package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.query.AbstractQueryServiceFactory;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get instances of the SyncQueryService.
 * @author MarcoSoldati
 *
 */
public class SyncQueryServiceFactory extends AbstractQueryServiceFactory {
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
     * Get a new instance of the "best" service provider for a given descriptor
     * @param serviceDescriptor the service descriptor to use
     * @return a AsyncQueryService implementation to send out queries to this service.
     */
    public SyncQueryService getSyncQueryService(String serviceName, AccessInterface ... accessInterfaces) {
        AssertUtil.assertArgumentHasText(serviceName, "serviceName");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = serviceRegistry.getAllEndpoints(serviceDescriptor, ServiceCapability.SYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        SyncQueryServiceImpl queryService = new SyncQueryServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);
        return queryService;
    }
}
