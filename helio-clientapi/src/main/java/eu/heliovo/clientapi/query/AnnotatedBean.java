package eu.heliovo.clientapi.query;

/**
 * Marker interface for a bean that it contains a BeanInfo description.
 * Note: this has nothing to do with Java Annotations.
 * @author MarcoSoldati
 *
 */
public interface AnnotatedBean {

    /**
     * Get the bean info for this specific bean instance.
     * This object can be used to read the configuration of available properties in this object. 
     * @return the bean info object.
     */
    public java.beans.BeanInfo getBeanInfo();

}