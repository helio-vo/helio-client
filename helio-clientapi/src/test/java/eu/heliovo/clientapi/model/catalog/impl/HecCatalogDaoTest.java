package eu.heliovo.clientapi.model.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.registryclient.impl.GenericServiceDescriptor;
import eu.heliovo.shared.props.HelioFileUtil;

public class HecCatalogDaoTest {
    
    private HelioCatalogDao hecDao;
    private FieldTypeRegistry fieldTypeRegistry;

    @Before public void setup() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:eu/heliovo/clientapi/spring-test-clientapi.xml");
        ServiceRegistryClient serviceRegistry = context.getBean(ServiceRegistryClient.class);
        serviceRegistry.registerServiceInstance(
                new GenericServiceDescriptor(HelioServiceName.HEC, "HEC", ServiceCapability.SYNC_QUERY_SERVICE),
                new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE,ServiceCapability.SYNC_QUERY_SERVICE, 
                        HelioFileUtil.asURL("http://festung1.oats.inaf.it:8080/helio-hec/HelioService?wsdl")));
        hecDao =  (HelioCatalogDao) context.getBean("hecDao");
        fieldTypeRegistry = (FieldTypeRegistry)context.getBean("fieldTypeRegistry");        
    }
    

	/**
	 * Test the synthetic field that describes the catalogs.
	 */
	@Test
	public void testGetCatalogField() {

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
        for (DomainValueDescriptor<String> c : hecDao.getCatalogField().getValueDomain()) {
			for (HelioField<?> hf : hecDao.getFields(c.getValue())) {
				assertNotNull(hf);
			}
		}
	}
}
