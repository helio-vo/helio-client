package eu.heliovo.clientapi.model.field;

/**
 * Descriptor for the values of a domain. 
 * @author marco soldati at fhnw ch
 *
 * @param <T> the data type wrapped by this descriptor.
 */
public interface DomainValueDescriptor <T> {
	/**
	 * Get the actual Value. Never null.
	 * @return the value.
	 */
	public T getValue();
	
	/**
	 * Get the label. Should use the toString method of the value if unknown.
	 * @return the label to display to the user.
	 */
	public String getLabel();
	
	/**
	 * Get the description assigned with this descriptor. May be null.
	 * @return a short description.
	 */
	public String getDescription();
	
	/**
	 * Implementations of this interface should overwrite the toString method.
	 * @return a string representation of the value
	 */
	@Override
	public String toString();
	
	/**
	 * The equals method should rely on the equals method of the stored value.
	 * @param obj the object to compare
	 * @return true if the value of two objects are equal.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * The hash code should rely on the hashCode of the wrapped value.
	 * @return the hashcode.
	 */
	@Override
	public int hashCode();
	
}
