package eu.heliovo.clientapi.model.field;



/**
 * Type of a field.
 * @author marco soldati at fhnw ch
 *
 */
public interface FieldType extends Cloneable {
	/**
	 * Get the name of the field type
	 * @return the type name.
	 */
	public String getName();
	
	/**
	 * Get the java type
	 * @return the java type
	 */
	public Class<?> getJavaType();
	
	/**
	 * Get the operator domain 
	 * @return the operator domain
	 */
	public Operator[] getOperatorDomain();

	/**
	 * Get the UType if defined
	 * @return the utype
	 */
    public abstract String getUtype();

    /**
     * Get the unit if defined
     * @return the unit
     */
    public abstract String getUnit();

    /**
     * Get the ucd, if defined
     * @return
     */
    public abstract String getUcd();
    
    /**
     * Overwrite the ucd of the field type
     * @param ucd
     */
    public void setUcd(String ucd);

    /**
     * Overwrite the unit of the field type
     * @param unitString
     */
    public void setUnit(String unitString);

    /**
     * Overwrite the utype of the field type
     * @param utype
     */
    public void setUtype(String utype);
    
    /**
     * Create an extact clone of the field type.
     * @return a new instance
     */
    public FieldType clone();

}