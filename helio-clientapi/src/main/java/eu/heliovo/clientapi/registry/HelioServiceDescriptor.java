package eu.heliovo.clientapi.registry;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Describe a service in general (i.e. not a particular instance of the service). 
 * The description can be retrieved through the {@link HelioServiceRegistry}.
 * The business key of this object is the "name - type" tuple. 
 * @author MarcoSoldati
 *
 */
public class HelioServiceDescriptor {
	/**
	 * A unique identifier of the service
	 */
	private final String name;
	
	/**
	 * A label for the service
	 */
	private final String label;
	
	/**
	 * A description of the service
	 */
	private final String description;

	/**
	 * The type of this service
	 */
	private final HelioServiceType type;
	
	/**
	 * Create a descriptor for a service.
	 * @param name the name of the service, must not be null.
	 * @param the type of the service, Must not be null
	 * @param label the label of the service. Will match the name if null.
	 * @param description the description of the service. May be null.
	 */
	public HelioServiceDescriptor(String name, HelioServiceType type, String label, String description) {
		AssertUtil.assertArgumentHasText(name, "name");
		AssertUtil.assertArgumentNotNull(type, "type");
		this.name = name;
		this.type = type;
		this.label = label == null ? name : label;
		this.description = description;
	}
	
	/**
	 * Return the name of the service
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the label for the service.
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Get a short description for the service.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the type of a service
	 * @return the type of a service.
	 */
	public HelioServiceType getType() {
		return type;
	}
	
	/**
	 * Check if the name and type are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof HelioServiceDescriptor))
			return false;
		
		return name.equals(((HelioServiceDescriptor)obj).name) && type.equals(((HelioServiceDescriptor)obj).type);
	}
	
	@Override
	public int hashCode() {
		return 37 * name.hashCode() + 11 * type.hashCode();
	}
}
