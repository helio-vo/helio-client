package eu.heliovo.clientapi.model.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.heliovo.clientapi.model.catalog.CatalogRegistry;
import eu.heliovo.clientapi.model.catalog.impl.HecStaticCatalogRegistry;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;

public class HecStaticCatalogRegistryTest {

	/**
	 * The field type registry to use
	 */
	private FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();

	/**
	 * Test the syntetic field that describes the catalogs.
	 */
	@Test
	public void testGetCatalogField() {
		CatalogRegistry registry = HecStaticCatalogRegistry.getInstance();
		HelioField<String> catalogField = registry.getCatalogField();

		assertEquals("hec_catalog", catalogField.getId());
		assertEquals("catalog", catalogField.getName());
		assertEquals("catalog", catalogField.getLabel());
		assertNotNull(catalogField.getDescription());
		assertEquals("goes_xray_flare", catalogField.getDefaultValue());
		assertEquals(fieldTypeRegistry.getType("string"), catalogField.getType());
		assertTrue(2 < catalogField.getValueDomain().length);
	}

	@Test public void test() {
		CatalogRegistry registry = HecStaticCatalogRegistry.getInstance();
		for (DomainValueDescriptor<String> c : registry.getCatalogField().getValueDomain()) {
			for (HelioField<?> hf : registry.getFields(c.getValue())) {
				assertNotNull(hf);
			}
		}
	}
}
