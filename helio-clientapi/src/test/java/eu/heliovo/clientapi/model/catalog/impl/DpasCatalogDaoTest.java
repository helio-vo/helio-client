package eu.heliovo.clientapi.model.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;

public class DpasCatalogDaoTest {

	/**
	 * The field type registry to use
	 */
	private FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();

	/**
	 * Test the synthetic field that describes the catalogs.
	 */
	@Test
	public void testGetCatalogField() {
		HelioCatalogDao dpasDao = HelioCatalogDaoFactory.getInstance().getHelioCatalogDao("dpas");;
		HelioField<String> catalogField = dpasDao.getCatalogField();

		assertEquals("dpas_catalog", catalogField.getId());
		assertEquals("catalog", catalogField.getName());
		assertEquals("catalog", catalogField.getLabel());
		assertNotNull(catalogField.getDescription());
		assertEquals(null, catalogField.getDefaultValue());
		assertEquals(fieldTypeRegistry.getType("string"), catalogField.getType());
		assertTrue(1 == catalogField.getValueDomain().length);
	}

	@Test public void test() {
	    HelioCatalogDao dpasDao = HelioCatalogDaoFactory.getInstance().getHelioCatalogDao("dpas");;
		for (DomainValueDescriptor<String> c : dpasDao.getCatalogField().getValueDomain()) {
			for (HelioField<?> hf : dpasDao.getFields(c.getValue())) {
				assertNotNull(hf);
				if (hf.getValueDomain() != null) {
				System.out.println(Arrays.toString(hf.getValueDomain()));
				}
			}
		}
	}
}
