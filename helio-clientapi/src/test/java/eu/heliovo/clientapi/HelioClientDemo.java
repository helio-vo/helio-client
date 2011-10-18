package eu.heliovo.clientapi;

import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
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
        helioClient.init();
        if (false) getIcsPat(helioClient);
        if (true ) getDesPlot(helioClient, AcePlotterServiceImpl.SERVICE_VARIANT);
        if (true ) getDesPlot(helioClient, StaPlotterServiceImpl.SERVICE_VARIANT);
        if (true ) getDesPlot(helioClient, StbPlotterServiceImpl.SERVICE_VARIANT);
        if (true ) getDesPlot(helioClient, UlyssesPlotterServiceImpl.SERVICE_VARIANT);
        if (true ) getDesPlot(helioClient, WindPlotterServiceImpl.SERVICE_VARIANT);
        if (false) dumpServices(helioClient);
    }

    /**
     * Get a plot for a specific mission.
     * @param helioClient the client
     * @param serviceVariant the variant of the service for a specific plot.
     */
    private void getDesPlot(HelioClient helioClient, String serviceVariant) {
        DesPlotterService desPlotter = (DesPlotterService)helioClient.getServiceInstance(HelioServiceName.DES, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, serviceVariant);
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate =  cal.getTime();
        cal.set(2010, Calendar.JANUARY, 3, 0, 0, 0);
        Date endDate =  cal.getTime();

        ProcessingResult result = desPlotter.desPlot(startDate, endDate);
        URL url = result.asURL(1, TimeUnit.MINUTES);
        System.out.println(url);
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
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("instrument"), null, 0, 0, null);
        System.out.println(result.asURL());
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
