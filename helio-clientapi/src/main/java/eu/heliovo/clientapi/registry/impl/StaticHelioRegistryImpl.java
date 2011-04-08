package eu.heliovo.clientapi.registry.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import eu.heliovo.clientapi.registry.GenericHelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Implementation of a static serach registry
 * @author MarcoSoldati
 *
 */
public class StaticHelioRegistryImpl implements HelioServiceRegistry {
	/**
	 * The logger for this registry impl
	 */
	private static final Logger _LOGGER = Logger.getLogger(StaticHelioRegistryImpl.class);

	/**
	 * Name of the serialized registry.
	 */
    private static final String REGISTRY_FILE = "static_registry.properties";
	
    /**
     * The instance of this registry
     */
	private static StaticHelioRegistryImpl instance = null;
	
	/**
	 * Get the singleton instance of this registry.
	 * @return the singleton instance
	 */
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
	    // check if config exists
	    File registryDir = HelioFileUtil.getHelioHomeDir("registry");
	    File registryFile = new File(registryDir, REGISTRY_FILE);
	    
	    // read config from stored file
	    if (registryFile.exists())  {
	        _LOGGER.info("Reading from service registry at " + registryFile.getAbsolutePath());
	        FileInputStream is = null;
            try {
                is = FileUtils.openInputStream(registryFile);
           
                Properties props = new Properties();
                props.load(is);
                
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    HelioServiceDescriptor desc;
                    String key = (String)entry.getKey();
                    String value = (String)entry.getValue();
                    if (key.startsWith("LongRunningServiceDescriptor")) {
                        desc = LongRunningServiceDescriptor.valueOf(key.substring("LongRunningServiceDescriptor.".length()));
                    } else if (key.startsWith("SyncServiceDescriptor"))  {
                        desc = SyncServiceDescriptor.valueOf(key.substring("SyncServiceDescriptor.".length()));
                    } else {
                        throw new RuntimeException("Unknown service descriptor found: " + key);
                    }
                    String[] wsdlFiles = value.split(",");
                    register(desc, wsdlFiles);
                }
                
                if (instanceDescriptors.isEmpty()) {
                    _LOGGER.info("No data has been retrieved from " + registryFile.getAbsolutePath() + ". Trying to recreate the file.");
                    File backup = new File(registryDir, REGISTRY_FILE + ".bak0");
                    int i = 0;
                    while (backup.exists() && i < 10) {
                        backup = new File(backup.getAbsolutePath().replace(i-1 + "", i + ""));
                        i++;
                    }
                    FileUtils.copyFile(registryFile, backup);
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot load the registryFile " + registryFile + ": " + e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(is);
            }
	    }
	    
