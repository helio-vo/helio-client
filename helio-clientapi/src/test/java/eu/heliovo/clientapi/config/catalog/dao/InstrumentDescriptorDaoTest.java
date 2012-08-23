package eu.heliovo.clientapi.config.catalog.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.config.catalog.dao.InstrumentDescriptorDao;
import eu.heliovo.clientapi.model.catalog.descriptor.InstrumentDescriptor;
import eu.heliovo.shared.props.HelioFileUtil;


public class InstrumentDescriptorDaoTest {
    
    private InstrumentDescriptorDao instrumentDescriptorDao;

    @Before public void setup() {
        instrumentDescriptorDao = new InstrumentDescriptorDao();
        HelioFileUtil helioFileUtil = new HelioFileUtil("test");
        instrumentDescriptorDao.setHelioFileUtil(helioFileUtil);
        
        instrumentDescriptorDao.init();
        
    }; 
    
    @Test public void testInit() throws Exception {
        List<InstrumentDescriptor> domainValues = instrumentDescriptorDao.getDomainValues();
        assertTrue(domainValues.size() > 0);
        for (InstrumentDescriptor instrumentDescriptor : domainValues) {
            if ("SOHO__EIT".equals(instrumentDescriptor.getValue())) {
                assertEquals("SOHO__EIT",instrumentDescriptor.getValue());
                assertEquals("Extreme-ultraviolet Imaging Telescope", instrumentDescriptor.getLabel());
                assertEquals(null, instrumentDescriptor.getDescription());
                assertTrue(instrumentDescriptor.hasProviders());
                assertTrue(instrumentDescriptor.getProviders().size() > 1);
                return;
            }
        }
        fail("Excpected to find instrument 'SOHO_EIT'");
    }
}