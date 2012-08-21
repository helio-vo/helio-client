package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.field.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.service.dao.HecCatalogueDescriptorDao;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * A factory bean to create new where clauses.
 * @author MarcoSoldati
 *
 */
public class WhereClauseFactoryBean {
    /**
     * the HEC catalogue descriptor dao
     */
    private HecCatalogueDescriptorDao hecCatalogueDescriptorDao;
    
    /**
     * Create a new where clause for a given service and catalog name.
     * @param helioServiceName the service name.
     * @param catalogName the catalog name.
     * @return the created where clause or an empty where clause if the catalog does not support where clauses.
     */
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String catalogName) {
        List<HelioFieldDescriptor<?>> fieldDescriptors = null;
        if (HelioServiceName.HEC.equals(helioServiceName)) {
            fieldDescriptors = hecCatalogueDescriptorDao.getFieldDescriptors(catalogName);
        }
        
        if (fieldDescriptors == null) {
            fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
        }
        WhereClause whereClause = new WhereClause(catalogName, fieldDescriptors);
        return whereClause;
    }

    /**
     * @return the hecCatalogueDescriptorDao
     */
    public HecCatalogueDescriptorDao getHecCatalogueDescriptorDao() {
        return hecCatalogueDescriptorDao;
    }

    /**
     * @param hecCatalogueDescriptorDao the hecCatalogueDescriptorDao to set
     */
    public void setHecCatalogueDescriptorDao(HecCatalogueDescriptorDao hecCatalogueDescriptorDao) {
        this.hecCatalogueDescriptorDao = hecCatalogueDescriptorDao;
    }
    
}