	    if (instanceDescriptors.isEmpty()) {
	        _LOGGER.info("Storing service registry to " + registryFile.getAbsolutePath());
	        
	        // populate registry
	        register(LongRunningServiceDescriptor.ASYNC_HEC, "http://festung1.oats.inaf.it:8080/helio-hec/HelioLongQueryService?wsdl");
            register(SyncServiceDescriptor.SYNC_HEC, "http://festung1.oats.inaf.it:8080/helio-hec/HelioService?wsdl");
            
            register(LongRunningServiceDescriptor.ASYNC_UOC, "http://festung1.oats.inaf.it:8080/helio-uoc/HelioLongQueryService?wsdl");
            register(SyncServiceDescriptor.SYNC_UOC, "http://festung1.oats.inaf.it:8080/helio-uoc/HelioService?wsdl");
            
            register(LongRunningServiceDescriptor.ASYNC_DPAS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
            register(SyncServiceDescriptor.SYNC_DPAS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioService?wsdl");
            
            register(LongRunningServiceDescriptor.ASYNC_ICS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioLongQueryService?wsdl");
            register(SyncServiceDescriptor.SYNC_ICS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioService?wsdl");
            
            register(LongRunningServiceDescriptor.ASYNC_ILS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioLongQueryService?wsdl");
            register(SyncServiceDescriptor.SYNC_ILS, "http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioService?wsdl");
            
            register(LongRunningServiceDescriptor.ASYNC_MDES, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService.wsdl");      
            register(SyncServiceDescriptor.SYNC_MDES, "http://manunja.cesr.fr/Amda-Helio/WebServices/HelioService.wsdl");       

            // and serialize to registry file
            FileOutputStream os = null;
            Properties props = new Properties();
            for (Entry<HelioServiceDescriptor, Set<HelioServiceInstanceDescriptor>> entry : instanceDescriptors.entrySet()) {
                StringBuilder urls = new StringBuilder();
                Set<HelioServiceInstanceDescriptor> instanceDescs = entry.getValue();
                boolean first = true;
                for (HelioServiceInstanceDescriptor desc : instanceDescs) {
                    if (first) {
                        first = false;
                    } else {
                        urls.append(",");
                    }
                    urls.append(desc.getWsdlFile());
                }
                props.put(entry.getKey().getClass().getSimpleName() + "." + entry.getKey().toString(), urls.toString());
            }
            try {
                os = FileUtils.openOutputStream(registryFile);
                props.store(os, "Static registry created by " + StaticHelioRegistryImpl.class.getName());
            } catch (IOException e) {
                throw new RuntimeException("Unable to write registry file " + registryFile);
            } finally {
                IOUtils.closeQuietly(os);
            }
	    }
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
			success = registerServiceInstance(serviceDescriptor, HelioFileUtil.asURL(wsdlFile));
			// sanity check
			if (success == false) {
				throw new ServiceResolutionException("Unable to register services. Check log for more information.");
			}
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
	public boolean registerServiceInstance(HelioServiceDescriptor serviceDescriptor, URL ... wsdlFiles) {
		// check if service descriptor is already registered, if not do so.
		if (!serviceDescriptors.contains(serviceDescriptor)) {
			registerServiceDescriptor(serviceDescriptor);
		}
		
		Set<HelioServiceInstanceDescriptor> currentInstanceDescriptors = instanceDescriptors.get(serviceDescriptor);
		
		// create hash set for descriptor if not yet existing.
		if (currentInstanceDescriptors == null) {
			currentInstanceDescriptors = new LinkedHashSet<HelioServiceInstanceDescriptor>();
			instanceDescriptors.put(serviceDescriptor, currentInstanceDescriptors);
		}
		
		boolean ret = true;
		for (URL wsdlFile : wsdlFiles) {
			HelioServiceInstanceDescriptor instanceDescriptor = new HelioServiceInstanceDescriptor(serviceDescriptor, wsdlFile);
			
			boolean ret2 = currentInstanceDescriptors.add(instanceDescriptor);
			if (!ret) {
				_LOGGER.warn("Service instance descriptor '" +  instanceDescriptor + "' has been previously registered.");
			}
			ret &= ret2;
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
	

	/**
	 * Descriptor of a concrete instance of a service.
	 * Do not instanciate this class outside of {@link StaticHelioRegistryImpl}. It's public for the XMLEncoder
	 * @author MarcoSoldati
	 *
	 */
	private static class HelioServiceInstanceDescriptor {
		/**
		 * The assigned service descriptor
		 */
		private final HelioServiceDescriptor serviceDescriptor;
		
		/**
		 * Pointer to the WSDL file
		 */
		private final URL wsdlFile;
		
		
		public HelioServiceInstanceDescriptor(HelioServiceDescriptor serviceDescriptor, URL wsdlFile) {
			AssertUtil.assertArgumentNotNull(serviceDescriptor, "serviceDescriptor");
			AssertUtil.assertArgumentNotNull(wsdlFile, "wsdlFile");
			this.serviceDescriptor = serviceDescriptor;
			this.wsdlFile = wsdlFile;
		}
		
		/**
		 * Get the descriptor of the service.
		 * @return the service descriptor.
		 */
		@SuppressWarnings("unused")
		public HelioServiceDescriptor getServiceDescriptor() {
			return serviceDescriptor;
		}
		
		/**
		 * Get a pointer to the WSDL file
		 * @return the wsdl file
		 */
		public URL getWsdlFile() {
			return wsdlFile;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof HelioServiceInstanceDescriptor)) {
				return false;
			}
			
			return serviceDescriptor.equals(((HelioServiceInstanceDescriptor)other).serviceDescriptor) &&
				wsdlFile.equals(((HelioServiceInstanceDescriptor)other).wsdlFile);
			
		}
		
		@Override
		public int hashCode() {
			return wsdlFile.hashCode() *13 + serviceDescriptor.hashCode() *11;
		}
	}
}
