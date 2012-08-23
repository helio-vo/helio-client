package eu.heliovo.clientapi.model.field.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;

/**
 * 
 * @author marco soldati at fhnw ch
 *
 */
public class FieldTypeFactoryTest {

	@Test public void testFieldTypeFactory() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        FieldTypeFactory registry = (FieldTypeFactory)context.getBean("fieldTypeFactory");
		
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
