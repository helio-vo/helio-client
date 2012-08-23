package eu.heliovo.clientapi.model.field.descriptor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.DomainValueDescriptor;
import eu.heliovo.clientapi.model.DomainValueDescriptorUtil;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;

/**
 * Unit test class for the helio field
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class HelioFieldDescriptorTest {

	@Test
	public void testCreation() {
		String id = "id";
		String fieldName = "fieldName";
		String description = "description";
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        FieldTypeFactory fieldTypeFactory = (FieldTypeFactory)context.getBean("fieldTypeFactory");
		FieldType type = fieldTypeFactory.getTypeByName("string");
		DomainValueDescriptor<String>[] valueDomain = DomainValueDescriptorUtil.asValueDomain("string1", "string2",
				"string3");
		String defaultValue = "string1";

		assertNotNull(type);

		{
			HelioFieldDescriptor<String> helioField1 = new HelioFieldDescriptor<String>(id, fieldName, description, type);
			assertNotNull(helioField1.getId());
			assertEquals(id, helioField1.getId());
			assertNotNull(helioField1.getName());
			assertEquals(fieldName, helioField1.getName());
			assertNotNull(helioField1.getLabel());
			assertEquals(fieldName, helioField1.getLabel());
			assertNotNull(helioField1.getDescription());
			assertEquals(description, helioField1.getDescription());
			assertNotNull(helioField1.getType());
			assertEquals(type, helioField1.getType());
			assertNull(helioField1.getValueDomain());
			assertNull(helioField1.getDefaultValue());
			assertNotNull(helioField1.toString());
		}

		{
			HelioFieldDescriptor<String> helioField2 = new HelioFieldDescriptor<String>(id, fieldName, description, type, valueDomain);
			assertNotNull(helioField2.getId());
			assertNotNull(helioField2.getName());
			assertNotNull(helioField2.getDescription());
			assertNotNull(helioField2.getType());
			assertNotNull(helioField2.getValueDomain());
			assertArrayEquals(valueDomain, helioField2.getValueDomain());
			assertNull(helioField2.getDefaultValue());
			assertNotNull(helioField2.toString());
		}
		{
			HelioFieldDescriptor<String> helioField3 = new HelioFieldDescriptor<String>(id, fieldName, description, type, defaultValue);
			assertNotNull(helioField3.getId());
			assertNotNull(helioField3.getName());
			assertNotNull(helioField3.getDescription());
			assertNotNull(helioField3.getType());
			assertNull(helioField3.getValueDomain());
			assertNotNull(helioField3.getDefaultValue());
			assertEquals(defaultValue, helioField3.getDefaultValue());
			assertNotNull(helioField3.toString());
		}
		{
			HelioFieldDescriptor<String> helioField4 = new HelioFieldDescriptor<String>(id, fieldName, description, type,
					valueDomain, defaultValue);
			assertNotNull(helioField4.getId());
			assertNotNull(helioField4.getName());
			assertNotNull(helioField4.getLabel());
			assertNotNull(helioField4.getDescription());
			assertNotNull(helioField4.getType());
			assertNotNull(helioField4.getValueDomain());
			assertNotNull(helioField4.getDefaultValue());
			assertNotNull(helioField4.toString());
		}
	}

	/**
	 * Test if the default value is consistent with the value domain.
	 */
	@Test
	public void testDomainConsitency() {
		String id = "id";
		String fieldName = "fieldName";
		String description = "description";
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        FieldTypeFactory fieldTypeFactory = (FieldTypeFactory)context.getBean("fieldTypeFactory");
		FieldType type = fieldTypeFactory.getTypeByName("string");
		
		DomainValueDescriptor<String>[] valueDomain = DomainValueDescriptorUtil.asValueDomain("string1", "string2",
				"string3");
		String defaultValue = "string1";

		HelioFieldDescriptor<String> helioField = new HelioFieldDescriptor<String>(id, fieldName, description, type,
				valueDomain, defaultValue);
		try {
			helioField.setDefaultValue("string.not.existing");
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// all right
		}
		assertEquals(defaultValue, helioField.getDefaultValue());

		try {
			DomainValueDescriptor<String>[] valueDomain2 = DomainValueDescriptorUtil.asValueDomain("not1", "not2",
					"not3");
			helioField.setValueDomain(valueDomain2);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// all right
		}
		assertArrayEquals(valueDomain, helioField.getValueDomain());

		DomainValueDescriptor<String>[] valueDomain3 = DomainValueDescriptorUtil.asValueDomain("string1", "string3",
				"string5");
		helioField.setValueDomain(valueDomain3);
		assertArrayEquals(valueDomain3, helioField.getValueDomain());

		helioField.setDefaultValue("string3");
		assertEquals("string3", helioField.getDefaultValue());

	}

	/**
	 * Test methods equal and hash code.
	 */
	@Test
	public void testEqualsAndHashcode() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        FieldTypeFactory fieldTypeFactory = (FieldTypeFactory)context.getBean("fieldTypeFactory");

		FieldType type = fieldTypeFactory.getTypeByName("string");
		HelioFieldDescriptor<String> helioField1 = new HelioFieldDescriptor<String>("id1", "field1", null, type);
		HelioFieldDescriptor<String> helioField2 = new HelioFieldDescriptor<String>("id1", "field2", null, type);
		HelioFieldDescriptor<String> helioField3 = new HelioFieldDescriptor<String>("id3", "field3", null, type);

		assertTrue(helioField1.equals(helioField2));
		assertFalse(helioField1.equals(helioField3));
		assertEquals(helioField1.hashCode(), helioField2.hashCode());
		assertTrue(helioField1.hashCode() != helioField3.hashCode());
		assertFalse(helioField1.equals(null));
		assertFalse(helioField1.equals("no field"));
	}
}
