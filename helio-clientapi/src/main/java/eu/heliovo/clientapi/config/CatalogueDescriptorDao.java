package eu.heliovo.clientapi.config;

import java.util.List;

import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;

public interface CatalogueDescriptorDao {
    
    /**
     * Get the enumeration of valid catalogue names.
     * @return the domain values.
     */
    public List<? extends HelioCatalogueDescriptor> getDomainValues();
}
