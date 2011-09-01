package eu.heliovo.clientapi.processing.context;

import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.GoesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.SimpleParkerModelServiceImpl;
import eu.heliovo.clientapi.query.AbstractQueryServiceFactory;
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
public class ContextServiceFactory extends AbstractQueryServiceFactory {
	/**
	 * The logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(ContextServiceFactory.class);
	
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
	
	/**
	 * Get a new instance of the "best" service provider for a desired service.
	 * @param serviceName the service name to use.
	 * @return a Context implementation to send out queries to this service.
	 */
	public ContextService getContextService(HelioServiceName serviceName, String serviceType, AccessInterface ... accessInterfaces) {
	    AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
	    AssertUtil.assertArgumentHasText(serviceType, "serviceType");
	    ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceName);
	    if (accessInterfaces == null || accessInterfaces.length == 0 || accessInterfaces[0] == null) {
	        accessInterfaces = serviceRegistry.getAllEndpoints(serviceDescriptor, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, AccessInterfaceType.SOAP_SERVICE);
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
	
	/**
	 * Convenience method to get access to a goes plotter client stub.
	 * @param accessInterfaces the interfaces that implement the flare plotter.
	 * @return the client stub to access the plotter.
	 */
	public GoesPlotterService getGoesPlotterService(AccessInterface ... accessInterfaces) {
	    return (GoesPlotterService) getContextService(HelioServiceName.CXS, "ivo://helio-vo.eu/cxs/goesplotter", accessInterfaces);
	}

	/**
	 * Convenience method to get access to a flare plotter client stub
	 * @param accessInterfaces the interfaces that implement the service.
	 * @return the client stub to access the plotter.
	 */
	public FlarePlotterService getFlarePlotterService(AccessInterface ... accessInterfaces) {
	    return (FlarePlotterService) getContextService(HelioServiceName.CXS, "ivo://helio-vo.eu/cxs/flareplotter", accessInterfaces);
	}

	/**
	 * Convenience method to get access to a flare plotter client stub
	 * @param accessInterfaces the interfaces that implement the service.
	 * @return the client stub to access the plotter.
	 */
	public SimpleParkerModelService getSimpleParkerModelServicesImpl(AccessInterface ... accessInterfaces) {
	    return (SimpleParkerModelService) getContextService(HelioServiceName.CXS, "ivo://helio-vo.eu/cxs/parkermodel", accessInterfaces);
	}
}
