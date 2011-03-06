package eu.heliovo.clientapi.model.field;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class FieldTypeRegistryTest {

	@Test public void testFieldTypeRegistry() {
		FieldTypeRegistry registry = FieldTypeRegistry.getInstance();
		
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
