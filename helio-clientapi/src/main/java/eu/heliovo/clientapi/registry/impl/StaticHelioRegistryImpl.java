package eu.heliovo.clientapi.registry.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceInstanceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.LongRunningServiceDescriptor;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;

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
		return getBestEndpoint("hec", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
	}
	
	@Override
	public URL getUoc() throws ServiceResolutionException {
		return getBestEndpoint("uoc", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
	}

	@Override
	public URL getDpas() throws ServiceResolutionException {
		return getBestEndpoint("dpas", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
	}

	@Override
	public URL getIcs() throws ServiceResolutionException {
		return getBestEndpoint("ics", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
	}

	@Override
	public URL getIls() throws ServiceResolutionException {
		return getBestEndpoint("ils", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
	}

	@Override
	public URL getCea() throws ServiceResolutionException {
		return getBestEndpoint("cea", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
	}

	@Override
	public URL getMdes() throws ServiceResolutionException {
		return getBestEndpoint("mdes", HelioServiceType.LONGRUNNING_QUERY_SERVICE);
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
		register(LongRunningServiceDescriptor.ASYNC_HEC, "http://140.105.77.30:8080/helio-hec/HelioLongQueryService?wsdl");
		register(LongRunningServiceDescriptor.ASYNC_UOC, "http://140.105.77.30:8080/helio-uoc/HelioLongQueryService?wsdl");
		register(LongRunningServiceDescriptor.ASYNC_DPAS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
		register(LongRunningServiceDescriptor.ASYNC_ICS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioLongQueryService?wsdl");
		register(LongRunningServiceDescriptor.ASYNC_ILS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioLongQueryService?wsdl");
		register(LongRunningServiceDescriptor.ASYNC_MDES, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService.wsdl");		
	}

	/**
	 * Register a service
	 * @param serviceName name of the service
	 * @param type type of the service
	 * @param label the label for the service
	 * @param description the description of the service.
	 * @param wsdlFiles the wsdlFiles pointing to valid endpoints for this service.
	 */
	@SuppressWarnings("unused")
	private void register(String serviceName, 
			HelioServiceType type, String label,
			String description, String... wsdlFiles) {
		// create the descriptor
		HelioServiceDescriptor serviceDescriptor = 
			new GenericHelioServiceDescriptor(serviceName, type, label, description);
		register(serviceDescriptor, wsdlFiles);
	}

	/**
	 * Register a service.
	 * @param serviceDescriptor the descriptor of the service
	 * @param wsdlFiles URLs pointing to the WSDL endpoints.
	 */
	private void register(HelioServiceDescriptor serviceDescriptor, String... wsdlFiles) {
		boolean success = registerServiceDescriptor(serviceDescriptor);
		// sanity check
		if (success == false) {
			throw new ServiceResolutionException("service descriptor " + serviceDescriptor + " has been previously registered.");
		}

		// create the instance descriptor.
		for (String wsdlFile : wsdlFiles) {
			HelioServiceInstanceDescriptor instanceDescriptor = new HelioServiceInstanceDescriptor(serviceDescriptor, asURL(wsdlFile));
			success = registerServiceInstanceDescriptor(instanceDescriptor);
			// sanity check
			if (success == false) {
				throw new ServiceResolutionException("service instance descriptor " + instanceDescriptor + " has been previously registered.");
			}
		}
	}
	
	/**
	 * Convert String to URL. 
	 * @param url the url to convert
	 * @return the url as URL object
	 * @throws ServiceResolutionException if the URL is not valid
	 */
	private URL asURL(String url) throws ServiceResolutionException {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new ServiceResolutionException("Unable to parse URL '" + url + "'. Cause: " + e.getMessage(), e);
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
	public HelioServiceDescriptor[] getAllServiceDescriptors() {
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
	public URL[] getAllEndpoints(HelioServiceDescriptor descriptor) {
		AssertUtil.assertArgumentNotNull(descriptor, "descriptor");
		Set<HelioServiceInstanceDescriptor> serviceInstances = instanceDescriptors.get(descriptor);
		if (serviceInstances == null) {
			return new URL[0];
		}

		// fill in array of URLs
		URL[] ret = new URL[serviceInstances.size()];
		int i = 0;
		for (HelioServiceInstanceDescriptor helioServiceInstanceDescriptor : serviceInstances) {
			ret[i] = helioServiceInstanceDescriptor.getWsdlFile();
			i++;
		}
		
		return ret;
	}

	/**
	 * This implementation just takes the first match.
	 */
	@Override
	public URL getBestEndpoint(HelioServiceDescriptor descriptor) {
		AssertUtil.assertArgumentNotNull(descriptor, "descriptor");
		Set<HelioServiceInstanceDescriptor> serviceInstances = instanceDescriptors.get(descriptor);
		if (serviceInstances == null || serviceInstances.isEmpty()) {
			return null;
		}
		
		// just take the first entry
		for (HelioServiceInstanceDescriptor helioServiceInstanceDescriptor : serviceInstances) {
			return helioServiceInstanceDescriptor.getWsdlFile();
		}
		
		// will never be reached
		return null; 
	}
	

	public URL getBestEndpoint(String name,
			HelioServiceType type) {
		HelioServiceDescriptor serviceDescsriptor = getServiceDescriptor(name, type);
		if (serviceDescsriptor == null) {
			throw new ServiceResolutionException("No service found with name " + name + " and type " + type);
		}
		URL bestEndpoint = getBestEndpoint(serviceDescsriptor);
		if (bestEndpoint == null) {
			throw new ServiceResolutionException("No service instance found for " + serviceDescsriptor);
		}
		return bestEndpoint;
	}


}
