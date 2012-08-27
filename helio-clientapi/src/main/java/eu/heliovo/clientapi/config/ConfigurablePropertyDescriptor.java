package eu.heliovo.clientapi.config;

import static java.util.Locale.ENGLISH;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.validation.Validator;

import eu.heliovo.clientapi.model.DomainValueDescriptor;

/**
 * Convenience property descriptor to add restrictions and value domains to a
 * JavaBean property.
 * 
 * @param <T>
 *            type of the property described by this descriptor.
 * @author MarcoSoldati
 * 
 */
public class ConfigurablePropertyDescriptor<T> extends PropertyDescriptor {
    /**
     * Key for the validator list
     */
    public static final String VALIDATOR = "bean.meta.validator";
    public static final String VALUE_DOMAIN = "bean.meta.valuedomain";

    public ConfigurablePropertyDescriptor(String propertyName, Class<?> beanClass, String readMethodName,
            String writeMethodName) throws IntrospectionException {
        super(propertyName, beanClass, readMethodName, writeMethodName);
    }

    public ConfigurablePropertyDescriptor(String propertyName, Class<?> beanClass) throws IntrospectionException {
        super(propertyName, beanClass);
    }

    public ConfigurablePropertyDescriptor(String propertyName, Method readMethod, Method writeMethod)
            throws IntrospectionException {
        super(propertyName, readMethod, writeMethod);
    }

    public ConfigurablePropertyDescriptor(String propertyName, Class<?> beanClass, boolean isRead,
            boolean isWrite) throws IntrospectionException {
        super(propertyName, beanClass, isRead ? "is" + capitalize(propertyName) : null, isWrite ? "set"
                + capitalize(propertyName) : null);
    }

    /**
     * Returns a String which capitalizes the first letter of the string.
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    /**
     * set the validator to use for this property.
     * 
     * @param validator
     *            the validator.
     * @return the current configurable property descriptor in order to chain
     *         multiple constraints.
     */
    public ConfigurablePropertyDescriptor<T> setValidator(Validator validator) {
        Validator current = (Validator) this.getValue(VALIDATOR);
        if (current != null) {
            throw new RuntimeException("A validator has been previously registered: " + current);
        }
        this.setValue(VALIDATOR, validator);
        return this;
    }

    /**
     * Set the domain of allowed values for this property.
     * 
     * @param validator
     *            the validator.
     * @return the current configurable property descriptor in order to chain
     *         multiple constraints.
     */
    public <S> ConfigurablePropertyDescriptor<T> setValueDomain(
            Collection<? extends DomainValueDescriptor<S>> domainValues) {
        @SuppressWarnings("unchecked")
        Collection<DomainValueDescriptor<S>> current = (Collection<DomainValueDescriptor<S>>) this
                .getValue(VALUE_DOMAIN);
        if (current != null) {
            throw new RuntimeException("A value domain has been previously registered: " + current);
        }
        this.setValue(VALUE_DOMAIN, domainValues);
        return this;
    }

    /**
     * Get the domain value descriptor assigned to this class
     * 
     * @return the domain value descriptor.
     */
    public <S> Collection<DomainValueDescriptor<S>> getValueDomain() {
        @SuppressWarnings("unchecked")
        Collection<DomainValueDescriptor<S>> current = (Collection<DomainValueDescriptor<S>>) this
                .getValue(VALUE_DOMAIN);
        return current;
    }
}
