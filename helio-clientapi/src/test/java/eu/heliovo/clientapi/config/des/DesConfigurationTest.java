package eu.heliovo.clientapi.config.des;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the des configuration
 * @author MarcoSoldati
 *
 */
public class DesConfigurationTest {
    
    private DesConfiguration desConfiguration;

    @Before public void setup() {
        desConfiguration = new DesConfiguration();
        desConfiguration.init();
    }
    
    /**
     * Test the init method
     */
    @Test public void testInit() {
        DesConfiguration desConfiguration = new DesConfiguration();
        desConfiguration.init();
        
        assertEquals(5, desConfiguration.getMissions().size());
        assertEquals(4, desConfiguration.getFunctions().size());
        assertEquals(6, desConfiguration.getParams().size());
    }
    
    /**
     * Test for missions
     */
    @Test public void testGetMissionById() {
        assertNotNull(desConfiguration.getMissionById("ACE"));
        assertNull(desConfiguration.getMissionById("UNKNONW"));
    }
    
    /**
     * Test for functions
     */
    @Test public void testGetFunctionById() {
        assertNotNull(desConfiguration.getFunctionById("DERIV"));
        assertNull(desConfiguration.getFunctionById("UNKNONW"));
    }
    
    @Test public void testGetParamsByMissionAndFunction() {
        List<DesParam> params = desConfiguration.getParamsByMissionAndFunction("ULYSSES", "DERIV");
        assertNotNull(params);
        assertEquals(5, params.size());
    }
}
