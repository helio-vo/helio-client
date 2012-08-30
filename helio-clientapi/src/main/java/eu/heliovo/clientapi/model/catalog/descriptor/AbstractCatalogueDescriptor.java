package eu.heliovo.clientapi.model.catalog.descriptor;

import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;
import java.text.ParseException;
import java.util.Date;

import eu.heliovo.clientapi.config.AnnotatedBean;
import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.shared.util.DateUtil;

abstract class AbstractCatalogueDescriptor implements HelioCatalogueDescriptor, AnnotatedBean {
    
    /**
     * Bean info class for the {@link EventListDescriptor}
     * @author MarcoSoldati
     *
     */
    public abstract static class  AbstractCatalogueDescriptorBeanInfo <T extends AbstractCatalogueDescriptor> extends SimpleBeanInfo {
        /**
         * The class described by this catalogue descriptor
         */
        private Class<?> beanClass;

        public AbstractCatalogueDescriptorBeanInfo(Class<T> beanClass) {
            this.beanClass= beanClass;
        }
        
        protected ConfigurablePropertyDescriptor<?> createPropertyDescriptor(String propertyName, String displayName, String shortDescription) {
            return createPropertyDescriptor(propertyName, displayName, shortDescription, false);
        }
        
        protected ConfigurablePropertyDescriptor<?> createPropertyDescriptor(String propertyName, String displayName, String shortDescription, boolean isReadOnly) {
            try {
                ConfigurablePropertyDescriptor<?> propDescriptor;
                propDescriptor = new ConfigurablePropertyDescriptor<Object>(propertyName, beanClass, true, !isReadOnly);
                propDescriptor.setDisplayName(displayName);
                propDescriptor.setShortDescription(shortDescription);
                return propDescriptor;
            } catch (IntrospectionException e) {
                throw new IllegalStateException("Failed to create property descriptor '" + propertyName + "':" + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Helper method to convert a date string to a date object
     * @param time the time string
     * @return the converted date object in time zone UT.
     */
    protected static Date toDate(String time) {
        try {
            return DateUtil.fromIsoDate(time + "T00:00:00");
        } catch (ParseException e) {
            throw new IllegalArgumentException("String cannot be converted to Date: " + time);
        }
    }
}