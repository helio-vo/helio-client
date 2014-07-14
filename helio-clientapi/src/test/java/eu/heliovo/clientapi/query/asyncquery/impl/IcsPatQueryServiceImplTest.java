package eu.heliovo.clientapi.query.asyncquery.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.impl.IcsPatQueryServiceImpl;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Test the {@link IcsPatQueryServiceImpl}.
 * @author MarcoSoldati
 *
 */
public class IcsPatQueryServiceImplTest {

    /**
     * Test the loading of an instruments list.
     */
    @Ignore @Test public void testIcsPatCreation() throws Exception {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
        HelioClient helioClient = (HelioClient) context.getBean("helioClient");
        
        IcsPatQueryServiceImpl icsService = (IcsPatQueryServiceImpl) helioClient.getServiceInstance(HelioServiceName.ICS, IcsPatQueryServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE);
        assertNotNull(icsService);
        HelioQueryResult result = icsService.query(Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("instrument"), 0, 0, null);
        assertNotNull(result);
        STILUtils stilUtils = new STILUtils();
        StarTable[] votable = stilUtils.read(result.asURL());
        assertEquals(1, votable.length);
        StarTable resource = votable[0];
        assertEquals(18, resource.getColumnCount());
        assertEquals("isInPat", resource.getColumnInfo(resource.getColumnCount() -1).getName());
        //System.out.println(result.asURL());
        //System.out.println(result.asString());
    }
}
