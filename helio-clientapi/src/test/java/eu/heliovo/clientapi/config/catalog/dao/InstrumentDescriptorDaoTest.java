package eu.heliovo.clientapi.config.catalog.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.heliovo.clientapi.config.catalog.dao.InstrumentDescriptorDao;
import eu.heliovo.clientapi.model.catalog.descriptor.InstrumentDescriptor;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;


public class InstrumentDescriptorDaoTest {
    
    private InstrumentDescriptorDao instrumentDescriptorDao;

    @Before public void setup() {
        instrumentDescriptorDao = new InstrumentDescriptorDao();
        HelioFileUtil helioFileUtil = new HelioFileUtil("test");
        STILUtils stilUtils = new STILUtils();
        stilUtils.setHelioFileUtil(helioFileUtil);

        instrumentDescriptorDao.setHelioFileUtil(helioFileUtil);
        instrumentDescriptorDao.setStilUtils(stilUtils);
        
        instrumentDescriptorDao.init();
        
    }; 
    
    @Test public void testInit() throws Exception {
        List<InstrumentDescriptor> domainValues = instrumentDescriptorDao.getDomainValues();
        assertTrue(domainValues.size() > 0);
        for (InstrumentDescriptor instrumentDescriptor : domainValues) {
            if ("SOHO__EIT".equals(instrumentDescriptor.getValue())) {
                assertEquals("SOHO__EIT",instrumentDescriptor.getValue());
                assertEquals("Extreme-ultraviolet Imaging Telescope", instrumentDescriptor.getLabel());
                assertEquals("Sun", instrumentDescriptor.getInstOd1());
                assertEquals(null, instrumentDescriptor.getDescription());
                assertTrue(instrumentDescriptor.hasProviders());
                assertTrue(instrumentDescriptor.getProviders().size() > 1);
                return;
            }
        }
        fail("Excpected to find instrument 'SOHO_EIT'");
    }
    
    @Ignore @Test public void testTableOfInstruments() throws Exception {
        System.out.println("Instrument\tInstrument.xsd\tPAT\tICS");
        
        List<InstrumentDescriptor> domainValues = instrumentDescriptorDao.getDomainValues();
        Collections.sort(domainValues, new Comparator<InstrumentDescriptor>() {
            @Override
            public int compare(InstrumentDescriptor o1, InstrumentDescriptor o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        
        for (InstrumentDescriptor instrumentDescriptor : domainValues) {
            StringBuilder line = new StringBuilder("");
            line.append(instrumentDescriptor.getValue());
            line.append("\t");
            if (instrumentDescriptor.isInInstrumentsXsd()) {
                line.append("x");
            }
            line.append("\t");
            if (instrumentDescriptor.isInPat()) {
                line.append("x");
            }
            line.append("\t");
            if (instrumentDescriptor.isInIcs()) {
                line.append("x");
            }
            System.out.println(line);
        }
        
    }
}