package eu.heliovo.clientapi.query.asyncquery.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Test the {@link IcsPatAsyncQueryServiceImpl}.
 * @author MarcoSoldati
 *
 */
public class IcsPatAsyncQueryServiceImplTest {

    /**
     * Test the loading of an instruments list.
     */
    @Test public void testIcsPatCreation() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:eu/heliovo/clientapi/spring-clientapi.xml");
        HelioClient helioClient = (HelioClient) context.getBean("helioClient");
        
        IcsPatAsyncQueryServiceImpl icsService = (IcsPatAsyncQueryServiceImpl) helioClient.getServiceInstance(HelioServiceName.ICS, IcsPatAsyncQueryServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE);
        assertNotNull(icsService);
        HelioQueryResult result = icsService.query(Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("instrument"), null, 0, 0, null);
        
        System.out.println(result.asURL());
        System.out.println(result.asString());
    }
}
