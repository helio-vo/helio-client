package eu.heliovo.clientapi.model.field;

/**
 * Descriptor for the values of a domain. 
 * @author marco soldati at fhnw ch
 *
 * @param <T> the data type wrapped by this descriptor.
 */
public interface DomainValueDescriptor <T> {
	/**
	 * Get the actual Value
	 * @return
	 */
	public T getValue();
	
	/**
	 * Get the label.
	 * @return the label to display to the user.
	 */
	public String getLabel();
	
	/**
	 * Get the description assigned with this descriptor
	 * @return a short description.
	 */
	public String getDescription();
	
	/**
	 * Implementations of this interface should overwrite the toString method.
	 * @return a string representation of the value
	 */
	@Override
	public String toString();
}
