package eu.heliovo.clientapi.model.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.heliovo.clientapi.model.field.FieldType;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.field.Operator;

public class HelioCatalogTest {

	@Test public void testAddField() {
		HelioCatalog catalog = new HelioCatalog("catalogName", "catalogLabel", "catalogDescription");
		
		assertEquals("catalogName", catalog.getCatalogName());
		assertEquals("catalogName", catalog.getValue());
		assertEquals("catalogLabel", catalog.getLabel());
		assertEquals("catalogDescription", catalog.getDescription());
		
		assertNotNull(catalog.getFields());
		
		assertEquals(0, catalog.getFields().length);
		
		FieldType stringType = new FieldType() {
			@Override
			public String getXsdType() {
				return "string";
			}
			@Override
			public Operator[] getOperatorDomain() {
				return null;
			}
			@Override
			public String getName() {
				return "string";
			}
			@Override
			public Class<?> getJavaType() {
				return String.class;
			}
		};
		
		assertTrue(catalog.addField(new HelioField<Object>("id", "fieldName", "description", stringType)));
		assertEquals(1, catalog.getFields().length);
		
		assertTrue(catalog.addField(new HelioField<Object>("id2", "fieldName", "description", stringType)));
		assertEquals(2, catalog.getFields().length);
		
		assertFalse(catalog.addField(new HelioField<Object>("id", "fieldName2", "description", stringType)));
		assertEquals(2, catalog.getFields().length);
	}
}
