package eu.heliovo.clientapi.model.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.registry.impl.HelioDummyServiceRegistryClient;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

public class DpasCatalogDaoTest {

    /**
     * Setup dummy registry client
     */
    @Before public void setup() {
        HelioDummyServiceRegistryClient serviceRegistryClient = new HelioDummyServiceRegistryClient();
        ServiceRegistryClientFactory.getInstance().setServiceRegistryClient(serviceRegistryClient);
    }

	/**
	 * Test the synthetic field that describes the catalogs.
	 */
	@Test
	public void testGetCatalogField() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        HelioCatalogDao dpasDao =  (HelioCatalogDao) context.getBean("dpasDao");
        FieldTypeRegistry fieldTypeRegistry = (FieldTypeRegistry)context.getBean("fieldTypeRegistry");

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
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        HelioCatalogDao dpasDao =  (HelioCatalogDao) context.getBean("dpasDao");
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
