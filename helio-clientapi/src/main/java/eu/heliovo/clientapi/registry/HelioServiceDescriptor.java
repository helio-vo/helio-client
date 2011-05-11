package eu.heliovo.clientapi.registry;

import java.util.Set;

/**
 * Describe a service in general (i.e. not a particular instance of the service). 
 * The description can be retrieved through the {@link HelioServiceRegistryDao}.
 * The business key of this object is the "name - type" tuple. 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioServiceDescriptor {

	/**
	 * Return the name of the service
	 * @return the name
	 */
	public String getName();

	/**
	 * Get the label for the service.
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Get a short description for the service.
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Get the capabilities of a service
	 * @return the type of a service.
	 */
	public Set<HelioServiceCapability> getCapabilities();

	/**
	 * Check if the name and capabilties are equal
	 */
	public boolean equals(Object obj);

	/**
	 * Create the hash code from name and capabilities
	 * @return the hashcode.
	 */
	public int hashCode();

    /**
     * Add a capability to this service descriptor.
     * @param capability the capability to add.
     * @return true if the capability did not already exist, false otherwise.
     */
    public boolean addCapability(HelioServiceCapability capability);

}