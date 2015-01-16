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

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.linkprovider.LinkProviderService;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.service.HelioService;
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
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.clientapi.query.QueryType;
import eu.heliovo.clientapi.query.WhereClause;
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
//        DebugUtils.enableDump();
        demo.run();
        DebugUtils.disableDump();
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
//        config.add("ics");
//        config.add("icsPat");
        config.add("hec");
//        config.add("hec_sync");
//        config.add("hec_pql");
//        config.add("dpas");
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
//        config.add("dumpServices");
//        config.add("taverna2283");
//        config.add("links");
    }
    

    /**
     * Run the demos.
     */
    public void run() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
        HelioClient helioClient = (HelioClient) context.getBean("helioClient");
        
//        DebugUtils.enableDump();
        if (config.contains("ils")) getIls(helioClient);
        if (config.contains("ics")) getIcs(helioClient);
        if (config.contains("icsPat")) getIcsPat(helioClient);
        if (config.contains("hec")) getHec(helioClient);
        if (config.contains("hec_sync")) getHecSync(helioClient);
        if (config.contains("hec_pql")) getHecPql(helioClient);
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
        if (config.contains("links")) doLinks(helioClient);
        DebugUtils.disableDump();
    }
 

    /**
     * run a propagation model service.
     * @param helioClient
     */
    private void runCmePropagationModel(HelioClient helioClient) {
        CmePropagationModel processingService = (CmePropagationModel) 
            helioClient.getServiceInstance(HelioServiceName.HPS, 
            CmePropagationModelImpl.SERVICE_VARIANT, null);
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
                helioClient.getServiceInstance(HelioServiceName.HPS, CmeBackwardPropagationModelImpl.SERVICE_VARIANT, 
                        null);
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
                helioClient.getServiceInstance(HelioServiceName.HPS, CirPropagationModelImpl.SERVICE_VARIANT, 
                        null);
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
                helioClient.getServiceInstance(HelioServiceName.HPS, CirBackwardPropagationModelImpl.SERVICE_VARIANT, 
                        null);
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
            helioClient.getServiceInstance(HelioServiceName.HPS, SepPropagationModelImpl.SERVICE_VARIANT, 
                    null);
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
                helioClient.getServiceInstance(HelioServiceName.HPS, SepBackwardPropagationModelImpl.SERVICE_VARIANT, 
                        null);
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
                helioClient.getServiceInstance(HelioServiceName.TAVERNA_SERVER, TavernaWorkflow2283.SERVICE_VARIANT, 
                        ServiceCapability.TAVERNA_SERVER);
            
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
        DesPlotterService desPlotter = (DesPlotterService)helioClient.getServiceInstance(HelioServiceName.DES, 
                serviceVariant, ServiceCapability.ASYNC_QUERY_SERVICE);
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
        QueryService service = (QueryService)helioClient.getServiceInstance(
                HelioServiceName.ILS, null, null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2009-01-01T00:00:00"), Arrays.asList("2009-01-02T00:00:00"), Arrays.asList("trajectories"), 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));        
    }

    /**
     * Get the ICS table.
     * @param helioClient the client
     */
    private void getIcs(HelioClient helioClient) {
        QueryService service = (QueryService)helioClient.getServiceInstance(
                HelioServiceName.ICS, null, null);
        HelioQueryResult result = service.query(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), Integer.MAX_VALUE));        
    }
    
    /**
     * Get the ics pat table.
     * @param helioClient the client
     */
    private void getIcsPat(HelioClient helioClient) {
        QueryService service = (QueryService)helioClient.getServiceInstance(HelioServiceName.ICS, 
                "ivo://helio-vo.eu/ics/ics_pat", null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("instrument"), 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));        
    }
    
    /**
     * Get the HEC table.
     * @param helioClient the client
     */
    private void getHec(HelioClient helioClient) {
        QueryService service = (QueryService)helioClient.getServiceInstance(HelioServiceName.HEC, null, null);
        service.setQueryType(QueryType.ASYNC_QUERY);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2009-01-01T00:00:00"), Arrays.asList("2009-01-02T00:00:00"), Arrays.asList("goes_sxr_flare"), 0, 0, null);
        System.out.println(result.asURL());
        //System.out.println(trunc(result.asString(), 5000));        
    }

    /**
     * Get the HEC table.
     * @param helioClient the client
     */
    private void getHecSync(HelioClient helioClient) {
        QueryService service = (QueryService)helioClient.getServiceInstance(HelioServiceName.HEC, null, null);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2009-01-01T00:00:00"), Arrays.asList("2010-01-30T00:00:00"), Arrays.asList("goes_sxr_flare"), 0, 0, null);
        System.out.println(result.asURL());
        //System.out.println(trunc(result.asString(), 5000));
    }
    
    /**
     * Get the HEC table.
     * @param helioClient the client
     */
    private void getHecPql(HelioClient helioClient) {
        QueryService service = (QueryService)helioClient.getServiceInstance(HelioServiceName.HEC, null, null);
        service.setQueryType(QueryType.SYNC_QUERY);
        
        //System.out.println(service.getClass());
        service.setStartTime(Arrays.asList("2000-01-01T00:00:00"));
        service.setEndTime(Arrays.asList("2010-01-30T00:00:00"));
        service.setFrom(Arrays.asList("rhessi_hxr_flare"));
        service.setMaxRecords(20);
        
        List<WhereClause> whereClauses = service.getWhereClauses();
        WhereClause clause = whereClauses.get(0);
        List<HelioFieldDescriptor<?>> descriptors = clause.getFieldDescriptors();
        @SuppressWarnings("unchecked")
        HelioFieldDescriptor<Long> totalCount = (HelioFieldDescriptor<Long>)clause.findFieldDescriptorById("total_count");
        clause.setQueryTerm(totalCount, new HelioFieldQueryTerm<Long>(totalCount, Operator.LARGER_EQUAL_THAN, 100000000l));
        
        System.out.println(descriptors);
        
        HelioQueryResult result = service.execute();
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 20000));
    }

    /**
     * Exec a DPAS query
     * @param helioClient the client
     */
    private void runDPAS(HelioClient helioClient) {
        QueryService service = (QueryService)helioClient.getServiceInstance(HelioServiceName.DPAS, null, ServiceCapability.SYNC_QUERY_SERVICE);
        //System.out.println(service.getClass());
        HelioQueryResult result = service.query(Arrays.asList("2007-10-30T20:00:00"), Arrays.asList("2007-10-31T03:59:59"), Arrays.asList("BBSO__GONG"), 0, 0, null);
        System.out.println(result.asURL());
        System.out.println(trunc(result.asString(), 5000));        
    }
    
    
    private void doFlarePlot(HelioClient helioClient) {
        FlarePlotterService flarePlotterService = (FlarePlotterService) helioClient.getServiceInstance(HelioServiceName.CXS, FlarePlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
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
     * Test the link providers
     * @param helioClient
     */
    private void doLinks(HelioClient helioClient) {
        HelioService[] linkProviders = helioClient.getServiceInstances(ServiceCapability.LINK_PROVIDER_SERVICE);
        for (HelioService linkProvider : linkProviders) {
            
            // create some dummy date
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(0);
            cal.set(2005, Calendar.JANUARY, 1, 0, 0, 0);
            Date startTime = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date endTime = cal.getTime();
            
            // if link is null there is not page for the given date range.
            String title = ((LinkProviderService)linkProvider).getTitle(startTime, endTime);
            URL link = ((LinkProviderService)linkProvider).getLink(startTime, endTime);
            System.out.println(title + " - " + link);
        }
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
