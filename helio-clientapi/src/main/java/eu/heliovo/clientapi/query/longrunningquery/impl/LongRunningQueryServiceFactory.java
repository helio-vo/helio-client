package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.net.URL;

import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.clientapi.registry.impl.StaticHelioRegistryImpl;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Factory to get long running query service instances.
 * Every call will return a new instance of the requested service.
 * @author marco soldati at fhnw ch
 *
 */
public class LongRunningQueryServiceFactory {
	/**
	 * Hold the instance
	 */
	private static LongRunningQueryServiceFactory instance = new LongRunningQueryServiceFactory();
	
	/**
	 * Get the singleton of this factory
	 * @return the singleton instance.
	 */
	public static LongRunningQueryServiceFactory getInstance() {
		return instance;
	}
	
	/**
	 * the service registry bean.
	 */
	private HelioServiceRegistry serviceRegistry = new StaticHelioRegistryImpl();
	
	/**
	 * Get a new instance of the "best" ILS service provider.
	 * @return client for the best ILS service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public IlsLongRunningQueryService getIlsService() throws ServiceResolutionException, JobExecutionException {
		URL wsdlLocation = serviceRegistry.getIls();
		return new IlsLongRunningQueryServiceImpl(wsdlLocation);
	}
	
	/**
	 * Get a new instance of the "best" ICS service provider.
	 * @return client for the best ICS service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public IcsLongRunningQueryService getIcsService() throws ServiceResolutionException, JobExecutionException {
		URL wsdlLocation = serviceRegistry.getIcs();
		return new IcsLongRunningQueryServiceImpl(wsdlLocation);
	}
	
	/**
	 * Get a new instance of the "best" HEC service provider.
	 * @return client for the best HEC service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public HecLongRunningQueryService getHecService() throws ServiceResolutionException, JobExecutionException {
		URL wsdlLocation = serviceRegistry.getHec();
		return new HecLongRunningQueryServiceImpl(wsdlLocation);
	}
	
	/**
	 * Get a new instance of the "best" HFC service provider.
	 * @return client for the best HFC service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public HfcLongRunningQueryService getHfcService() throws ServiceResolutionException, JobExecutionException {
		URL wsdlLocation = serviceRegistry.getHfc();
		return new HfcLongRunningQueryServiceImpl(wsdlLocation);
	}
	
	/**
	 * Get a new instance of the "best" DPAS service provider.
	 * @return client for the best DPAS service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public DpasLongRunningQueryService getDpasService() throws ServiceResolutionException, JobExecutionException {
		URL wsdlLocation = serviceRegistry.getDpas();
		return new DpasLongRunningQueryServiceImpl(wsdlLocation);
	}

	/**
	 * Get a new instance of the "best" MDES service provider.
	 * @return client for the best MDES service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public MdesLongRunningQueryService getMdesService() throws ServiceResolutionException, JobExecutionException {
		URL wsdlLocation = serviceRegistry.getMdes();
		return new MdesLongRunningQueryServiceImpl(wsdlLocation);
	}

	/**
	 * Get a new instance of the "best" UOC service provider.
	 * @return client for the best UOC service provider.
	 * @throws ServiceResolutionException if no service end point can be found.
	 * @throws JobExecutionException if the service client cannot be created.
	 */
	public UocLongRunningQueryService getUocService() {
		URL wsdlLocation = serviceRegistry.getUoc();
		return new UocLongRunningQueryServiceImpl(wsdlLocation);
	}
}
