package eu.heliovo.clientapi.model.field;

import java.util.HashMap;
import java.util.Map;

public class FieldTypeRegistry {
	
	/**
	 * singleton instance of the registry.
	 */
	private static FieldTypeRegistry instance;
	
	/**
	 * Get the singleton instance of this registry.
	 * @return the singleton instance.
	 */
	public static synchronized FieldTypeRegistry getInstance() {
		if (instance == null) {
			instance = new FieldTypeRegistry();
		}
		return instance;
	}
	
	/**
	 * The actual registry
	 */
	private final Map<String, FieldType> fieldMap = new HashMap<String, FieldType>();

	/**
	 * Init the registry with default types.
	 */
	private FieldTypeRegistry() {
		add(new SimpleFieldType("string", java.lang.String.class, "xsd:string" , new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("integer", java.math.BigInteger.class, "xsd:integer" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("int", int.class, "xsd:int" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("long", long.class, "xsd.long" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("short", short.class, "xsd:short" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("decimal", java.math.BigDecimal.class, "xsd:decimal" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("float", float.class, "xsd:float" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("double", double.class, "xsd:double" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("boolean ", boolean.class, "xsd:boolean" , new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("byte", byte.class, "xsd:byte" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("Qname", javax.xml.namespace.QName.class, "xsd:QName" , new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("dateTime", javax.xml.datatype.XMLGregorianCalendar.class, "xsd:dateTime" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("base64Binary", byte[].class, "xsd:base64Binary" , new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("hexBinary", byte[].class, "xsd:hexBinary" , new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("unsignedInt", long.class, "xsd:unsignedInt" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("unsignedShort", int.class, "xsd:unsignedShort" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("unsignedByte", short.class, "xsd:unsignedByte" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("time", javax.xml.datatype.XMLGregorianCalendar.class, "xsd:time" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("date", javax.xml.datatype.XMLGregorianCalendar.class, "xsd:date" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("g", javax.xml.datatype.XMLGregorianCalendar.class, "xsd:g" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("object", java.lang.Object.class, "xsd:anySimpleType" , new Operator[] {Operator.EQUALS}));
		add(new SimpleFieldType("duration", javax.xml.datatype.Duration.class, "xsd:duration" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
		add(new SimpleFieldType("NOTATION", javax.xml.namespace.QName.class, "xsd:NOTATION" , new Operator[] {Operator.EQUALS}));
    
		add(new SimpleFieldType("xclass", java.lang.String.class, "xsd:string" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
    add(new SimpleFieldType("oclass", java.lang.String.class, "xsd:string" , new Operator[] {Operator.EQUALS, Operator.LESS_EQUAL_THAN, Operator.LARGER_EQUAL_THAN}));
    
    add(new SimpleFieldType("unknown", java.lang.String.class, "xsd:string" ,new Operator[] {}));
	}

	/**
	 * Add a field type to the type map
	 * @param fieldType the field type to add. 
	 */
	private void add(FieldType fieldType) {
		FieldType oldFieldType = fieldMap.put(fieldType.getName().toLowerCase(), fieldType);
		if (oldFieldType != null) {
			throw new RuntimeException("Attempt to register two fieldTypes with the same name: old type: " + oldFieldType + ", new type: " + fieldType);
		};
	}
	
	/**
	 * Lookup a field type for a given name.
	 * @param name the name. Must not be null.
	 * @return the found type or null if not applicable.
	 */
	public FieldType getType(String name) {
		return fieldMap.get(name == null ? null : name.toLowerCase());
	}
}
