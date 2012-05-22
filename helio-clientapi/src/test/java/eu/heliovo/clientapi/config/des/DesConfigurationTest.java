package eu.heliovo.clientapi.config.des;

import static org.junit.Assert.*;
import org.junit.Test;

public class DesConfigurationTest {
    
    @Test public void testInit() {
        DesConfiguration desConfiguration = new DesConfiguration();
        desConfiguration.init();
        
        assertEquals(5, desConfiguration.getMissions().size());
        assertEquals(4, desConfiguration.getFunctions().size());
        assertEquals(6, desConfiguration.getParams().size());
    }
}
