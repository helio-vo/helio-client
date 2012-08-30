package eu.heliovo.clientapi.config.catalog.propertyhandler;

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.List;

import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.config.HelioPropertyHandler;
import eu.heliovo.clientapi.config.catalog.dao.IlsCatalogueDescriptorDao;
import eu.heliovo.clientapi.model.catalog.descriptor.IlsCatalogueDescriptor;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Property handler for the HEC from value.
 * @author MarcoSoldati
 *
 */
public class IlsFromPropertyHandler implements HelioPropertyHandler {
    
    /**
     * Name of the service
     */
    private static final HelioServiceName SERVICE_NAME = HelioServiceName.ILS;
    
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
     * Keep a reference to the catalogue descriptor dao.
     */
    private IlsCatalogueDescriptorDao catalogueDescriptorDao; 
    
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

    // init the configuration
    @Override
    public void init() {
        ConfigurablePropertyDescriptor<List<String>> propDescriptor;
        try {
            propDescriptor = new ConfigurablePropertyDescriptor<List<String>>(getPropertyName(), QueryService.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException("Internal Error: Unable to create 'from' property: " + e.getMessage(), e);
        }
        
        Collection<IlsCatalogueDescriptor> domainValues = catalogueDescriptorDao.getDomainValues();
        propDescriptor.setValueDomain(domainValues);
        this.propertyDescriptor = propDescriptor;
    }
    
    @Override
    public ConfigurablePropertyDescriptor<List<String>> getPropertyDescriptor(Class<? extends HelioService> serviceClass) {
        return propertyDescriptor;
    }

    /**
     * @return the ilsCatalogueDescriptorDao
     */
    public IlsCatalogueDescriptorDao getCatalogueDescriptorDao() {
        return catalogueDescriptorDao;
    }

    /**
     * @param ilsCatalogueDescriptorDao the ilsCatalogueDescriptorDao to set
     */
    public void setCatalogueDescriptorDao(IlsCatalogueDescriptorDao ilsCatalogueDescriptorDao) {
        this.catalogueDescriptorDao = ilsCatalogueDescriptorDao;
    }
    
    
}
