package eu.heliovo.clientapi.processing.context;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.model.service.ServiceVariantRegistry;
import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.GoesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.SimpleParkerModelServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get accessor objects for the context services.
 * @author marco soldati at fhnw ch
 *
 */
public class ContextServiceFactory extends AbstractServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(ContextServiceFactory.class);

	/**
	 * Hold the instance
	 */
	private static ContextServiceFactory instance = new ContextServiceFactory();
	
	/**
	 * Registry for the service variant.s
	 */
	private final ServiceVariantRegistry serviceVariantRegistry = new ServiceVariantRegistry();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static synchronized ContextServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * Initialize the service variant registry.
	 */
	public ContextServiceFactory() {
	    serviceVariantRegistry.register(HelioServiceName.CXS, GoesPlotterServiceImpl.GOES_PLOTTER, GoesPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.CXS, FlarePlotterServiceImpl.FLARE_PLOTTER, FlarePlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.CXS, SimpleParkerModelServiceImpl.PARKER_MODEL, SimpleParkerModelServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, AcePlotterServiceImpl.SERVICE_VARIANT, AcePlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, StaPlotterServiceImpl.SERVICE_VARIANT, StaPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, StbPlotterServiceImpl.SERVICE_VARIANT, StbPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, UlyssesPlotterServiceImpl.SERVICE_VARIANT, UlyssesPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, WindPlotterServiceImpl.SERVICE_VARIANT, WindPlotterServiceImpl.class);
    }

    @Override
    public ContextService getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces) {
        return getContextService(serviceName, serviceVariant, accessInterfaces);
    }

	
	/**
	 * Convenience method to get access to a goes plotter client stub.
	 * @param accessInterfaces the interfaces that implement the service. Will be retrieved from the registry if null.
	 * @return the client stub to access the plotter.
	 */
	public GoesPlotterService getGoesPlotterService(AccessInterface ... accessInterfaces) {
	    return (GoesPlotterService) getContextService(HelioServiceName.CXS, GoesPlotterServiceImpl.GOES_PLOTTER, accessInterfaces);
	}

	/**
	 * Convenience method to get access to a flare plotter client stub
	 * @param accessInterfaces the interfaces that implement the service. Will be retrieved from the registry if null.
	 * @return the client stub to access the plotter.
	 */
	public FlarePlotterService getFlarePlotterService(AccessInterface ... accessInterfaces) {
	    return (FlarePlotterService) getContextService(HelioServiceName.CXS, FlarePlotterServiceImpl.FLARE_PLOTTER, accessInterfaces);
	}

	/**
	 * Convenience method to get access to a parker spiral plotter client stub
	 * @param accessInterfaces the interfaces that implement the service. Will be retrieved from the registry if null.
	 * @return the client stub to access the plotter.
	 */
	public SimpleParkerModelService getSimpleParkerModelService(AccessInterface ... accessInterfaces) {
	    return (SimpleParkerModelService) getContextService(HelioServiceName.CXS, SimpleParkerModelServiceImpl.PARKER_MODEL, accessInterfaces);
	}
	
    /**
     * Get the context service by name and variant
     * @param serviceName name of the overall service.
     * @param serviceVariant id of the concrete context service. Use one of the constants defined by this class.
     * @param accessInterfaces the interface to use. Will be retrieved from the registry if null.
     * @return a proxy to the context service.
     */
    private ContextService getContextService(HelioServiceName serviceName, String serviceVariant, AccessInterface ... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentHasText(serviceVariant, "serviceVariant");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = getServiceRegistryClient().getAllEndpoints(serviceDescriptor, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        
        Class<? extends HelioService> contextServiceClass = serviceVariantRegistry.getServiceImpl(serviceName, serviceVariant);
        if (contextServiceClass == null) {
            throw new ServiceResolutionException("Unable to find context service of type " + serviceVariant);
        }
        
        ContextService contextService;
        try {
            Constructor<? extends HelioService> constructor = contextServiceClass.getConstructor(AccessInterface[].class);
            contextService = (ContextService)constructor.newInstance(new Object[] {accessInterfaces});
        } catch (Exception e) {
            throw new ServiceResolutionException("Unable to instanciate service " + contextServiceClass + ": " + e.getMessage(), e);
        }
        
        return contextService;
    }

}
