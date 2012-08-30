package eu.heliovo.clientapi.model.field.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import eu.heliovo.clientapi.model.field.Operator;

/**
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class FieldTypeFactoryTest {

	@Test public void testFieldTypeFactory() {
        FieldTypeFactory registry = new FieldTypeFactory(); 
        registry.init();
		
		assertNotNull(registry.getTypeByName("string"));
		assertNull(registry.getTypeByName("notexisting"));
		assertNull(registry.getTypeByName(null));
		
		
		FieldType type = registry.getTypeByName("string");
		assertNotNull(type);
		assertEquals(String.class, type.getJavaType());
		assertEquals("string", type.getName());
		assertArrayEquals(new Operator[] {Operator.EQUALS}, type.getOperatorDomain());

	}
}
