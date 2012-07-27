package eu.heliovo.clientapi.config.catalog;

import static junit.framework.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeFactory;
import eu.heliovo.clientapi.model.service.dao.HecCatalogueDescriptorDao;
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
        
        HecCatalogueDescriptorDao hecCatalogueDescriptorDao = new HecCatalogueDescriptorDao();
        hecCatalogueDescriptorDao.setStilUtils(stilUtils);
        hecCatalogueDescriptorDao.setHelioFileUtil(helioFileUtil);
        hecCatalogueDescriptorDao.setFieldTypeFactory(fieldTypeFactory);
        hecCatalogueDescriptorDao.init();
        
        hecFromPropertyHandler.setHecCatalogueDescriptorDao(hecCatalogueDescriptorDao);
        hecFromPropertyHandler.init();
        
        assertNotNull(hecFromPropertyHandler.getPropertyDescriptor());
        Collection<DomainValueDescriptor<String>> domainValues = hecFromPropertyHandler.getPropertyDescriptor().getValueDomain();
        int i =0;
        for (DomainValueDescriptor<String> domainValueDescriptor : domainValues) {
            i++;
            System.out.println(i + ":\t"+ domainValueDescriptor.getLabel() + " (" + domainValueDescriptor.getValue() + ")");
        }
    }
}