package eu.heliovo.clientapi.registry;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Describe a service in general (i.e. not a particular instance of the service). 
 * The description can be retrieved through the {@link HelioServiceRegistry}.
 * The business key of this object is the "name - type" tuple. 
 * @author MarcoSoldati
 *
 */
public class GenericHelioServiceDescriptor implements HelioServiceDescriptor {
	
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
	public GenericHelioServiceDescriptor(String name, HelioServiceType type, String label, String description) {
		AssertUtil.assertArgumentHasText(name, "name");
		AssertUtil.assertArgumentNotNull(type, "type");
		this.name = name;
		this.type = type;
		this.label = label == null ? name : label;
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#getType()
	 */
	@Override
	public HelioServiceType getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GenericHelioServiceDescriptor))
			return false;
		
		return name.equals(((GenericHelioServiceDescriptor)obj).name) && type.equals(((GenericHelioServiceDescriptor)obj).type);
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#hashCode()
	 */
	@Override
	public int hashCode() {
		return 37 * name.hashCode() + 11 * type.hashCode();
	}
}
