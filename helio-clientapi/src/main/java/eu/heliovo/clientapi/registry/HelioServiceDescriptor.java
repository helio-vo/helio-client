package eu.heliovo.clientapi.registry;

/**
 * Describe a service in general (i.e. not a particular instance of the service). 
 * The description can be retrieved through the {@link HelioServiceRegistry}.
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
	 * Get the type of a service
	 * @return the type of a service.
	 */
	public HelioServiceType getType();

	/**
	 * Check if the name and type are equal
	 */
	public boolean equals(Object obj);

	/**
	 * Create the hash code from name and type
	 * @return the hashcode.
	 */
	public int hashCode();

}