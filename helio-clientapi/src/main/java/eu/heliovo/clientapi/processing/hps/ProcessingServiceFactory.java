package eu.heliovo.clientapi.processing.hps;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.model.service.ServiceVariantRegistry;
import eu.heliovo.clientapi.processing.hps.impl.CmePropagationModelImpl;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get accessor objects for the processing services.
 * @author marco soldati at fhnw ch
 *
 */
public class ProcessingServiceFactory extends AbstractServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(ProcessingServiceFactory.class);

	/**
	 * Hold the instance
	 */
	private static ProcessingServiceFactory instance = new ProcessingServiceFactory();
	
	/**
	 * Registry for the service variant.s
	 */
	private final ServiceVariantRegistry serviceVariantRegistry = new ServiceVariantRegistry();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static synchronized ProcessingServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Initialize the service variant registry.
	 */
	public ProcessingServiceFactory() {
	    serviceVariantRegistry.register(HelioServiceName.HPS, CmePropagationModelImpl.SERVICE_VARIANT, CmePropagationModelImpl.class);
    }

    @Override
    public HelioProcessingService<?> getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces) {
        return getProcessingService(serviceName, serviceVariant, accessInterfaces);
    }

    /**
     * Get the processing service by name and variant
     * @param serviceName name of the overall service.
     * @param serviceVariant id of the concrete context service. Use one of the constants defined by this class.
     * @param accessInterfaces the interface to use. Will be retrieved from the registry if null.
     * @return a proxy to the context service.
     */
    private HelioProcessingService<?> getProcessingService(HelioServiceName serviceName, String serviceVariant, AccessInterface ... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentHasText(serviceVariant, "serviceVariant");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = getServiceRegistryClient().getAllEndpoints(serviceDescriptor, ServiceCapability.HELIO_PROCESSING_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        
        Class<? extends HelioService> contextServiceClass = serviceVariantRegistry.getServiceImpl(serviceName, serviceVariant);
        if (contextServiceClass == null) {
            throw new ServiceResolutionException("Unable to find context service of type " + serviceVariant);
        }
        
        HelioProcessingService<?> processingService;
        try {
            Constructor<? extends HelioService> constructor = contextServiceClass.getConstructor(AccessInterface[].class);
            processingService = (HelioProcessingService<?>)constructor.newInstance(new Object[] {accessInterfaces});
        } catch (Exception e) {
            throw new ServiceResolutionException("Unable to instanciate service " + contextServiceClass + ": " + e.getMessage(), e);
        }
        
        return processingService;
    }
}
