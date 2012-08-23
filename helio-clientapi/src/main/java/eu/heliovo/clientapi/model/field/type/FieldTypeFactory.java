package eu.heliovo.clientapi.model.field.type;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.heliovo.clientapi.model.field.Operator;

public class FieldTypeFactory {
	/**
	 * The actual registry
	 */
	private final Map<String, FieldType> fieldMap = new LinkedHashMap<String, FieldType>();

	/**
	 * default constructor
	 */
	public FieldTypeFactory() { 
	    
	}
	
	/**
	 * Init the registry with default types.
	 */
	public void init() {
		add(new SimpleFieldType("string", java.lang.String.class, new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("biginteger", java.math.BigInteger.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("int", int.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Integer", Integer.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("long", long.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Long", Long.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("short", short.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Short", Short.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("decimal", java.math.BigDecimal.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("float", float.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Float", Float.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("double", double.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Double", Double.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("boolean ", boolean.class, new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("Boolean ", Boolean.class, new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("byte", byte.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Byte", Byte.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("dateTime", Date.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("base64Binary", byte[].class, new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("unsignedInt", long.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("unsignedShort", int.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("unsignedByte", short.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("time", Date.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("date", Date.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("object", java.lang.Object.class, new Operator[] {Operator.EQUALS}));
    
		add(new SimpleFieldType("xclass", java.lang.String.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
        add(new SimpleFieldType("oclass", java.lang.String.class, new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
        
        add(new SimpleFieldType("unknown", java.lang.String.class, new Operator[] {}));
	}

	/**
	 * Add a field type to the type map
	 * @param fieldType the field type to add. 
	 */
	private void add(FieldType fieldType) {
		FieldType oldFieldType = fieldMap.put(fieldType.getName(), fieldType);
		if (oldFieldType != null) {
			throw new RuntimeException("Attempt to register two fieldTypes with the same name: old type: " + oldFieldType + ", new type: " + fieldType);
		};
	}
	
	/**
	 * Lookup a field type for a given name.
	 * @param name the name. Must not be null.
	 * @return the found type or null if not applicable.
	 */
	public FieldType getTypeByName(String name) {
		return fieldMap.get(name == null ? null : name);
	}
	
	/**
	 * Get a clone of a field type by the first type that implements a given java class.
	 * The field type can be further customized by the caller (i.e. setting units/ucds/...) 
	 * @param javaClass the java class to look for.
	 * @return null if the class cannot be found.
	 */
	public FieldType getNewTypeByJavaClass(Class<?> javaClass) {
	    for (FieldType fieldType : fieldMap.values()) {
            if (fieldType.getJavaType().isAssignableFrom(javaClass)) {
                return fieldType.clone();
            }
        }
	    return null;
	}
}



