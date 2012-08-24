package eu.heliovo.clientapi.config.catalog;

import static junit.framework.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

import eu.heliovo.clientapi.config.catalog.dao.EventListDescriptorDao;
import eu.heliovo.clientapi.config.catalog.propertyhandler.HecFromPropertyHandler;
import eu.heliovo.clientapi.model.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;

public class HecFromPropertyHandlerTest {
    
    @Test public void testInit() {
        HecFromPropertyHandler hecFromPropertyHandler = new HecFromPropertyHandler();
        STILUtils stilUtils = new STILUtils();
        HelioFileUtil helioFileUtil = new HelioFileUtil("test");
        stilUtils.setHelioFileUtil(helioFileUtil);
        FieldTypeFactory fieldTypeFactory = new FieldTypeFactory();
        fieldTypeFactory.init();
        
        EventListDescriptorDao eventListDescriptorDao = new EventListDescriptorDao();
        eventListDescriptorDao.setStilUtils(stilUtils);
        eventListDescriptorDao.setHelioFileUtil(helioFileUtil);
        eventListDescriptorDao.setFieldTypeFactory(fieldTypeFactory);
        eventListDescriptorDao.init();
        
        hecFromPropertyHandler.setEventListDescriptorDao(eventListDescriptorDao);
        hecFromPropertyHandler.init();
        
        assertNotNull(hecFromPropertyHandler.getPropertyDescriptor(null));
        Collection<DomainValueDescriptor<String>> domainValues = hecFromPropertyHandler.getPropertyDescriptor(null).getValueDomain();
        int i =0;
        for (DomainValueDescriptor<String> domainValueDescriptor : domainValues) {
            i++;
            System.out.println(i + ":\t"+ domainValueDescriptor.getLabel() + " (" + domainValueDescriptor.getValue() + ")");
        }
    }
}