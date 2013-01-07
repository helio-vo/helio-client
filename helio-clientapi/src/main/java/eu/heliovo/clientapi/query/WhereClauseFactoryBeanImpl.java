package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.config.HelioConfigurationManager;
import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * A factory bean to create new where clauses by reading the configuration from the configurationManager.
 * @author MarcoSoldati
 *
 */
public class WhereClauseFactoryBeanImpl implements WhereClauseFactoryBean {
    /**
     * the HEC catalogue descriptor dao
     */
    private HelioConfigurationManager configurationManager;
    
    @Override
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String listName) {
        List<HelioFieldDescriptor<?>> fieldDescriptors = null;
        List<? extends HelioCatalogueDescriptor> catalogueDescriptors = 
                configurationManager.getCatalogueDescriptors(helioServiceName, null);
        if (catalogueDescriptors.size() > 0) {
            HelioCatalogueDescriptor catalogueDescriptor = findByListName(catalogueDescriptors, listName);
            fieldDescriptors = catalogueDescriptor != null ? catalogueDescriptor.getFieldDescriptors() : null;
        }
        
        if (fieldDescriptors == null) {
            fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
        }
        WhereClause whereClause = new WhereClause(listName, fieldDescriptors);
        return whereClause;
    }

    /**
     * Find an event list descriptor by its name.
     * @param catalogueDescriptors the descriptors to search
     * @param listName the list name
     * @return the descriptor with the given name or null if not found.
     */
    private HelioCatalogueDescriptor findByListName(List<? extends HelioCatalogueDescriptor> catalogueDescriptors, 
    		String listName) {
        for (HelioCatalogueDescriptor catalogueDescriptor : catalogueDescriptors) {
            if (listName.equals(catalogueDescriptor.getValue())) {
                return catalogueDescriptor;
            }
        }
        return null;
    }

    /**
     * @return the configurationManager
     */
    public HelioConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * @param configurationManager the configurationManager to set
     */
    public void setConfigurationManager(HelioConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }
}
