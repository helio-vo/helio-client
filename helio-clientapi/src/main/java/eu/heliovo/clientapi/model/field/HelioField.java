package eu.heliovo.clientapi.model.field;

import java.util.Arrays;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Description of a helio field.
 * @param <T> Java type of the field. This type has to match the provided type.
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class HelioField<T extends Object> {
	/**
	 * name of the stored VOTable.
	 */
	public static final String FIELD_SAVE_TO = "saveto";

	/**
	 * start index of a query.
	 */
	public static final String FIELD_STARTINDEX = "startindex";

	/**
	 * the maximum of records to display
	 */
	public static final String FIELD_MAX_RECORDS = "maxrecords";

	/**
	 * The start time field name.
	 */
	public static final String FIELD_STARTTIME = "starttime";

	/**
	 * The end time field name.
	 */
	public static final String FIELD_ENDTIME = "endtime";
	
	/**
	 * Unique id of the field.
	 * This id is required to distinguish between fields with the same name.  
	 */
	private final String id;
	
	/**
	 * Name of the field.
	 */
	private final String name;
	
	/**
	 * Label to show to the user.
	 */
	private final String label;
	
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
	private T defaultValue;

	/**
	 * Create the HELIO field. The default value and the value domain will be null.
	 * @param id the id of the field. must not be null.
	 * @param name the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 */
	public HelioField(String id, String name, String description, FieldType type) {
		this(id, name, name, description, type, null, null);	
	}
	
	/**
	 * Create the HELIO field with a given default value. The value domain will be null.
	 * @param id the id of the field. must not be null.
	 * @param name the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 * @param defaultValue the default value. Will be ignored if null. 
	 */
	public HelioField(String id, String name, String description, FieldType type, T defaultValue) {
		this(id, name, name, description, type, null, defaultValue);
	}

	/**
	 * Create the HELIO field with a given value domain. The default value will be null.
	 * @param id the id of the field. must not be null.
	 * @param name the name of the field. must not be null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 * @param valueDomain the value domain. Will be ignored if null. 
	 */
	public HelioField(String id, String name, String description, FieldType type, DomainValueDescriptor<T>[] valueDomain) {
		this(id, name, name, description, type, valueDomain, null);
	}
	/**
	 * Create the helio field
	 * @param id the id of the field. must not be null.
	 * @param name the name of the field. must not be null.
	 * @param label label to be shown to the user. Same as name if null.
	 * @param description the description of the field in user friendly format. May be null.
	 * @param type the type of the field in XML Schema definition. Must not be null
	 * @param valueDomain the value domain. Will be ignored if null. 
	 * @param defaultValue the default value. Will be ignored if null.
	 */
	public HelioField(String id, String name, String label, String description, FieldType type, DomainValueDescriptor<T>[] valueDomain, T defaultValue) {
		AssertUtil.assertArgumentHasText(id, "id");
		AssertUtil.assertArgumentHasText(name, "name");
		AssertUtil.assertArgumentNotNull(type, "type");
		this.id = id;
		this.name = name;
		this.label = label == null ? name : label;
		this.description = description;
		this.type = type;
		checkDomainConsistency(defaultValue, valueDomain);
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
	public String getName() {
		return name;
	}

	/**
	 * Get the label of this field.
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Get the description of the field. Any string is applicable. May be null.
	 * @return the description 
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
		checkDomainConsistency(defaultValue, valueDomain);
		this.valueDomain = valueDomain;
	}
	
	/**
	 * The default value of a field.
	 * @return the default value.
	 */
	public T getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Set the default value of the field.
	 * @param defaultValue the default value.
	 */
	public void setDefaultValue(T defaultValue) {
		checkDomainConsistency(defaultValue, valueDomain);
		this.defaultValue = defaultValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HelioField)) {
			return false;
		}
		boolean ret = this.id.equals(((HelioField<?>)obj).id);
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HelioField");
		sb.append(" id=").append(id);
		sb.append(" name=").append(name);
		sb.append(", label=").append(label);
		sb.append(", type=").append(type);
		if (valueDomain != null) 
			sb.append(", domain=").append(Arrays.toString(valueDomain));
		if (description != null)
			sb.append(", description=").append(description);
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return 1 + 37 * id.hashCode();
	}
	
	/**
	 * Check if a value is part of the given domain. If either domain or value are null the check will succeed.
	 * @param <T> Type of the domain value.
	 * @param value the value to check
	 * @param domain the domain of values to check.
	 */
	private static <T> void checkDomainConsistency(T value, DomainValueDescriptor<T>[] domain) {
		//nothing to do if not both valueDomain and defaultValue are set.
		if (domain == null || value == null) {
			return;
		}
		
		for (DomainValueDescriptor<T> val : domain) {
			if (value != null && val.getValue().equals(value)) {
				return;
			}
		}
		throw new IllegalArgumentException("Value '" + value + "' is not part of domain '" + Arrays.toString(domain) + "'.");
	}
}
