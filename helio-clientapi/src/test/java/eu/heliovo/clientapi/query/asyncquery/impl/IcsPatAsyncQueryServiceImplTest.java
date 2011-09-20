package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.Arrays;

import org.junit.Test;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.ServiceRegistryClient;
import eu.heliovo.registryclient.impl.ServiceRegistryClientFactory;

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
        ServiceRegistryClient registryClient = ServiceRegistryClientFactory.getInstance().getServiceRegistryClient();
        AccessInterface accessInterface = registryClient.getBestEndpoint(HelioServiceName.ICS, ServiceCapability.ASYNC_QUERY_SERVICE, AccessInterfaceType.SOAP_SERVICE);
        IcsPatAsyncQueryServiceImpl icsService = new IcsPatAsyncQueryServiceImpl(HelioServiceName.ICS, IcsPatAsyncQueryServiceImpl.SERVICE_VARIANT, null, accessInterface);
        HelioQueryResult result = icsService.query(Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("instrument"), null, 0, 0, null);
        
        System.out.println(result.asURL());
        System.out.println(result.asString());
    }
}
