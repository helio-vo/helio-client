package eu.heliovo.clientapi.model.service.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.model.field.FieldTypeFactory;
import eu.heliovo.clientapi.model.field.descriptor.HecCatalogueDescriptor;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.DateUtil;


public class HecCatalogueDescriptorDaoTest {
    
    private HecCatalogueDescriptorDao catalogueDescriptorDao;

    @Before public void setup() {
        catalogueDescriptorDao = new HecCatalogueDescriptorDao();
        HelioFileUtil helioFileUtil = new HelioFileUtil("test");
        STILUtils stilUtils = new STILUtils();
        stilUtils.setHelioFileUtil(helioFileUtil);
        FieldTypeFactory fieldTypeFactory = new FieldTypeFactory();
        fieldTypeFactory.init();
        
        catalogueDescriptorDao.setHelioFileUtil(helioFileUtil);
        catalogueDescriptorDao.setStilUtils(stilUtils);
        catalogueDescriptorDao.setFieldTypeFactory(fieldTypeFactory);
        
        catalogueDescriptorDao.init();
        
    }; 
    
    @Test public void testInit() throws Exception {
        List<HecCatalogueDescriptor> domainValues = catalogueDescriptorDao.getDomainValues();
        assertTrue(domainValues.size() > 0);
        for (HecCatalogueDescriptor hecCatalogueDescriptor : domainValues) {
            if ("goes_sxr_flare".equals(hecCatalogueDescriptor.getName())) {
                assertEquals("goes_sxr_flare",hecCatalogueDescriptor.getName());
                assertEquals("goes_sxr_flare", hecCatalogueDescriptor.getValue());
                assertEquals("GOES Soft X-ray Flare List", hecCatalogueDescriptor.getDescription());
                assertEquals("GOES Soft X-ray Flare List", hecCatalogueDescriptor.getLabel());
                assertEquals("r", hecCatalogueDescriptor.getOtyp());
                assertEquals("active", hecCatalogueDescriptor.getStatus());
                assertEquals(DateUtil.fromIsoDate("1975-09-01T00:00:00"), hecCatalogueDescriptor.getTimefrom());
                //assertEquals(DateUtil.fromIsoDate("2011-12-31T00:00:00"), hecCatalogueDescriptor.getTimeto());
                assertEquals("event", hecCatalogueDescriptor.getType());
                assertFalse(hecCatalogueDescriptor.isCme());
                assertTrue(hecCatalogueDescriptor.isFlare());
                assertFalse(hecCatalogueDescriptor.isGeo());
                assertFalse(hecCatalogueDescriptor.isIps());
                assertFalse(hecCatalogueDescriptor.isPart());
                assertFalse(hecCatalogueDescriptor.isPlanet());
                return;
            }
        }
        fail("Excpected to have catalogue 'goes_sxr_flare'");
    }
    
    
    @Test public void testBeanInfo() {
        List<HecCatalogueDescriptor> domainValues = catalogueDescriptorDao.getDomainValues();
        assertTrue(domainValues.size() > 0);
        BeanInfo beanInfo = domainValues.iterator().next().getBeanInfo();
        assertNotNull(beanInfo);
        PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
        assertNotNull(propDescs);
        assertEquals(15, propDescs.length);
        
        assertEquals("name", propDescs[0].getName());
        assertEquals("description", propDescs[1].getName());
        assertEquals("timefrom", propDescs[2].getName());
        
    }
}