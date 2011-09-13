package eu.heliovo.clientapi.processing.context;

import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.GoesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.SimpleParkerModelServiceImpl;
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
	 * ID of the goes plotter
	 */
    public static final String GOES_PLOTTER = "ivo://helio-vo.eu/cxs/goesplotter";

    /**
     * ID of the flare plotter
     */
    public static final String FLARE_PLOTTER = "ivo://helio-vo.eu/cxs/flareplotter";

    /**
     * ID of the parker model
     */
    public static final String PARKER_MODEL = "ivo://helio-vo.eu/cxs/parkermodel";
	
	/**
	 * Hold the instance
	 */
	private static ContextServiceFactory instance = new ContextServiceFactory();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static synchronized ContextServiceFactory getInstance() {
		return instance;
	}

    @Override
    public HelioService getHelioService(HelioServiceName serviceName, String subType, AccessInterface... accessInterfaces) {
        return getContextService(serviceName, subType, accessInterfaces);
    }

	
	/**
	 * Convenience method to get access to a goes plotter client stub.
	 * @param accessInterfaces the interfaces that implement the flare plotter.
	 * @return the client stub to access the plotter.
	 */
	public GoesPlotterService getGoesPlotterService(AccessInterface ... accessInterfaces) {
	    return (GoesPlotterService) getContextService(HelioServiceName.CXS, GOES_PLOTTER, accessInterfaces);
	}

	/**
	 * Convenience method to get access to a flare plotter client stub
	 * @param accessInterfaces the interfaces that implement the service.
	 * @return the client stub to access the plotter.
	 */
	public FlarePlotterService getFlarePlotterService(AccessInterface ... accessInterfaces) {
	    return (FlarePlotterService) getContextService(HelioServiceName.CXS, FLARE_PLOTTER, accessInterfaces);
	}

	/**
	 * Convenience method to get access to a flare plotter client stub
	 * @param accessInterfaces the interfaces that implement the service.
	 * @return the client stub to access the plotter.
	 */
	public SimpleParkerModelService getSimpleParkerModelServicesImpl(AccessInterface ... accessInterfaces) {
	    return (SimpleParkerModelService) getContextService(HelioServiceName.CXS, PARKER_MODEL, accessInterfaces);
	}
	
    /**
     * Get the context service by name
     * @param serviceName
     * @param serviceType
     * @param accessInterfaces
     * @return
     */
    private ContextService getContextService(HelioServiceName serviceName, String serviceType, AccessInterface ... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentHasText(serviceType, "serviceType");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = getServiceRegistryClient().getAllEndpoints(serviceDescriptor, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        ContextService contextService;
        if ("ivo://helio-vo.eu/cxs/goesplotter".equals(serviceType)) {
            contextService = new GoesPlotterServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);
        } else if ("ivo://helio-vo.eu/cxs/flareplotter".equals(serviceType)) {
            contextService = new FlarePlotterServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);          
        } else if ("ivo://helio-vo.eu/cxs/parkermodel".equals(serviceType)) {
            contextService = new SimpleParkerModelServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);                     
        } else {
            throw new ServiceResolutionException("Unable to find context service of type " + serviceType);
        }
        
        return contextService;
    }

}
