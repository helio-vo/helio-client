package eu.heliovo.clientapi.processing.context;

import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractServiceFactory;
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
    public ContextService getHelioService(HelioServiceName serviceName, String subType, AccessInterface... accessInterfaces) {
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
     * @param serviceName name of the overall service.
     * @param subServiceName internal id of the concrete service. Use one of the constants defined by this class.
     * @param accessInterfaces the interface to use.
     * @return a proxy to the context service.
     */
    private ContextService getContextService(HelioServiceName serviceName, String subServiceName, AccessInterface ... accessInterfaces) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentHasText(subServiceName, "subServiceName");
        ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
        if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
            accessInterfaces = getServiceRegistryClient().getAllEndpoints(serviceDescriptor, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        }
        AssertUtil.assertArgumentNotEmpty(accessInterfaces, "accessInterfaces");

        _LOGGER.info("Found services at: " + Arrays.toString(accessInterfaces));
        ContextService contextService;
        if (GOES_PLOTTER.equals(subServiceName)) {
            contextService = new GoesPlotterServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);
        } else if (FLARE_PLOTTER.equals(subServiceName)) {
            contextService = new FlarePlotterServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);          
        } else if (PARKER_MODEL.equals(subServiceName)) {
            contextService = new SimpleParkerModelServiceImpl(serviceDescriptor.getName(), serviceDescriptor.getLabel(), accessInterfaces);                     
        } else {
            throw new ServiceResolutionException("Unable to find context service of type " + subServiceName);
        }
        return contextService;
    }

}
