package eu.heliovo.clientapi.model.service;

import eu.heliovo.registryclient.HelioServiceName;


/**
 * Description of a HELIO service.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioService {
	/**
	 * Get the name of the service. Must not be null.
	 * @return the name of the service.
	 */
	public HelioServiceName getName();
	
	/**
	 * A free text description for user feedback. May be null.  
	 * @return the description.
	 */
	public String getDescription();
	
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
