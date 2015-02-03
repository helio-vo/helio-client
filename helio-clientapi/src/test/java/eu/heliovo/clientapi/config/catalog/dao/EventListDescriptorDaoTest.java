package eu.heliovo.clientapi.config.catalog.dao;

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

import eu.heliovo.clientapi.model.catalog.descriptor.EventListDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.DateUtil;


public class EventListDescriptorDaoTest {
    
    private EventListDescriptorDao eventListDescriptorDao;

    @Before public void setup() {
        eventListDescriptorDao = new EventListDescriptorDao();
        HelioFileUtil helioFileUtil = new HelioFileUtil("test");
        STILUtils stilUtils = new STILUtils();
        stilUtils.setHelioFileUtil(helioFileUtil);
        FieldTypeFactory fieldTypeFactory = new FieldTypeFactory();
        fieldTypeFactory.init();
        
        eventListDescriptorDao.setHelioFileUtil(helioFileUtil);
        eventListDescriptorDao.setStilUtils(stilUtils);
        eventListDescriptorDao.setFieldTypeFactory(fieldTypeFactory);
        
        eventListDescriptorDao.init();
        
    }; 
    
    @Test public void testInit() throws Exception {
        List<EventListDescriptor> domainValues = eventListDescriptorDao.getDomainValues();
        assertTrue(domainValues.size() > 0);
        for (EventListDescriptor eventListDescriptor : domainValues) {
            if ("goes_sxr_flare".equals(eventListDescriptor.getName())) {
                assertEquals("goes_sxr_flare",eventListDescriptor.getName());
                assertEquals("goes_sxr_flare", eventListDescriptor.getValue());
                assertEquals("GOES Soft X-ray Flare List", eventListDescriptor.getDescription());
                assertEquals("GOES Soft X-ray Flare List", eventListDescriptor.getLabel());
                assertEquals("R", eventListDescriptor.getOtyp());
                assertEquals("active", eventListDescriptor.getStatus());
                assertEquals(DateUtil.fromIsoDate("1975-09-01T00:00:00"), eventListDescriptor.getTimefrom());
                //assertEquals(DateUtil.fromIsoDate("2011-12-31T00:00:00"), eventListDescriptor.getTimeto());
                assertEquals("event", eventListDescriptor.getType());
                assertFalse(eventListDescriptor.isCme());
                assertTrue(eventListDescriptor.isFlare());
                assertFalse(eventListDescriptor.isGeo());
                assertFalse(eventListDescriptor.isIps());
                assertFalse(eventListDescriptor.isPart());
                assertFalse(eventListDescriptor.isPlanet());
                return;
            }
        }
        fail("Excpected to have catalogue 'goes_sxr_flare'");
    }
    
    @Test public void testBeanInfo() {
        List<EventListDescriptor> domainValues = eventListDescriptorDao.getDomainValues();
        assertTrue(domainValues.size() > 0);
        BeanInfo beanInfo = domainValues.iterator().next().getBeanInfo();
        assertNotNull(beanInfo);
        PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
        assertNotNull(propDescs);
        assertEquals(16, propDescs.length);
        
        assertEquals("name", propDescs[0].getName());
        assertEquals("description", propDescs[1].getName());
        assertEquals("timefrom", propDescs[2].getName());
        assertEquals("infoUrl", propDescs[15].getName());
    }
    
//    @Test public void testFieldDescriptor() {
//        List<HelioFieldDescriptor<?>> fieldDescriptors = 
//                eventListDescriptorDao.findByListName("goes_sxr_flare").getFieldDescriptors();
//        assertNotNull(fieldDescriptors);
//        assertTrue(fieldDescriptors.size() > 0);
//        assertEquals(10, fieldDescriptors.size());
//        
//        assertEquals("hec_id", fieldDescriptors.get(0).getName());
//        assertEquals("time_start", fieldDescriptors.get(1).getName());
//        assertEquals("time_peak", fieldDescriptors.get(2).getName());
//    }
}