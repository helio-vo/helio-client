package eu.heliovo.clientapi.model.field;

import java.util.Arrays;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Type of a field.
 * @author marco soldati at fhnw ch
 *
 */
public interface FieldType {
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
	 * Get the Xsd type
	 * @return the xsd type
	 */
	public String getXsdType();
	
	
	public Operator[] getOperatorDomain();
}

/**
 * Default Implementation of a simple field type.
 * @author marco soldati at fhnw ch
 *
 */
class SimpleFieldType implements FieldType {

	private final String name;
	private final Class<?> javaType;
	private final String xsdType;
	private final Operator[] operatorDomain;

	/**
	 * Create the SimpleFieldType
	 * @param name the name of the type. Must not be null.
	 * @param javaType the java type. Must not be null.
	 * @param xsdType the xsdType. Must not be null.
	 * @param operatorDomain the operator domain. Must not be null.
	 */
	public SimpleFieldType(String name, Class<?> javaType, String xsdType, Operator[] operatorDomain) {
		AssertUtil.assertArgumentHasText(name, "name");
		AssertUtil.assertArgumentNotNull(javaType, "javaType");
		AssertUtil.assertArgumentHasText(xsdType, "xsdType");
		AssertUtil.assertArgumentNotNull(operatorDomain, "operatorDomain");
		this.name = name;
		this.javaType = javaType;
		this.xsdType = xsdType;
		this.operatorDomain = operatorDomain;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getJavaType() {
		return javaType;
	}

	@Override
	public String getXsdType() {
		return xsdType;
	}

	@Override
	public Operator[] getOperatorDomain() {
		return operatorDomain;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SimpleFieldType)) 
			return false;
		
		return name.equals(((SimpleFieldType)obj).name);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("fieldType {");
		sb.append("name=").append(name);
		sb.append(", ").append("javaType=").append(javaType.getName());
		sb.append(", ").append("xsdType=").append(xsdType);
		sb.append(", ").append("operatorDomain=").append(Arrays.toString(operatorDomain));
		sb.append("}");
		return sb.toString();
	}
}
