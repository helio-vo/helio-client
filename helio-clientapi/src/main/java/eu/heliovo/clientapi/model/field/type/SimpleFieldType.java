package eu.heliovo.clientapi.model.field.type;

import java.util.Arrays;

import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Default Implementation of a simple field type.
 * @author marco soldati at fhnw ch
 *
 */
public class SimpleFieldType implements FieldType {
    /**
     * Name of the type
     */
	private String name;
	
	/**
	 * Associated java type 
	 */
	private Class<?> javaType;

	/**
	 * Associated operator domain
	 */
	private Operator[] operatorDomain;

    /**
     * Unified content descriptor
     */
    private String ucd;

    /**
     * The unit assigned with this field.
     */
    private String unit;

    /**
     * The utype
     */
    private String utype;

    /**
     * Default constructor creates an emtpy object.
     */
    public SimpleFieldType() {
    }
    
	/**
	 * Create the SimpleFieldType
	 * @param name the name of the type. Must not be null.
	 * @param javaType the java type. Must not be null.
	 * @param operatorDomain the operator domain. Must not be null.
	 */
	public SimpleFieldType(String name, Class<?> javaType, Operator[] operatorDomain) {
		setName(name);
		setJavaType(javaType);
		setOperatorDomain(operatorDomain);
	}
	
	/**
	 * The copy constructor
	 * @param simpleFieldType
	 */
    public SimpleFieldType(SimpleFieldType simpleFieldType) {
        setName(simpleFieldType.name);
        setJavaType(simpleFieldType.javaType);
        setOperatorDomain(simpleFieldType.operatorDomain);
        setUcd(simpleFieldType.ucd);
        setUnit(simpleFieldType.unit);
        setUtype(simpleFieldType.utype);
    }

    @Override
	public String getName() {
		return name;
	}
	
	   /**
     * @return the ucd
     */
    @Override
    public String getUcd() {
        return ucd;
    }

    /**
     * @param ucd the ucd to set
     */
    public void setUcd(String ucd) {
        this.ucd = ucd;
    }

    /**
     * @return the unit
     */
    @Override
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the utype
     */
    @Override
    public String getUtype() {
        return utype;
    }

    /**
     * @param utype the utype to set
     */
    public void setUtype(String utype) {
        this.utype = utype;
    }

    @Override
	public Class<?> getJavaType() {
		return javaType;
	}

    @Override
	public Operator[] getOperatorDomain() {
		return operatorDomain;
	}
	
    
	/**
     * @param name the name to set
     */
    public void setName(String name) {
        AssertUtil.assertArgumentHasText(name, "name");
        this.name = name;
    }

    /**
     * @param javaType the javaType to set
     */
    public void setJavaType(Class<?> javaType) {
        AssertUtil.assertArgumentNotNull(javaType, "javaType");
        this.javaType = javaType;
    }

    /**
     * @param operatorDomain the operatorDomain to set
     */
    public void setOperatorDomain(Operator[] operatorDomain) {
        AssertUtil.assertArgumentNotNull(operatorDomain, "operatorDomain");
        this.operatorDomain = operatorDomain;
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SimpleFieldType)) {
			return false;
		}
		
		return name.equals(((SimpleFieldType)obj).name);
	}
    
    @Override
    public SimpleFieldType clone() {
        SimpleFieldType clone = new SimpleFieldType(this);
        return clone;
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("fieldType {");
		sb.append("name=").append(name);
		sb.append(", ").append("javaType=").append(javaType.getName());
		sb.append(", ").append("operatorDomain=").append(Arrays.toString(operatorDomain));
		sb.append("}");
		return sb.toString();
	}
}