package eu.heliovo.clientapi.registry;

import java.net.URL;


public interface HelioServiceRegistry {
	/**
	 * Get a list of service descriptors.
	 * @return a read-only list of all service descriptors in no particular order.
	 */
	public HelioServiceDescriptor[] getAllServiceDescriptors();
	
	/**
	 * Get a service descriptor by name
	 * @param name the name of the descriptor to lookup. Must not be null.
	 * @param type the type of the descriptor to lookup. Must not be null.
	 * @return the descriptor or null if none was found.
	 */
	public HelioServiceDescriptor getServiceDescriptor(String name, HelioServiceType type);
	
	/**
	 * Get all endpoints for an instance of a service. 
	 * @param descriptor the descriptor to use
	 * @return an array of URL pointing to the WSDL files and
	 * an empty array if no endpoints have been found.
	 */
	public URL[] getAllEndpoints(HelioServiceDescriptor descriptor);
	
	/**
	 * Get the endpoint for the "best" instance of a service. 
	 * @param descriptor the descriptor to use
	 * @return the URL pointing to the WSDL files or null if no endpoint has been found.
	 */
	public URL getBestEndpoint(HelioServiceDescriptor descriptor);
}
