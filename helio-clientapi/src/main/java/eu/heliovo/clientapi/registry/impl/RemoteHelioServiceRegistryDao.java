package eu.heliovo.clientapi.registry.impl;


import static uk.ac.starlink.registry.RegistryRequestFactory.keywordSearch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.starlink.registry.AbstractRegistryClient;
import uk.ac.starlink.registry.BasicCapability;
import uk.ac.starlink.registry.BasicResource;
import uk.ac.starlink.registry.SoapClient;
import eu.heliovo.clientapi.registry.AccessInterface;
import eu.heliovo.clientapi.registry.AccessInterfaceType;
import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.ServiceResolutionException;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * A registry client to access the HELIO Search Registry in order to find the
 * appropriate services.
 * @author donal k fellows
 * @author marco soldati at fhnw ch
 */
class RemoteHelioServiceRegistryDao extends AbstractHelioServiceRegistryDao {
	private static final Logger _LOGGER = Logger.getLogger(RemoteHelioServiceRegistryDao.class);

	/** The default address of the registry. */
	public static final String REGISTRY_AT_MSSL = "http://msslxw.mssl.ucl.ac.uk:8080/helio_registry/services/RegistryQueryv1_0";
	
	/**
	 * Client stub to the registry.
	 */
	private AbstractRegistryClient<BasicResource> registry;

	/**
	 * Create the registry impl and initialize it accordingly.
	 */
	public RemoteHelioServiceRegistryDao() {
	    setRegistryURL(HelioFileUtil.asURL(REGISTRY_AT_MSSL));
	}

	/**
	 * Set the url to use to access the registry.
	 * @param url the url to use.
	 */
	public void setRegistryURL(URL url)  {
		registry = new PatchedBasicRegistryClient(new SoapClient(url));
		reinit();
	}

	/**
	 * Delete the full content of the registry and repopulate it
	 */
	private void reinit() {
	    this.instanceDescriptors.clear();
	    this.serviceDescriptors.clear();
	    init();
    }
	
	
    /**
     * Update the service descriptors with all known services. 
     * @throws ServiceResolutionException
     */
    private void init() throws ServiceResolutionException {
        List<BasicResource> allServices;
        try {
            allServices = registry.getResourceList(keywordSearch(
                    new String[] { "*" }, false));
        } catch (IOException e) {
            throw new ServiceResolutionException("failed to access registry", e);
        }
        
        

        // loop through all services and qualify them
        for (BasicResource r : allServices) {
            _LOGGER.info("found match: " + r.getIdentifier() + " (" + r.getTitle() + ")");
            String description = getDescription(r);
            HelioServiceDescriptor serviceDescriptor = new GenericHelioServiceDescriptor(r.getIdentifier(), r.getTitle(), description, (HelioServiceCapability)null);
            serviceDescriptor = registerServiceDescriptor(serviceDescriptor);
            
            // register capabilities
            for (BasicCapability c : r.getCapabilities()) {
                HelioServiceCapability cap = HelioServiceCapability.findCapabilityById(c.getStandardId());
                System.out.println(c.getStandardId() + ", " + c.getXsiType() + ", " +  c.getDescription() + ", " + c.getVersion() + ", "+ c.getAccessUrl());
                if (cap == null) {
                    //cap = HelioServiceCapability.register(c.getStandardId(), c.getXsiType(), c.getDescription(), c.getVersion());
                    cap = HelioServiceCapability.UNKNOWN;
                }
                
                AccessInterfaceType accessInterfaceType = AccessInterfaceType.findInterfaceTypeByXsiType(c.getXsiType());
                
                if (accessInterfaceType == null) {
                    accessInterfaceType = AccessInterfaceType.register(c.getXsiType(), c.getXsiType());
                }
                try {
                    AccessInterface accessInterface = new AccessInterfaceImpl(accessInterfaceType, new URL(c.getAccessUrl()));
                    registerServiceInstance(serviceDescriptor, cap, accessInterface);
                } catch (MalformedURLException e) {
                    _LOGGER.warn("Unable to register URL " + c.getAccessUrl() + " for " + serviceDescriptor + "::" + cap + ": " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Extract a description string from a basic resource.
     * @param r the resource to read
     * @return the description as a multiline string or null if not description.
     */
    private String getDescription(BasicResource r) {
        StringBuilder desc = new StringBuilder();
        if (r.getTitle() != null) {
            desc.append(r.getTitle()).append("\n");            
        }
        if (r.getReferenceUrl() != null) {
            desc.append("reference url: ").append(r.getReferenceUrl()).append("\n");            
        }
        if (r.getPublisher() != null) {
            desc.append("publisher: ").append(r.getPublisher()).append("\n");            
        }
        if (r.getContact() != null) {
            desc.append("contact: ").append(r.getContact()).append("\n");            
        }
        if (desc.length() == 0) {
            return null;
        }
        // remove trailing new line.
        desc.delete(desc.length()-1, desc.length());
        return desc.toString();
    }
}
