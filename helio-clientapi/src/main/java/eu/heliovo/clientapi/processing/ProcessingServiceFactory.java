package eu.heliovo.clientapi.processing;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.model.service.ServiceVariantRegistry;
import eu.heliovo.clientapi.processing.context.FlarePlotterService;
import eu.heliovo.clientapi.processing.context.GoesPlotterService;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService;
import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.GoesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.SimpleParkerModelServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
import eu.heliovo.clientapi.processing.hps.impl.CmePropagationModelImpl;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceDescriptor;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Factory to get accessor objects for all processing services.
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
	    // register context plots
	    serviceVariantRegistry.register(HelioServiceName.CXS, GoesPlotterServiceImpl.GOES_PLOTTER, GoesPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.CXS, FlarePlotterServiceImpl.FLARE_PLOTTER, FlarePlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.CXS, SimpleParkerModelServiceImpl.PARKER_MODEL, SimpleParkerModelServiceImpl.class);
	    
	    // register des plots
	    serviceVariantRegistry.register(HelioServiceName.DES, AcePlotterServiceImpl.SERVICE_VARIANT, AcePlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, StaPlotterServiceImpl.SERVICE_VARIANT, StaPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, StbPlotterServiceImpl.SERVICE_VARIANT, StbPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, UlyssesPlotterServiceImpl.SERVICE_VARIANT, UlyssesPlotterServiceImpl.class);
	    serviceVariantRegistry.register(HelioServiceName.DES, WindPlotterServiceImpl.SERVICE_VARIANT, WindPlotterServiceImpl.class);
	    
	    // register HPS plots
	    serviceVariantRegistry.register(HelioServiceName.HPS, CmePropagationModelImpl.SERVICE_VARIANT, CmePropagationModelImpl.class);

	    
	    serviceVariantRegistry.register(HelioServiceName.TAVERNA_SERVER, TavernaWorkflow2283.SERVICE_VARIANT, TavernaWorkflow2283.class);

    }

    @Override
    public ProcessingService<?> getHelioService(HelioServiceName serviceName, String serviceVariant, AccessInterface... accessInterfaces) {
        return getProcessingService(serviceName, serviceVariant, accessInterfaces);
    }

	
	/**
	 * Convenience method to get access to a goes plotter client stub.
	 * @param accessInterfaces the interfaces that implement the service. Will be retrieved from the registry if null.
	 * @return the client stub to access the plotter.
	 */
	public GoesPlotterService getGoesPlotterService(AccessInterface ... accessInterfaces) {
	    return (GoesPlotterService) getProcessingService(HelioServiceName.CXS, GoesPlotterServiceImpl.GOES_PLOTTER, accessInterfaces);
	}

	/**
	 * Convenience method to get access to a flare plotter client stub
	 * @param accessInterfaces the interfaces that implement the service. Will be retrieved from the registry if null.
	 * @return the client stub to access the plotter.
	 */
	public FlarePlotterService getFlarePlotterService(AccessInterface ... accessInterfaces) {
	    return (FlarePlotterService) getProcessingService(HelioServiceName.CXS, FlarePlotterServiceImpl.FLARE_PLOTTER, accessInterfaces);
	}

	/**
	 * Convenience method to get access to a parker spiral plotter client stub
	 * @param accessInterfaces the interfaces that implement the service. Will be retrieved from the registry if null.
	 * @return the client stub to access the plotter.
	 */
	public SimpleParkerModelService getSimpleParkerModelService(AccessInterface ... accessInterfaces) {
	    return (SimpleParkerModelService) getProcessingService(HelioServiceName.CXS, SimpleParkerModelServiceImpl.PARKER_MODEL, accessInterfaces);
	}
	
    /**
     * Get the context service by name and variant
     * @param serviceName name of the overall service.
     * @param serviceVariant id of the concrete context service. Use one of the constants defined by this class.
     * @param accessInterfaces the interface to use. Will be retrieved from the registry if null.
     * @return a proxy to the context service.
     */
    private ProcessingService<?> getProcessingService(HelioServiceName serviceName, String serviceVariant, AccessInterface ... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentHasText(serviceVariant, "serviceVariant");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        
        // load services from registry
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            List<AccessInterface> tmpAccessInterfaces = new ArrayList<AccessInterface>();
            for (ServiceCapability capability : serviceDescriptor.getCapabilities()) {
                Collections.addAll(tmpAccessInterfaces, 
                        getServiceRegistryClient().getAllEndpoints(serviceDescriptor, capability, AccessInterfaceType.SOAP_SERVICE)
                );
            }
            accessInterfaces = tmpAccessInterfaces.toArray(new AccessInterface[tmpAccessInterfaces.size()]);
        }
        _LOGGER.info("Using services at: " + Arrays.toString(accessInterfaces));
        
        Class<? extends HelioService> serviceClass = serviceVariantRegistry.getServiceImpl(serviceName, serviceVariant);
        if (serviceClass == null) {
            throw new ServiceResolutionException("Unable to find context service of type " + serviceVariant);
        }
        
        ProcessingService<?> service;
        try {
            Constructor<? extends HelioService> constructor = serviceClass.getConstructor(AccessInterface[].class);
            service = (ProcessingService<?>)constructor.newInstance(new Object[] {accessInterfaces});
        } catch (Exception e) {
            throw new ServiceResolutionException("Unable to instanciate service " + serviceClass + ": " + e.getMessage(), e);
        }
        
        return service;
    }
}
