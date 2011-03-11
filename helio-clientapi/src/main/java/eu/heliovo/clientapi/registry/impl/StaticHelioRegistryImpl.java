package eu.heliovo.clientapi.registry.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceInstanceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.ServiceResolutionException;

public class StaticHelioRegistryImpl implements HelioServiceRegistry {
	/**
	 * The logger for this registry impl
	 */
	private static final Logger _LOGGER = Logger.getLogger(StaticHelioRegistryImpl.class);
	
	@Override
	public URL getHfc() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getHec() throws ServiceResolutionException {
		return asURL("http://140.105.77.30:8080/helio-hec/HelioLongQueryService?wsdl");
	}
	
	@Override
	public URL getUoc() throws ServiceResolutionException {
		return asURL("http://140.105.77.30:8080/helio-uoc/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getDpas() throws ServiceResolutionException {
		return asURL("http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getIcs() throws ServiceResolutionException {
		return asURL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getIls() throws ServiceResolutionException {
		return asURL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getCea() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getMdes() throws ServiceResolutionException {
		return asURL("http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService_MDES.wsdl");		
	}

	private static StaticHelioRegistryImpl instance = null;
	
	public synchronized static StaticHelioRegistryImpl getInstance() {
		if (instance == null) {
			instance = new StaticHelioRegistryImpl();
			instance.init();
		}
		return instance;
	}
	
	/**
	 * Hide the default constructor
	 */
	private StaticHelioRegistryImpl() {
	}

	/**
	 * Init the registry with current values.
	 */
	private void init() {
		HelioServiceDescriptor serviceDescriptor = new HelioServiceDescriptor("HEC", HelioServiceType.LONGRUNNING_QUERY_SERVICE, "HEC - HELIO Query Interface", null);
		registerServiceDescriptor(serviceDescriptor);
	}
	
	/**
	 * Convert String to URL. 
	 * @param url the url to convert
	 * @return the url as URL object
	 * @throws ServiceResolutionException if the URL is not valid
	 */
	private URL asURL(String url) throws ServiceResolutionException {
		try {
			return new URL("http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
		} catch (MalformedURLException e) {
			throw new ServiceResolutionException("Unable to parse URL: " + e.getMessage(), e);
		}
	}

	/**
	 * Store the registered service descriptors.
	 */
	private final Set<HelioServiceDescriptor> serviceDescriptors = new HashSet<HelioServiceDescriptor>();
	
	/**
	 * Store the registered service instance descriptor
	 */
	private final Map<HelioServiceDescriptor, Set<HelioServiceInstanceDescriptor>> instanceDescriptors = new HashMap<HelioServiceDescriptor, Set<HelioServiceInstanceDescriptor>>();
	
	/**
	 * Register a new service descriptor.
	 * @param helioServiceDescriptor the service descriptor
	 * @return true if the descriptor has not been registered before. false if a previous instance of the service already existed.
	 */
	public boolean registerServiceDescriptor(HelioServiceDescriptor helioServiceDescriptor) {
		boolean ret = serviceDescriptors.add(helioServiceDescriptor);
		if (!ret) {
			_LOGGER.warn("Service descriptor '" +  helioServiceDescriptor + "' has been previously registered.");
		}
		return ret;
	}
	
	/**
	 * Register a descriptor for an instance of a service.
	 * @param instanceDescriptor the instance descriptor
	 * @return true if the descriptor has not been registered before, false if a instance of this descriptor already exists.
	 */
	public boolean registerServiceInstanceDescriptor(HelioServiceInstanceDescriptor instanceDescriptor) {
		// check if service descritor is already registered.
		if (!serviceDescriptors.contains(instanceDescriptor.getServiceDescriptor())) {
			throw new ServiceResolutionException("Please register the service descriptor "
					+ instanceDescriptor.getServiceDescriptor() 
					+ " before registering the service instance descriptor");
		}
		
		Set<HelioServiceInstanceDescriptor> currentInstanceDescriptors = instanceDescriptors.get(instanceDescriptor.getServiceDescriptor());
		
		// create hash set for descriptor if not yet existing.
		if (currentInstanceDescriptors == null) {
			currentInstanceDescriptors = new LinkedHashSet<HelioServiceInstanceDescriptor>();
			instanceDescriptors.put(instanceDescriptor.getServiceDescriptor(), currentInstanceDescriptors);
		}
		
		boolean ret = currentInstanceDescriptors.add(instanceDescriptor);
		if (!ret) {
			_LOGGER.warn("Service instance descriptor '" +  instanceDescriptor + "' has been previously registered.");
		}
		return ret;
	}
	
	
	@Override
	public HelioServiceDescriptor[] getServiceDescriptors() {
		return serviceDescriptors.toArray(new HelioServiceDescriptor[serviceDescriptors.size()]);
	}

	@Override
	public HelioServiceDescriptor getServiceDescriptor(String name,	HelioServiceType type) {
		HelioServiceDescriptor ret = null;
		for (HelioServiceDescriptor currentDescriptor : serviceDescriptors) {
			if (currentDescriptor.getName().equals(name) && currentDescriptor.getType().equals(type)) {
				ret = currentDescriptor;
				break;
			}
		}
		return ret;
	}

	@Override
	public URL[] getAllEndpoints(HelioServiceDescriptor descriptor)
			throws ServiceResolutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getBestEndpoint(HelioServiceDescriptor descriptor)
			throws ServiceResolutionException {
		// TODO Auto-generated method stub
		return null;
	}
}
