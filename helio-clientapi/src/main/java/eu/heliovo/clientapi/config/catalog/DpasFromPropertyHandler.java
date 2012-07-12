package eu.heliovo.clientapi.config.catalog;

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.config.HelioPropertyHandler;
import eu.heliovo.clientapi.model.field.descriptor.InstrumentDescriptor;
import eu.heliovo.clientapi.model.service.dao.InstrumentDescriptorDao;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Property handler for the HEC from value.
 * @author MarcoSoldati
 *
 */
public class DpasFromPropertyHandler implements HelioPropertyHandler {
    
    /**
     * Name of the service
     */
    private static final HelioServiceName SERVICE_NAME = HelioServiceName.DPAS;
    
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
    private InstrumentDescriptorDao instrumentDescriptorDao;
    
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
        
        Collection<InstrumentDescriptor> domainValues = instrumentDescriptorDao.getDomainValues();
        domainValues = filterDpasInstruments(domainValues);
        
        propDescriptor.setValueDomain(domainValues);
        this.propertyDescriptor = propDescriptor;
    }

    /**
     * Filter those instruments that are part of the DPAS (i.e. where hasProviders() is true).
     * @param domainValues the dpas domain values.
     * @return the instruments registered in the DPAS.
     */
    private Collection<InstrumentDescriptor> filterDpasInstruments(Collection<InstrumentDescriptor> domainValues) {
        Collection<InstrumentDescriptor> ret = new HashSet<InstrumentDescriptor>();
        for (InstrumentDescriptor instrumentDescriptor : domainValues) {
            if (instrumentDescriptor.hasProviders()) {
                ret.add(instrumentDescriptor);
            }
        }
        return ret;
    }

    @Override
    public ConfigurablePropertyDescriptor<List<String>> getPropertyDescriptor() {
        return propertyDescriptor;
    }

    /**
     * @return the instrumentDescriptorDao
     */
    public InstrumentDescriptorDao getInstrumentDescriptorDao() {
        return instrumentDescriptorDao;
    }

    /**
     * @param instrumentDescriptorDao the instrumentDescriptorDao to set
     */
    public void setInstrumentDescriptorDao(
            InstrumentDescriptorDao instrumentDescriptorDao) {
        this.instrumentDescriptorDao = instrumentDescriptorDao;
    }
}
