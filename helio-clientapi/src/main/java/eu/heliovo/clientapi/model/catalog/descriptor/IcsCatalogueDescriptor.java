package eu.heliovo.clientapi.model.catalog.descriptor;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import eu.heliovo.clientapi.model.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;

/**
 * Describe an instrument.
 * 
 * @author MarcoSoldati
 * 
 */
public class IcsCatalogueDescriptor extends AbstractCatalogueDescriptor implements DomainValueDescriptor<String> {
    
    private static final HelioFieldDescriptor<?>[] propDescriptors = new HelioFieldDescriptor<?>[]  {
        new HelioFieldDescriptor<String>("name", "ICS catalogue Name", "Name of the ICS catalogue", FieldType.STRING),
    };
	
    /**
     * Bean info class for the {@link IcsCatalogueDescriptor}
     * @author MarcoSoldati
     *
     */
    static class IcsCatalogueDescriptorBeanInfo extends AbstractCatalogueDescriptor.AbstractCatalogueDescriptorBeanInfo<IcsCatalogueDescriptor> {
        private static IcsCatalogueDescriptorBeanInfo instance = new IcsCatalogueDescriptorBeanInfo();
        
        /**
         * Get the singleton instance of the BeanInfo
         * @return the catalog descriptor
         */
        public static IcsCatalogueDescriptorBeanInfo getInstance() {
            return instance;
        }

        /**
         * Cache for the property descriptors.
         */
        private final PropertyDescriptor[] propertyDescriptors;
        
        /**
         * Hide the default constructor. Use getInstance() instead.
         */
        private IcsCatalogueDescriptorBeanInfo() {
            super(IcsCatalogueDescriptor.class);
            propertyDescriptors = asPropertyDescriptor(propDescriptors);        }
        
        private PropertyDescriptor[] asPropertyDescriptor(HelioFieldDescriptor<?>[] fieldDescriptors) {
            PropertyDescriptor[] ret = new PropertyDescriptor[fieldDescriptors.length];
            for (int i = 0; i < fieldDescriptors.length; i++) {
                HelioFieldDescriptor<?> fd = fieldDescriptors[i];
                ret[i] = createPropertyDescriptor(fd.getId(), fd.getLabel(), fd.getDescription());
            }
            return ret;
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            return propertyDescriptors;
        }
    }

    /**
     * Id of the instrument
     */
    private final String id;

    /**
     * Label presented to the user
     */
    private final String label;

    /**
     * Optional description
     */
    private final String description;

    /**
     * The descriptor of an instrument
     * 
     * @param id
     *            must not be null.
     * @param label
     *            must not be null.
     * @param description
     *            optional floating text description of the instrument.
     */
    public IcsCatalogueDescriptor(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }
    
    @Override
    public BeanInfo getBeanInfo() {
        return IcsCatalogueDescriptorBeanInfo.getInstance();
    }
    
    @Override
    public String getValue() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IcsCatalogueDescriptor other = (IcsCatalogueDescriptor) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
