package eu.heliovo.clientapi.model.field.type;

import java.util.Date;

import eu.heliovo.clientapi.model.field.Operator;



/**
 * Type of a field.
 * @author marco soldati at fhnw ch
 *
 */
public interface FieldType extends Cloneable {
    public static final FieldType STRING = new SimpleFieldType("string", java.lang.String.class, new Operator[] {Operator.EQUALS});
    public static final FieldType INTEGER = new SimpleFieldType("Integer", Integer.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    public static final FieldType LONG = new SimpleFieldType("Long", Long.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    public static final FieldType SHORT = new SimpleFieldType("Short", Short.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    public static final FieldType FLOAT = new SimpleFieldType("Float", Float.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    public static final FieldType DOUBLE = new SimpleFieldType("Double", Double.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    public static final FieldType BOOLEAN = new SimpleFieldType("Boolean ", Boolean.class, new Operator[] {Operator.EQUALS});
    public static final FieldType BYTE = new SimpleFieldType("Byte", Byte.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    public static final FieldType DATETIME = new SimpleFieldType("dateTime", Date.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN});
    
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
     * Create an exact clone of the field type.
     * @return a new instance
     */
    public FieldType clone();

}