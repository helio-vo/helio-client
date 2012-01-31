package eu.heliovo.clientapi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.FlarePlotterService;
import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
import eu.heliovo.clientapi.processing.hps.CirBackwardPropagationModel;
import eu.heliovo.clientapi.processing.hps.CirPropagationModel;
import eu.heliovo.clientapi.processing.hps.CirPropagationModel.CirProcessingResultObject;
import eu.heliovo.clientapi.processing.hps.CmeBackwardPropagationModel;
import eu.heliovo.clientapi.processing.hps.CmePropagationModel;
import eu.heliovo.clientapi.processing.hps.CmePropagationModel.CmeProcessingResultObject;
import eu.heliovo.clientapi.processing.hps.SepBackwardPropagationModel;
import eu.heliovo.clientapi.processing.hps.SepPropagationModel;
import eu.heliovo.clientapi.processing.hps.SepPropagationModel.SepProcessingResultObject;
import eu.heliovo.clientapi.processing.hps.impl.CirBackwardPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.CirPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.CmeBackwardPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.CmePropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.SepBackwardPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.SepPropagationModelImpl;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283.TavernaWorkflow2283ResultObject;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.utils.DebugUtils;
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
     * List of available tasks
     */
    private static List<String> config = new ArrayList<String>();
    
    static {
//        config.add("ils");
//        config.add("hec");
        config.add("hec_sync");
//        config.add("icsPat");
//        config.add("desPlot_Ace");
//        config.add("desPlot_Sta");
//        config.add("desPlot_Stb");
//        config.add("desPlot_Ulysses");
//        config.add("desPlot_Wind");
//        config.add("flarePlot");
//        config.add("cmePM");
//        config.add("cmeBwPM");
//        config.add("cirPM");
//        config.add("cirBwPM");
//        config.add("sepPM");
//        config.add("sepBwPM");
//        config.add("dpas");
//        config.add("taverna2283");
//        config.add("dumpServices");
    }
    

    /**
     * Run the demos.
     */
    public void run() {
        HelioClient helioClient = new HelioClient();
        helioClient.init();
        DebugUtils.enableDump();
        if (config.contains("ils")) getIls(helioClient);
        if (config.contains("hec")) getHec(helioClient);
        if (config.contains("hec_sync")) getHecSync(helioClient);
        if (config.contains("icsPat")) getIcsPat(helioClient);
        if (config.contains("dpas")) runDPAS(helioClient);
        if (config.contains("desPlot_Ace")) getDesPlot(helioClient, AcePlotterServiceImpl.SERVICE_VARIANT);
        if (config.contains("desPlot_Sta")) getDesPlot(helioClient, StaPlotterServiceImpl.SERVICE_VARIANT);
        if (config.contains("desPlot_Stb")) getDesPlot(helioClient, StbPlotterServiceImpl.SERVICE_VARIANT);
        if (config.contains("desPlot_Ulysses")) getDesPlot(helioClient, UlyssesPlotterServiceImpl.SERVICE_VARIANT);
        if (config.contains("desPlot_Wind")) getDesPlot(helioClient, WindPlotterServiceImpl.SERVICE_VARIANT);
        if (config.contains("flarePlot")) doFlarePlot(helioClient);
        if (config.contains("cmePM")) runCmePropagationModel(helioClient);
        if (config.contains("cmeBwPM")) runCmeBackwardPropagationModel(helioClient);
        if (config.contains("cirPM")) runCirPropagationModel(helioClient);
        if (config.contains("cirBwPM")) runCirBackwardPropagationModel(helioClient);
        if (config.contains("sepPM")) runSepPropagationModel(helioClient);
        if (config.contains("sepBwPM")) runSepBackwardPropagationModel(helioClient);
        if (config.contains("dumpServices")) dumpServices(helioClient);
        if (config.contains("taverna2283")) doTaverna2283(helioClient);
        DebugUtils.disableDump();
    }
    
    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runCmePropagationModel(HelioClient helioClient) {
        CmePropagationModel processingService = (CmePropagationModel) 
            helioClient.getServiceInstance(HelioServiceName.HPS, 
            ServiceCapability.HELIO_PROCESSING_SERVICE, CmePropagationModelImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        float longitude = 0;
        float width = 45;
        float speed = 100;
        float speedError = 0;
        ProcessingResult<CmeProcessingResultObject> result = processingService.execute(cal.getTime(), longitude, width, speed, speedError);
        CmeProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        System.out.println(resultObject.getVoTableUrl());
        System.out.println(resultObject.getInnerPlotUrl());
        System.out.println(resultObject.getOuterPlotUrl());
    }

    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runCmeBackwardPropagationModel(HelioClient helioClient) {
        CmeBackwardPropagationModel processingService = (CmeBackwardPropagationModel) 
                helioClient.getServiceInstance(HelioServiceName.HPS, 
                        ServiceCapability.HELIO_PROCESSING_SERVICE, CmeBackwardPropagationModelImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        String object = "Earth";
        float width = 45;
        float speed = 100;
        float speedError = 0;
        ProcessingResult<CmeProcessingResultObject> result = processingService.execute(cal.getTime(), object, width, speed, speedError);
        CmeProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        System.out.println(resultObject.getVoTableUrl());
        System.out.println(resultObject.getInnerPlotUrl());
        System.out.println(resultObject.getOuterPlotUrl());
    }

    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runCirPropagationModel(HelioClient helioClient) {
        CirPropagationModel processingService = (CirPropagationModel) 
                helioClient.getServiceInstance(HelioServiceName.HPS, 
                        ServiceCapability.HELIO_PROCESSING_SERVICE, CirPropagationModelImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        float longitude = 0; 
        float speed = 600;
        float speedError = 10;
        ProcessingResult<CirProcessingResultObject> result = processingService.execute(cal.getTime(), longitude, speed, speedError);
        CirProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        System.out.println(resultObject.getVoTableUrl());
        System.out.println(resultObject.getInnerPlotUrl());
        System.out.println(resultObject.getOuterPlotUrl());
    }

    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runCirBackwardPropagationModel(HelioClient helioClient) {
        CirBackwardPropagationModel processingService = (CirBackwardPropagationModel) 
                helioClient.getServiceInstance(HelioServiceName.HPS, 
                        ServiceCapability.HELIO_PROCESSING_SERVICE, CirBackwardPropagationModelImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        String object = "Earth";
        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        float speed = 600;
        float speedError = 10;
        ProcessingResult<CirProcessingResultObject> result = processingService.execute(cal.getTime(), object, speed, speedError);
        CirProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        System.out.println(resultObject.getVoTableUrl());
        System.out.println(resultObject.getInnerPlotUrl());
        System.out.println(resultObject.getOuterPlotUrl());
    }
    
    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runSepPropagationModel(HelioClient helioClient) {
        SepPropagationModel processingService = (SepPropagationModel) 
            helioClient.getServiceInstance(HelioServiceName.HPS, 
            ServiceCapability.HELIO_PROCESSING_SERVICE, SepPropagationModelImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        float longitude = 0;
        float speed = 100;
        float speedError = 0;
        float beta = 0.9f;
        ProcessingResult<SepProcessingResultObject> result = processingService.execute(cal.getTime(), longitude, speed, speedError, beta);
        SepProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        System.out.println(resultObject.getVoTableUrl());
        System.out.println(resultObject.getInnerPlotUrl());
        System.out.println(resultObject.getOuterPlotUrl());
    }

    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runSepBackwardPropagationModel(HelioClient helioClient) {
        SepBackwardPropagationModel processingService = (SepBackwardPropagationModel) 
                helioClient.getServiceInstance(HelioServiceName.HPS, 
                        ServiceCapability.HELIO_PROCESSING_SERVICE, SepBackwardPropagationModelImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        String object = "Earth";
        float speed = 100;
        float speedError = 0;
        float beta = 0.9f;
        ProcessingResult<SepProcessingResultObject> result = processingService.execute(cal.getTime(), object, speed, speedError, beta);
        SepProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        System.out.println(resultObject.getVoTableUrl());
        System.out.println(resultObject.getInnerPlotUrl());
        System.out.println(resultObject.getOuterPlotUrl());
    }
    
    private void doTaverna2283(HelioClient helioClient) {
        TavernaWorkflow2283 processingService = (TavernaWorkflow2283) 
                helioClient.getServiceInstance(HelioServiceName.TAVERNA_SERVER, 
                ServiceCapability.TAVERNA_SERVER, TavernaWorkflow2283.SERVICE_VARIANT);
            
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2010, Calendar.JANUARY, 1, 0, 0, 0);
        Date startTime =  cal.getTime();
        cal.set(2010, Calendar.JANUARY, 3, 0, 0, 0);
        Date endTime =  cal.getTime();

        processingService.setCatalogue1("goes_sxr_flare");
        processingService.setCatalogue2("yohkoh_hxr_flare");
        processingService.setStartTime(startTime);
        processingService.setEndTime(endTime);
        processingService.setLocationDelta(0.5);
        processingService.setTimeDelta(10);
        
        ProcessingResult<TavernaWorkflow2283ResultObject> result = processingService.execute();
        try {
            TavernaWorkflow2283ResultObject resultObject;
            resultObject = result.asResultObject(20, TimeUnit.SECONDS);
            System.out.println("our votable: " + resultObject.getVoTableUrl());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LogRecord[] logs = result.getUserLogs();
            for (LogRecord logRecord : logs) {
                System.out.println(logRecord.getMessage());
            }
        }
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
        Date startTime =  cal.getTime();
        cal.set(2010, Calendar.JANUARY, 3, 0, 0, 0);
        Date endTime =  cal.getTime();

        ProcessingResult<UrlProcessingResultObject> result = desPlotter.desPlot(startTime, endTime);
        UrlProcessingResultObject resultObject = result.asResultObject(1, TimeUnit.MINUTES);
        URL url = resultObject.getUrl();
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
     * Get the ILS table.
     * @param helioClient the client
     */
    private void getIls(HelioClient helioClient) {
        AsyncQueryService service = (AsyncQueryService)helioClient.getServiceInstance(HelioServiceName.ILS, ServiceCapability.ASYNC_QUERY_SERVICE, null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2009-01-01T00:00:00"), Arrays.asList("2009-01-02T00:00:00"), Arrays.asList("trajectories"), null, 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));        
    }
    
    /**
     * Get the HEC table.
     * @param helioClient the client
     */
    private void getHec(HelioClient helioClient) {
        AsyncQueryService service = (AsyncQueryService)helioClient.getServiceInstance(HelioServiceName.HEC, ServiceCapability.ASYNC_QUERY_SERVICE, null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2009-01-01T00:00:00"), Arrays.asList("2009-01-02T00:00:00"), Arrays.asList("goes_sxr_flare"), null, 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));        
    }

    /**
     * Get the HEC table.
     * @param helioClient the client
     */
    private void getHecSync(HelioClient helioClient) {
        SyncQueryService service = (SyncQueryService)helioClient.getServiceInstance(HelioServiceName.HEC, ServiceCapability.SYNC_QUERY_SERVICE, null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2009-01-01T00:00:00"), Arrays.asList("2010-01-30T00:00:00"), Arrays.asList("goes_sxr_flare"), null, 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));
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
     * Exec a DPAS query
     * @param helioClient the client
     */
    private void runDPAS(HelioClient helioClient) {
        SyncQueryService service = (SyncQueryService)helioClient.getServiceInstance(HelioServiceName.DPAS, ServiceCapability.SYNC_QUERY_SERVICE, null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2004-03-25T08:00:00"), Arrays.asList("2004-03-27T22:00:00"), Arrays.asList("WIND__SWE"), null, 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));        
    }
    
    
    private void doFlarePlot(HelioClient helioClient) {
        FlarePlotterService flarePlotterService = (FlarePlotterService) helioClient.getServiceInstance(HelioServiceName.CXS, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, FlarePlotterServiceImpl.SERVICE_VARIANT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        cal.setTimeInMillis(0);
        cal.set(2005, Calendar.JANUARY, 1, 0, 0, 0);
        ProcessingResult<UrlProcessingResultObject> result = flarePlotterService.flarePlot(cal.getTime());
        UrlProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
        URL url = resultObject.getUrl();
        System.out.println(url);
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
