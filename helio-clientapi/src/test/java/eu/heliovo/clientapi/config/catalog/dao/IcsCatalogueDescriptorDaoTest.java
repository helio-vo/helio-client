package eu.heliovo.clientapi.config.catalog.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.model.catalog.descriptor.IcsCatalogueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;

public class IcsCatalogueDescriptorDaoTest {

	IcsCatalogueDescriptorDao catalogueDescriptorDao;
	
	@Before
	public void setup() {
        catalogueDescriptorDao = new IcsCatalogueDescriptorDao();
        HelioFileUtil helioFileUtil = new HelioFileUtil("test");
        STILUtils stilUtils = new STILUtils();
        stilUtils.setHelioFileUtil(helioFileUtil);
        FieldTypeFactory fieldTypeFactory = new FieldTypeFactory();
        fieldTypeFactory.init();
        
        catalogueDescriptorDao.setHelioFileUtil(helioFileUtil);
        catalogueDescriptorDao.setStilUtils(stilUtils);
        catalogueDescriptorDao.setFieldTypeFactory(fieldTypeFactory);
        
        catalogueDescriptorDao.init();
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void test_init() {
		IcsCatalogueDescriptor instrumentsList = findDescriptor("instrument");
		assertNotNull(instrumentsList);
		
		List<HelioFieldDescriptor<?>> fieldDescriptors = instrumentsList.getFieldDescriptors();
		assertEquals(17, fieldDescriptors.size());
		assertEquals("name", fieldDescriptors.get(0).getId());
	}

	private IcsCatalogueDescriptor findDescriptor(String listName) {
		List<IcsCatalogueDescriptor> lists = catalogueDescriptorDao.getDomainValues();
		assertEquals(2, lists.size());
		for (IcsCatalogueDescriptor icsCatalogueDescriptor : lists) {
			if (listName.equals(icsCatalogueDescriptor.getValue())) {
				return icsCatalogueDescriptor;
			}
		}
		return null;
	}
}
