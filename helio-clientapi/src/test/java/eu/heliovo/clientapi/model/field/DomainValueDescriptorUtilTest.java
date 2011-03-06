package eu.heliovo.clientapi.model.field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.heliovo.clientapi.model.field.DomainValueDescriptorUtil.GenericDomainValueDescriptor;

/**
 * Unit Test for the {@link DomainValueDescriptorUtil}
 * @author marco soldati at fhnw ch
 *
 */
public class DomainValueDescriptorUtilTest {

	/**
	 * Test method {@link DomainValueDescriptorUtil#asDomainValue(Object)}
	 */
	@Test public void testAsDomainValue() {
		{
			DomainValueDescriptor<String> value = DomainValueDescriptorUtil.asDomainValue("string");
			assertNotNull(value);
			assertEquals("string", value.getValue());
			assertEquals("string", value.getLabel());
			assertEquals("string", value.toString());
			assertNull(value.getDescription());
		}
		{
			DomainValueDescriptor<Integer> value = DomainValueDescriptorUtil.asDomainValue(5);
			assertNotNull(value);
			assertEquals(new Integer(5), value.getValue());
			assertEquals("5", value.getLabel());
			assertEquals("5", value.toString());
			assertNull(value.getDescription());
		}
		
	}
	
	/**
	 * Test equality of the {@link GenericDomainValueDescriptor}.
	 */
	@Test public void testEqualsAndHashCode() {
		DomainValueDescriptor<Integer> value1 = DomainValueDescriptorUtil.asDomainValue(5);
		DomainValueDescriptor<Integer> value2 = DomainValueDescriptorUtil.asDomainValue(5);
		
		assertTrue(value1.equals(value2));
		assertEquals(value1.hashCode(), value2.hashCode());
	}
	
	/**
	 * Test method {@link DomainValueDescriptorUtil#asValueDomain(Object...)}
	 */
	@Test public void testAsValueDomain() {
		DomainValueDescriptor<String>[] valueDomain = DomainValueDescriptorUtil.asValueDomain("string1", "string2");
		assertNotNull(valueDomain);
		assertEquals(2, valueDomain.length);
		assertEquals("string1", valueDomain[0].getValue());
		assertEquals("string2", valueDomain[1].getValue());
	}
}
