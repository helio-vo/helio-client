package eu.heliovo.clientapi.model.field;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class FieldTypeRegistryTest {

	@Test public void testFieldTypeRegistry() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        FieldTypeRegistry registry = (FieldTypeRegistry)context.getBean("fieldTypeRegistry");
		
		assertNotNull(registry.getType("string"));
		assertNull(registry.getType("notexisting"));
		assertNull(registry.getType(null));
		
		
		FieldType type = registry.getType("string");
		assertNotNull(type);
		assertEquals(String.class, type.getJavaType());
		assertEquals("string", type.getName());
		assertEquals("xsd:string", type.getXsdType());
		assertArrayEquals(new Operator[] {Operator.EQUALS}, type.getOperatorDomain());

	}
}
