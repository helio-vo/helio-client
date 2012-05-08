package eu.heliovo.clientapi.model.service;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;


/**
 * Description of a HELIO service implementation.
 * This is the main interface that all implementation proxies to a service should implement.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioService {
	/**
	 * Get the name of the service. Must not be null.
	 * @return the name of the service.
	 */
	public HelioServiceName getServiceName();
	
	/**
	 * True if the service implementation supports a given capability. 
	 * @param capability the capability.
	 * @return true if supported.
	 */
	public boolean supportsCapability(ServiceCapability capability);
	
	/**
	 * Get the variant of a specific service.
	 * @return the variant id or null for the default variant or if there is no variant.
	 */
	public String getServiceVariant();
	
	/**
	 * Check if the name of two HelioServices is equal.
	 * @param obj the object to compare with 
	 * @return true if the name of two HelioServices is equal.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * Based on the hashcode of the name.
	 * @return the hashcode.
	 */
	@Override
	public int hashCode();

}
