package eu.heliovo.clientapi.loadbalancing.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.shared.util.FileUtil;

/**
 * Unit tests for the {@link RoundRobinLoadBalancer}
 * @author MarcoSoldati
 *
 */
public class RoundRobinLoadBalancerTest {
    
    
    @Test public void testGetBestEndPoint() {
        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        
        AccessInterface[] accessInterfaces = new AccessInterface[5];
        for (int i = 0; i < accessInterfaces.length; i++) {
            accessInterfaces[i] = new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE, FileUtil.asURL("http://helio-vo.eu/service_" + i));
        }
        
        Boolean[] hit = new Boolean[accessInterfaces.length];
        Arrays.fill(hit, Boolean.FALSE);
        
        for (int i = 0; i < accessInterfaces.length; i++) {
            AccessInterface endpoint = loadBalancer.getBestEndPoint(accessInterfaces);
            loadBalancer.updateAccessTime(endpoint, (int)(50 + Math.random() * 200));
            
            URL url = endpoint.getUrl();
            int index = Integer.parseInt(url.toExternalForm().substring(url.toExternalForm().length() -1));
            assertTrue(0<= index && index < accessInterfaces.length);
            assertFalse(hit[index]);
            hit[index] = true;
        }
        Boolean[] expected = new Boolean[accessInterfaces.length];
        Arrays.fill(expected, Boolean.TRUE);
        assertArrayEquals(expected, hit);
    }

}
