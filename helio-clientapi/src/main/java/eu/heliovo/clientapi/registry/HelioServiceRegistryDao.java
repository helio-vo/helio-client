package eu.heliovo.clientapi.registry;

import java.net.URL;

/**
 * Data access object to get access to a Helio Service Registry.
 * @author MarcoSoldati
 *
 */
public interface HelioServiceRegistryDao {
	/**
	 * Get a list of service descriptors.
	 * @return a read-only list of all service descriptors in no particular order.
	 */
	public HelioServiceDescriptor[] getAllServiceDescriptors() throws ServiceResolutionException;
	
	/**
	 * Get a service descriptor by name.
	 * @param name the name of the descriptor to lookup. Must not be null.
	 * @return the descriptor or null if none was found.
	 */
	public HelioServiceDescriptor getServiceDescriptor(String name) throws ServiceResolutionException;
	
	/**
	 * Get all endpoints for an instance of a service. 
	 * @param descriptor the descriptor to use
	 * @return an array of URL pointing to the WSDL files and
	 * an empty array if no endpoints have been found.
	 */
	public URL[] getAllEndpoints(HelioServiceDescriptor descriptor) throws ServiceResolutionException;
	
    /**
     * Convenience method to get the "best" endpoint by service name and capability.
     * @param name the name of the service
     * @param capability the capability to look for
     * @return URL pointing to the WSDL file or null if no endpoint has been found.
     * @throws ServiceResolutionException in case the service cannot be found.
     */
    public URL getBestEndpoint(String name, HelioServiceCapability capability) throws ServiceResolutionException;
    
    /**
	 * Get the endpoint for the "best" endpoint by service descriptor and capability. 
	 * @param descriptor the descriptor to use
	 * @param capability the capability to look for
	 * @return the URL pointing to the WSDL files or null if no endpoint has been found.
	 */
	public URL getBestEndpoint(HelioServiceDescriptor descriptor, HelioServiceCapability capability) throws ServiceResolutionException;
}
