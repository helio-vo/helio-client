package eu.heliovo.clientapi.registry.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistryDao;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

public class AbstractHelioServiceRegistryDao implements HelioServiceRegistryDao {

    /**
     * The logger for this registry impl
     */
    protected final Logger _LOGGER = Logger.getLogger(getClass());
    
    /**
     * Store the registered service descriptors.
     */
    protected final Set<HelioServiceDescriptor> serviceDescriptors = new HashSet<HelioServiceDescriptor>();

    /**
     * Store the registered service instance descriptor
     */
    protected final Map<HelioServiceDescriptor, Set<HelioServiceInstanceDescriptor>> instanceDescriptors = new HashMap<HelioServiceDescriptor, Set<HelioServiceInstanceDescriptor>>();

    /**
     * Register a capability for a given service.
     * @param serviceName name of the service.
     * @param capabilities capabilities of the service
     * @param label the label for the service
     * @param description the description of the service.
     * @param wsdlFile the wsdlFiles pointing to valid endpoints for this service.
     */
    protected void registerServiceInstance(String serviceName, String label, String description, HelioServiceCapability[] capabilities, HelioServiceCapability currentCapability, String wsdlFile) {
    	// create the descriptor
    	HelioServiceDescriptor serviceDescriptor = 
    		new GenericHelioServiceDescriptor(serviceName, label, description, capabilities);
    	registerServiceInstance(serviceDescriptor, currentCapability, wsdlFile);
    }

    /**
     * Register a capability for a given service.
     * @param serviceDescriptor the descriptor of the service.
     * @param the capability to register the service for.
     * @param wsdlFile URLs pointing to the WSDL endpoints.
     */
    protected void registerServiceInstance(final HelioServiceDescriptor serviceDescriptor, final HelioServiceCapability capability, final String wsdlFile) {
    	HelioServiceDescriptor serviceDescriptor2 = registerServiceDescriptor(serviceDescriptor);
    	// sanity check
    	if (serviceDescriptor2 == null) {
    		throw new ServiceResolutionException("Internal Error: Unable to register service descriptor " + serviceDescriptor2 + ".");
    	}
    	
    	// add capabilty if necessary
    	serviceDescriptor2.addCapability(capability);
    
    	// create the instance descriptor.
		boolean success = registerServiceInstance(serviceDescriptor2, capability, HelioFileUtil.asURL(wsdlFile));
		// sanity check
		if (success == false) {
			throw new ServiceResolutionException("Unable to register services. Check log for more information.");
		}
    }

    /**
     * Register a service descriptor. If the service descriptor already exists it is ignored.
     * @param helioServiceDescriptor the service descriptor.
     * @return true if the descriptor has not been registered before. false if a previous instance of the service already existed.
     */
    public HelioServiceDescriptor registerServiceDescriptor(HelioServiceDescriptor helioServiceDescriptor) {
        HelioServiceDescriptor ret;
        boolean exists = serviceDescriptors.add(helioServiceDescriptor);
    	if (!exists) {
    	    ret = null;
    	    for (HelioServiceDescriptor currentDescriptor : serviceDescriptors) {
                if (currentDescriptor.equals(helioServiceDescriptor)) {
                    ret = currentDescriptor;
                    break;
                }
            }
    	    
    	    if (ret == null) {
    	        throw new RuntimeException("Internal Error: unable to register " + helioServiceDescriptor + ".");
    	    }
    	    
    	    if (_LOGGER.isDebugEnabled()) {
    	        _LOGGER.debug("Service descriptor '" +  ret + "' has been previously registered. This is probably fine.");
    	    }
    	} else {
    	    _LOGGER.info("Service descriptor '" +  helioServiceDescriptor + "' has been successfully registered.");
    	    ret = helioServiceDescriptor;
    	}
    	
    	return ret;
    }

