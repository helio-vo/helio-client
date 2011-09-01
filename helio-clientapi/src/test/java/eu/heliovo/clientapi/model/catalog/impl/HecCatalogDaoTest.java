package eu.heliovo.clientapi.model.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.registryclient.HelioServiceName;

public class HecCatalogDaoTest {

	/**
	 * The field type registry to use
	 */
	private FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();

	/**
	 * Test the syntetic field that describes the catalogs.
	 */
	@Test
	public void testGetCatalogField() {
	    HelioCatalogDao hecDao = HelioCatalogDaoFactory.getInstance().getHelioCatalogDao(HelioServiceName.HEC);
		HelioField<String> catalogField = hecDao.getCatalogField();

		assertEquals("hec_catalog", catalogField.getId());
		assertEquals("catalog", catalogField.getName());
		assertEquals("catalog", catalogField.getLabel());
		assertNotNull(catalogField.getDescription());
		assertEquals("goes_sxr_flare", catalogField.getDefaultValue());
		assertEquals(fieldTypeRegistry.getType("string"), catalogField.getType());
		assertTrue(2 < catalogField.getValueDomain().length);
	}

	@Test public void test() {
	    HelioCatalogDao hecDao = HelioCatalogDaoFactory.getInstance().getHelioCatalogDao(HelioServiceName.HEC);
		for (DomainValueDescriptor<String> c : hecDao.getCatalogField().getValueDomain()) {
			for (HelioField<?> hf : hecDao.getFields(c.getValue())) {
				assertNotNull(hf);
			}
		}
	}
}
