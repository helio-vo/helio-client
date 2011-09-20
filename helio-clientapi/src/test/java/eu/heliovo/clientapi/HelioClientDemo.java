package eu.heliovo.clientapi;

import java.util.Arrays;

import org.junit.Test;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Demo on how to use the HelioClient to call individual services.
 * @author MarcoSoldati 
 *
 */
public class HelioClientDemo {

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        HelioClientDemo demo = new HelioClientDemo();
        demo.run();
        try {
            demo.finalize();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        demo = null;
        
        // safely terminate demo.
        System.exit(0);
    }


    /**
     * Run the demos.
     */
    @SuppressWarnings("unused")
    @Test public void run() {
        HelioClient helioClient = new HelioClient();
        if (true ) getIcsPat(helioClient);
        if (false) dumpServices(helioClient);
    }

    /**
     * Print all services to the stacktrace.
     * @param helioClient the helio client
     */
    private void dumpServices(HelioClient helioClient) {
        HelioServiceName[] serviceNames = helioClient.getAllServiceNames();
        System.out.println("------ Services -------");
        for (HelioServiceName helioServiceName : serviceNames) {
            System.out.print("* ");
            System.out.println(helioServiceName);
        }
        System.out.println("-----------------------");
    }
    
    /**
     * Get the ics pat table.
     * @param helioClient the client
     */
    private void getIcsPat(HelioClient helioClient) {
        AsyncQueryService service = (AsyncQueryService)helioClient.getServiceInstance(HelioServiceName.ICS, ServiceCapability.ASYNC_QUERY_SERVICE, "ivo://helio-vo.eu/ics/ics_pat");
        HelioQueryResult result = service.query(Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("instrument"), null, 0, 0, null);
        //System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));
        
    }

    /**
     * Truncate a string and append '...'
     * @param string the string to trunc
     * @param len the length of the string.
     * @return the truncated string followed by ...
     */
    private  String trunc(String string, int len) {
        if (string.length() <= len) {
            return string;
        }
        return string.substring(0, len) + "...";
    }
    
    
    
}
