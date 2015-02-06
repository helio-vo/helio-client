package eu.heliovo.clientapi.config.catalog.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.model.catalog.descriptor.IlsCatalogueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;

public class IlsCatalogueDescriptorDaoTest {

	IlsCatalogueDescriptorDao catalogueDescriptorDao;
	
	@Before
	public void setup() {
        catalogueDescriptorDao = new IlsCatalogueDescriptorDao();
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
		IlsCatalogueDescriptor instrumentsList = findDescriptor("trajectories");
		assertNotNull(instrumentsList);
		
		List<HelioFieldDescriptor<?>> fieldDescriptors = instrumentsList.getFieldDescriptors();
		assertEquals(10, fieldDescriptors.size());
		assertEquals("target_obj", fieldDescriptors.get(0).getId());
	}

	private IlsCatalogueDescriptor findDescriptor(String listName) {
		List<IlsCatalogueDescriptor> lists = catalogueDescriptorDao.getDomainValues();
		assertEquals(3, lists.size());
		for (IlsCatalogueDescriptor ilsCatalogueDescriptor : lists) {
			if (listName.equals(ilsCatalogueDescriptor.getValue())) {
				return ilsCatalogueDescriptor;
			}
		}
		return null;
	}
}
