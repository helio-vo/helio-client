package eu.heliovo.clientapi.config.catalog.propertyhandler;

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.List;

import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.config.HelioPropertyHandler;
import eu.heliovo.clientapi.config.catalog.dao.EventListDescriptorDao;
import eu.heliovo.clientapi.model.catalog.descriptor.EventListDescriptor;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Property handler for the HEC from value.
 * @author MarcoSoldati
 *
 */
public class HecFromPropertyHandler implements HelioPropertyHandler {
    /**
     * The logger
     */
    //private static final Logger _LOGGER = Logger.getLogger(HecFromPropertyHandler.class);
    
    /**
     * Name of the service
     */
    private static final HelioServiceName SERVICE_NAME = HelioServiceName.HEC;
    
    /**
     * Name of the service variant
     */
    private static final String SERVICE_VARIANT = null;

    /**
     * Name of the property
     */
    private static final String PROPERTY_NAME = "from";
    
    /**
     * Cache the loaded property descriptor.
     */
    private ConfigurablePropertyDescriptor<List<String>> propertyDescriptor;
    
    /**
     * DAO to get the HEC catalogue descriptors.
     */
    private EventListDescriptorDao eventListDescriptorDao;
    
    @Override
    public HelioServiceName getHelioServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public String getServiceVariant() {
        return SERVICE_VARIANT;
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    // init the HEC configuration
    @Override
    public void init() {
        ConfigurablePropertyDescriptor<List<String>> propDescriptor;
        try {
            propDescriptor = new ConfigurablePropertyDescriptor<List<String>>(getPropertyName(), QueryService.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException("Internal Error: Unable to create 'from' property: " + e.getMessage(), e);
        }
        
        Collection<EventListDescriptor> domainValues = eventListDescriptorDao.getDomainValues();
        propDescriptor.setValueDomain(domainValues);
        this.propertyDescriptor = propDescriptor;
    }

    @Override
    public ConfigurablePropertyDescriptor<List<String>> getPropertyDescriptor(HelioService helioService) {
        return propertyDescriptor;
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
    public void setEventListDescriptorDao(
            EventListDescriptorDao eventListDescriptorDao) {
        this.eventListDescriptorDao = eventListDescriptorDao;
    }
}
