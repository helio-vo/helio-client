package eu.heliovo.clientapi.model.catalog;

import java.util.List;

import eu.heliovo.clientapi.model.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;

/**
 * Descriptor for the properties of a catalogue.
 * @author MarcoSoldati
 *
 */
public interface HelioCatalogueDescriptor extends DomainValueDescriptor<String> {

    /**
     * Get the field descriptors for this catalogue.
     * @return the field descriptors describe the content of the catalog.
     */
    public List<HelioFieldDescriptor<?>> getFieldDescriptors();
    
}
