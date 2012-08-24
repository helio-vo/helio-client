package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.config.catalog.dao.EventListDescriptorDao;
import eu.heliovo.clientapi.model.catalog.descriptor.EventListDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
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
    private EventListDescriptorDao eventListDescriptorDao;
    
    /**
     * Create a new where clause for a given service and catalog name.
     * @param helioServiceName the service name.
     * @param listName the list name.
     * @return the created where clause or an empty where clause if the catalog does not support where clauses.
     */
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String listName) {
        List<HelioFieldDescriptor<?>> fieldDescriptors = null;
        if (HelioServiceName.HEC.equals(helioServiceName)) {
            EventListDescriptor eventListDescriptor = eventListDescriptorDao.findByListName(listName);
            fieldDescriptors = eventListDescriptor != null ? eventListDescriptor.getFieldDescriptors() : null;
        }
        
        if (fieldDescriptors == null) {
            fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
        }
        WhereClause whereClause = new WhereClause(listName, fieldDescriptors);
        return whereClause;
    }

    /**
     * @return the eventListDescriptorDao
     */
    public EventListDescriptorDao getEventListDescriptorDao() {
        return eventListDescriptorDao;
    }

    /**
     * @param eventListDescriptorDao the eventListDescriptorDao to set
     */
    public void setEventListDescriptorDao(EventListDescriptorDao eventListDescriptorDao) {
        this.eventListDescriptorDao = eventListDescriptorDao;
    }
    
}
