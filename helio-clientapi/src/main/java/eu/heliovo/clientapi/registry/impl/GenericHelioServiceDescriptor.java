package eu.heliovo.clientapi.registry.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.heliovo.clientapi.registry.HelioServiceCapability;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistryDao;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Describe a service in general (i.e. not a particular instance of the service). 
 * The description can be retrieved through the {@link HelioServiceRegistryDao}.
 * The business key of this object is the "name". Thus the name must be unique. 
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
	private final Set<HelioServiceCapability> capabilities = new HashSet<HelioServiceCapability>();
	
	/**
	 * Create a descriptor for a service.
	 * @param name the name of the service, must not be null.
	 * @param label the label of the service. Will match the name if null.
	 * @param description the description of the service. May be null.
	 * @param capabilities the capabilities assigned with this service. Ignored if null. More capabilities can be added later.
	 * 
	 */
	public GenericHelioServiceDescriptor(String name, String label, String description, HelioServiceCapability ... capabilities) {
		AssertUtil.assertArgumentHasText(name, "name");
		this.name = name;
		this.label = label == null ? name : label;
		this.description = description;
		if (capabilities != null) {
		    Collections.addAll(this.capabilities, capabilities);
		}
	}

	/**
	 * Create a descriptor for a service.
	 * @param name the name of the service, must not be null.
	 * @param label the label of the service. Will match the name if null.
	 * @param description the description of the service. May be null.
	 * @param capabilities the capabilities assigned with this service. Ignored if null.
	 */
	public GenericHelioServiceDescriptor(String name, String label, String description, Collection<HelioServiceCapability> capabilities) {
	    AssertUtil.assertArgumentHasText(name, "name");
	    this.name = name;
	    this.label = label == null ? name : label;
	    this.description = description;
	    if (capabilities != null) {
            this.capabilities.addAll(capabilities);
        }
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
	public Set<HelioServiceCapability> getCapabilities() {
		return Collections.unmodifiableSet(capabilities);
	}
	
	   
    /**
     * Add a capability to this entry.
     * @param capability the capability to add
     * @return true if the capability did not already exist, false otherwise.
     */
    @Override
    public boolean addCapability(HelioServiceCapability capability) {
        return this.capabilities.add(capability);
    }
    
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GenericHelioServiceDescriptor))
			return false;
		return name.equals(((GenericHelioServiceDescriptor)obj).name);
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.registry.HelioServiceDescriptor#hashCode()
	 */
	@Override
	public int hashCode() {
		return 37 * name.hashCode();
	}
}