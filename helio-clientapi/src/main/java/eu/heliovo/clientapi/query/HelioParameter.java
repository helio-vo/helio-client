package eu.heliovo.clientapi.query;

import java.util.Arrays;

/**
 * Description of a helio parameter.
 * TODO: use JAXB to do the data conversion.
 * @param <T> Java type of the parameter. This type has to match the provided xsdType.
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class HelioParameter<T extends Object> {
	/**
	 * Name of the parameter.
	 */
	private final String paramName;
	
	/**
	 * General description of the parameter.
	 */
	private final String description;
	
	/**
	 * The type of the param as XSD type.
	 */
	private final String xsdType;
	
	/**
	 * Enumeration of allowed values or null if not applicable.
	 */
	private T[] valueDomain;

	/**
	 * The default value assigned to an object. May be null.
	 */
	private final T defaultValue;

	/**
	 * Create the HELIO parameter. The default value and the value domain will be null.
	 * @param paramName the name of the param. must not be null.
	 * @param description the description of the parameter in user friendly format. May be null.
	 * @param xsdType the type of the param in XML Schema definition. Must not be null
	 */
	public HelioParameter(String paramName, String description, String xsdType) {
		this(paramName, description, xsdType, null, null);	
	}
	
	/**
	 * Create the HELIO parameter with a given default value. The value domain will be null.
	 * @param paramName the name of the param. must not be null.
	 * @param description the description of the parameter in user friendly format. May be null.
	 * @param xsdType the type of the param in XML Schema definition. Must not be null
	 * @param defaultValue the default value. Will be ignored if null. 
	 */
	public HelioParameter(String paramName, String description, String xsdType, T defaultValue) {
		this(paramName, description, xsdType, null, defaultValue);
	}

	/**
	 * Create the HELIO parameter with a given value domain. The default value will be null.
	 * @param paramName the name of the param. must not be null.
	 * @param description the description of the parameter in user friendly format. May be null.
	 * @param xsdType the type of the param in XML Schema definition. Must not be null
	 * @param valueDomain the value domain. Will be ignored if null. 
	 */
	public HelioParameter(String paramName, String description, String xsdType, T[] valueDomain) {
		this(paramName, description, xsdType, valueDomain, null);
	}
	/**
	 * Create the helio parameter
	 * @param paramName the name of the param. must not be null.
	 * @param description the description of the parameter in user friendly format. May be null.
	 * @param xsdType the type of the param in XML Schema definition. Must not be null
	 * @param valueDomain the value domain. Will be ignored if null. 
	 * @param defaultValue the default value. Will be ignored if null.
	 */
	public HelioParameter(String paramName, String description, String xsdType, T[] valueDomain, T defaultValue) {
		this.paramName = paramName;
		this.description = description;
		this.xsdType = xsdType;
		this.valueDomain = valueDomain;
		this.defaultValue = defaultValue;
	}

	/**
	 * Get the name of the parameter. Case sensitive. Must not be null.
	 * @return the name
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * Get the description of the parameter. Any string is applicable. May be null.
	 * @return 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The data type as XML Schema type. Must not be null. 
	 * @return the type
	 */
	public String getXsdType() {
		return xsdType;
	}

	/**
	 * The domain of allowed values. May be null.
	 * @return the value domain.
	 */
	public Object[] getValueDomain() {
		return valueDomain;
	}

	/**
	 * The domain of allowed values. May be null.
	 * @param valueDomain the value domain.
	 */
	public void setValueDomain(T[] valueDomain) {
		this.valueDomain = valueDomain;
	}
	
	/**
	 * The default value of a parameter.
	 * @return the default value.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(paramName).append("::").append(xsdType);
		if (valueDomain != null) 
			sb.append(" [domain:").append(Arrays.toString(valueDomain)).append("]");
		if (description != null)
			sb.append(" - ").append(description);		
		return sb.toString();
	}
}