    /**
     * Register a specific instance of a service capability.
     * @param instanceDescriptor the instance descriptor
     * @param capability the capability to register and endpoint for
     * @param wsdlFile the location of the wsdl file including '?wsdl'
     * @return true if the descriptor has not been registered before, false if a instance of this descriptor already exists.
     */
    public boolean registerServiceInstance(HelioServiceDescriptor serviceDescriptor, HelioServiceCapability capability, URL wsdlFile) {
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
    	
    	HelioServiceInstanceDescriptor instanceDescriptor = new HelioServiceInstanceDescriptor(serviceDescriptor, capability, wsdlFile);

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
    public HelioServiceDescriptor getServiceDescriptor(String name) {
    	HelioServiceDescriptor ret = null;
    	for (HelioServiceDescriptor currentDescriptor : serviceDescriptors) {
    		if (currentDescriptor.getName().equals(name)) {
    			ret = currentDescriptor;
    			break;
    		}
    	}
    	return ret;
    }

    /**
     * TODO: change return type to AccessInterface object.
     */
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
     * TODO: change return type
     */
    @Override
    public URL getBestEndpoint(String name, HelioServiceCapability capability) {
        HelioServiceDescriptor serviceDescsriptor = getServiceDescriptor(name);
        if (serviceDescsriptor == null) {
            throw new ServiceResolutionException("No service found with name " + name + ".");
        }
        URL bestEndpoint = getBestEndpoint(serviceDescsriptor, capability);
        if (bestEndpoint == null) {
            throw new ServiceResolutionException("No service instance found for " + serviceDescsriptor);
        }
        return bestEndpoint;
    }

    /**
     * This implementation just takes the first match.
     */
    @Override
    public URL getBestEndpoint(HelioServiceDescriptor descriptor, HelioServiceCapability capability) {
    	AssertUtil.assertArgumentNotNull(descriptor, "descriptor");
    	AssertUtil.assertArgumentNotNull(capability, "capability");
    	Set<HelioServiceInstanceDescriptor> serviceInstances = instanceDescriptors.get(descriptor);
    	if (serviceInstances == null || serviceInstances.isEmpty()) {
    		return null;
    	}
    	
    	// just take the first entry that matches the capability
    	for (HelioServiceInstanceDescriptor helioServiceInstanceDescriptor : serviceInstances) {
    	    if (helioServiceInstanceDescriptor.getCapabiltiy().equals(capability)) {
    	        return helioServiceInstanceDescriptor.getWsdlFile();
    	    }
    	}
    	
    	return null; 
    }


    public AbstractHelioServiceRegistryDao() {
        super();
    }
}

/**
 * Descriptor of a concrete instance of a service.
 * Do not instanciate this class outside of {@link AbstractHelioServiceRegistryDao}. It's public for the XMLEncoder
 * @author MarcoSoldati
 *
 */
class HelioServiceInstanceDescriptor {
    /**
     * The assigned service descriptor
     */
    private final HelioServiceDescriptor serviceDescriptor;

    /**
     * The capability described by this url.
     */
    private final HelioServiceCapability capabiltiy;
    
    /**
     * Pointer to the WSDL file
     */
    private final URL wsdlFile;
    
    
    /**
     * Container to hold the reference to a specific instance of a service.
     * @param serviceDescriptor the service descriptor
     * @param capability the capabilty described by this descriptor
     * @param wsdlFile the wsdl file assigned with this descripor
     */
    public HelioServiceInstanceDescriptor(HelioServiceDescriptor serviceDescriptor, HelioServiceCapability capability, URL wsdlFile) {
        AssertUtil.assertArgumentNotNull(serviceDescriptor, "serviceDescriptor");
        AssertUtil.assertArgumentNotNull(capability, "capability");
        AssertUtil.assertArgumentNotNull(wsdlFile, "wsdlFile");
        this.capabiltiy = capability;
        this.serviceDescriptor = serviceDescriptor;
        this.wsdlFile = wsdlFile;
    }
    
    /**
     * Get the descriptor of the service.
     * @return the service descriptor.
     */
    public HelioServiceDescriptor getServiceDescriptor() {
        return serviceDescriptor;
    }
    
    /**
     * The capability implemented by this descriptor.
     * @return the capability
     */
    public HelioServiceCapability getCapabiltiy() {
        return capabiltiy;
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
            capabiltiy.equals(((HelioServiceInstanceDescriptor)other).capabiltiy) &&
            wsdlFile.equals(((HelioServiceInstanceDescriptor)other).wsdlFile);
        
    }
    
    @Override
    public int hashCode() {
        return wsdlFile.hashCode() *13 + serviceDescriptor.hashCode() *11 + capabiltiy.hashCode() * 31;
    }
}