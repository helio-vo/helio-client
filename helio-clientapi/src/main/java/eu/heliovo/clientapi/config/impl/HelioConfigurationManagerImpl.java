package eu.heliovo.clientapi.config.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.config.HelioConfigurationManager;
import eu.heliovo.clientapi.config.HelioPropertyHandler;
import eu.heliovo.clientapi.model.DomainValueDescriptor;
import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Implementation of a HELIO configuration manager to read the property descriptors of some selected fields.
 * This implementation delegates the actual loading task to several property handler instances.
 * @author MarcoSoldati
 *
 */
public class HelioConfigurationManagerImpl implements HelioConfigurationManager {
    
    private static final Logger LOGGER = Logger.getLogger(HelioConfigurationManagerImpl.class);

    /**
     * the property handlers registered with this configuration
     */
    private Collection<HelioPropertyHandler> propertyHandlers;
   
    @SuppressWarnings("unchecked")
    @Override
    public List<? extends HelioCatalogueDescriptor> getCatalogueDescriptors(HelioServiceName serviceName, String serviceVariant) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(serviceName, serviceVariant, QueryService.class, "from");
        if (propertyDescriptor == null || !(propertyDescriptor instanceof ConfigurablePropertyDescriptor<?>)) {
            return Collections.EMPTY_LIST;
        }
        
        ConfigurablePropertyDescriptor<HelioCatalogueDescriptor> genericPropertyDescriptor = 
                (ConfigurablePropertyDescriptor<HelioCatalogueDescriptor>) propertyDescriptor;
        
        List<HelioCatalogueDescriptor> ret = new  ArrayList<HelioCatalogueDescriptor>();
        for (DomainValueDescriptor<?> catalogueDescriptor : genericPropertyDescriptor.getValueDomain()) {
            if (catalogueDescriptor instanceof HelioCatalogueDescriptor) {
                ret.add((HelioCatalogueDescriptor) catalogueDescriptor);
            } else {
                LOGGER.info("Ignoring catalogue descriptor as it is not an instance of a HelioCatalogueDescriptor: " + catalogueDescriptor);
            }
        }
        return ret;
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(HelioService helioService, String propertyName) {
        AssertUtil.assertArgumentNotNull(helioService, "helioService");
        return getPropertyDescriptor(helioService.getServiceName(), helioService.getServiceVariant(), helioService.getClass(), propertyName);
    }
    
    @Override
    public PropertyDescriptor getPropertyDescriptor(HelioServiceName serviceName, String serviceVariant,
            Class<? extends HelioService> serviceClass, String propertyName) {
        AssertUtil.assertArgumentNotNull(serviceName, "serviceName");
        AssertUtil.assertArgumentNotNull(serviceClass, "serviceClass");
        AssertUtil.assertArgumentNotNull(propertyName, "propertyName");
        
        HelioPropertyHandler bestMatch = findBestMatchingPropertyHandler(
                serviceName, serviceVariant, propertyName);
        
        // create a default handler for unknown property handlers
        PropertyDescriptor propertyDescriptor;
        if (bestMatch == null) {
            propertyDescriptor = getStandardPropertyDescriptor(propertyName, serviceClass);
        } else {
            propertyDescriptor = bestMatch.getPropertyDescriptor(serviceClass);
            if (propertyDescriptor == null) {
                throw new RuntimeException("Return type of HelioPropertyHandler.getPropertyDescriptor() must not be null.");
            }
        }        
        return propertyDescriptor;
    }

    /**
     * Find all property handlers that registered for property 'propertyName' and service 'serviceName', 
     * then check if one of them is registered for a specific 'serviceVariant'. If not return the generic
     * version without a 'serviceVariant' or null, if none has been found.
     * @param serviceName must not be null
     * @param serviceVariant may be null
     * @param propertyName must not be null.
     * @return
     */
    private HelioPropertyHandler findBestMatchingPropertyHandler(
            HelioServiceName serviceName, String serviceVariant,
            String propertyName) {
        HelioPropertyHandler bestMatch = null;
        for (HelioPropertyHandler propertyHandler : propertyHandlers) {
            // check if service and property names are equal
            if (serviceName.equals(propertyHandler.getHelioServiceName()) && 
                propertyName.equals(propertyHandler.getPropertyName())) {
                // if service variant of property handler is null, we have a good but probably not the best match
                if (propertyHandler.getServiceVariant() == null) {
                    bestMatch  = propertyHandler;
                    // if service variant is null we have the best match
                    if (serviceVariant == null) {
                        break;
                    } 
                // if serviceVariants are equal we have the best match.
                } else if (propertyHandler.getServiceVariant().equals(serviceVariant)) {
                    bestMatch  = propertyHandler;
                    break;
                } // otherwise we have no match and just continue.
            }
        }
        return bestMatch;
    }
    
    /**
     * Get the stanndard property descriptor for a given property in a given bean class
     * @param propertyName the name of the property to look for. Must not be null.
     * @param beanClass the class of the bean to search the property. Must not be null.
     * @return the property descriptor for the given property name.
     * @throws BeansException in case the property cannot be found.
     */
    private PropertyDescriptor getStandardPropertyDescriptor(
            String propertyName, Class<?> beanClass) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(beanClass, propertyName);
        return propertyDescriptor;
    }
    
    
    /**
     * @return the propertyHandlers
     */
    public Collection<HelioPropertyHandler> getPropertyHandlers() {
        return propertyHandlers;
    }

    /**
     * Register the property handlers used by this manager.
     * @param propertyHandlers the propertyHandlers to set
     */
    public void setPropertyHandlers(
            Collection<HelioPropertyHandler> propertyHandlers) {
        this.propertyHandlers = propertyHandlers;
    }
}
