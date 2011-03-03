package eu.heliovo.clientapi.model.field;

import java.util.Arrays;


/**
 * Description of a helio field.
 * @param <T> Java type of the field. This type has to match the provided type.
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class HelioField<T extends Object> {
	/**
	 * Unique id of the field.
	 * This id is required to distinguish between fields with the same name.  
	 */
	private final String id;
	
	/**
	 * Name of the field.
	 */
	private final String fieldName;
	
	/**
	 * General description of the field.
	 */
	private final String description;
	
	/**
	 * The type of the field as XSD type.
	 */
	private final FieldType type;
	
	/**
	 * Enumeration of allowed values or null if not applicable.
	 */
	private DomainValueDescriptor<T>[] valueDomain;

	/**
	 * The default value assigned to an object. May be null.
	 */
	private final T defaultValue;

	/**
	 * Create the HELIO field. The default value and the value domain will be null.
	 * @param id the id of the field. must not be null.
	 * @param fieldName the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 */
	public HelioField(String id, String fieldName, String description, FieldType type) {
		this(id, fieldName, description, type, null, null);	
	}
	
	/**
	 * Create the HELIO field with a given default value. The value domain will be null.
	 * @param id the id of the field. must not be null.
	 * @param fieldName the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 * @param defaultValue the default value. Will be ignored if null. 
	 */
	public HelioField(String id, String fieldName, String description, FieldType type, T defaultValue) {
		this(id, fieldName, description, type, null, defaultValue);
	}

	/**
	 * Create the HELIO field with a given value domain. The default value will be null.
	 * @param id the id of the field. must not be null.
	 * @param fieldName the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 * @param valueDomain the value domain. Will be ignored if null. 
	 */
	public HelioField(String id, String fieldName, String description, FieldType type, DomainValueDescriptor<T>[] valueDomain) {
		this(id, fieldName, description, type, valueDomain, null);
	}
	/**
	 * Create the helio field
	 * @param id the id of the field. must not be null.
	 * @param fieldName the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 * @param valueDomain the value domain. Will be ignored if null. 
	 * @param defaultValue the default value. Will be ignored if null.
	 */
	public HelioField(String id, String fieldName, String description, FieldType type, DomainValueDescriptor<T>[] valueDomain, T defaultValue) {
		this.id = id;
		this.fieldName = fieldName;
		this.description = description;
		this.type = type;
		this.valueDomain = valueDomain;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Get the id of the field. This id is usually not shown to the user 
	 * and is just used to distinguish fields with the same name but different meaning.
	 * @return the id of the field.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the name of the field. Case sensitive. Must not be null.
	 * @return the name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Get the description of the field. Any string is applicable. May be null.
	 * @return 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The data type as XML Schema type. Must not be null. 
	 * @return the type
	 */
	public FieldType getType() {
		return type;
	}

	/**
	 * The domain of allowed values. May be null.
	 * @return the value domain.
	 */
	public DomainValueDescriptor<T>[] getValueDomain() {
		return valueDomain;
	}

	/**
	 * The domain of allowed values. May be null.
	 * @param valueDomain the value domain.
	 */
	public void setValueDomain(DomainValueDescriptor<T>[] valueDomain) {
		this.valueDomain = valueDomain;
	}
	
	/**
	 * The default value of a field.
	 * @return the default value.
	 */
	public T getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HelioField)) {
			return false;
		}
		return this.id.equals(((HelioField<?>)obj).id);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HelioField");
		sb.append("name=").append(fieldName);
		sb.append(", type=").append(type);
		if (valueDomain != null) 
			sb.append(", domain=").append(Arrays.toString(valueDomain));
		if (description != null)
			sb.append(", description=").append(description);
		sb.append("}");
		return sb.toString();
	}
}
