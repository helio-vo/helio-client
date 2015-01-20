package eu.heliovo.clientapi.processing.context.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.io.FileUtils;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.FlarePlotterService;
import eu.heliovo.clientapi.processing.context.GoesPlotterService;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService.PlotType;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Demo for the context client
 * @author MarcoSoldati
 *
 */
public class ContextClientDemo {
    
    /**
     * Main method for demo
     * @param args ignored
     * @throws Exception in case of a problem.
     */
    public static void main(String[] args) throws Exception {
        // init the system

        final List<String> config = new ArrayList<String>();
        config.add("parker");
        config.add("flare");
        config.add("goes");
        config.add("ace");
        config.add("sta");
        config.add("stb");
        config.add("ulysses");
        config.add("wind");
        
        //final AccessInterface ai = new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, HelioFileUtil.asURL("http://helio.mssl.ucl.ac.uk/cxs/services/CommonExecutionConnectorService"));
        //final AccessInterface ai = null;

        final GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
        final HelioClient helioClient = context.getBean(HelioClient.class);

        
//        DebugUtils.enableDump();
        final StringBuffer sb = new StringBuffer();
        sb.append("<html><head><title>CXS stress test</title></head><body>\n");
        
        ExecutorService executor = Executors.newCachedThreadPool();
        
        Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        
        for (int i = 0; i < 1; i++) {
            final int j = i;
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    StringBuilder body = new StringBuilder();
                    try {
                        
                        if (config.contains("flare")) {
                            FlarePlotterService flarePlotterService = (FlarePlotterService) helioClient.getServiceInstance(HelioServiceName.CXS, FlarePlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeZone(TimeZone.getTimeZone("UTC"));

                            cal.setTimeInMillis(0);
                            cal.set(2005, Calendar.JANUARY, 1, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            body.append("<h1>flare plot for " + SimpleDateFormat.getDateInstance().format(cal.getTime()) + "</h1>\n");
                            ProcessingResult<UrlProcessingResultObject> result = flarePlotterService.flarePlot(cal.getTime());
                            UrlProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
                            URL url = resultObject.getUrl();
                            body.append("<p>").append(url);
                            body.append("<img src=\"").append(url).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();

                            displayImage("Flare Plot", url, logs);
                        }
                        
                        if (config.contains("goes")){
                            Calendar cal = Calendar.getInstance();
                            GoesPlotterService goesPlotterService = (GoesPlotterService) helioClient.getServiceInstance(HelioServiceName.CXS, GoesPlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
                            cal.set(2003, Calendar.OCTOBER, 1, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            Date startTime =  cal.getTime();
                            cal.set(2003, Calendar.NOVEMBER, 2, 03, 59, 59);
                            cal.add(Calendar.MONTH, j);
                            Date endTime =  cal.getTime();
                            body.append("<h1>goes plot for " + SimpleDateFormat.getDateInstance().format(startTime) + "-" + SimpleDateFormat.getDateInstance().format(endTime) + "</h1>\n");
                            ProcessingResult<UrlProcessingResultObject> result = goesPlotterService.goesPlot(startTime, endTime, eu.heliovo.clientapi.processing.context.GoesPlotterService.PlotType.BOTH);
                            UrlProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
                            URL url = resultObject.getUrl();
                            body.append("<p>").append(url);
                            body.append("<img src=\"").append(url).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();
                            displayImage("Goes Plot", url, logs);
                        }
                        
                        if (config.contains("parker")) {
                            Calendar cal = Calendar.getInstance();
                            SimpleParkerModelService parkerModelService = (SimpleParkerModelService)helioClient.getServiceInstance(HelioServiceName.CXS, SimpleParkerModelServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
                            cal.set(2000, Calendar.FEBRUARY, 1, 9, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            Date startTime =  cal.getTime();
                            int velocity = 800;
                            PlotType plotType = PlotType.OUTER;
                            
                            body.append("<h1>parker plot for " + SimpleDateFormat.getDateInstance().format(startTime) + "</h1>\n");
                            
                            ProcessingResult<UrlProcessingResultObject> result = parkerModelService.parkerModel(startTime, velocity, plotType);
                            UrlProcessingResultObject resultObject;
                            try {
                                 resultObject = result.asResultObject(60, TimeUnit.SECONDS);
                            } catch (Exception e) {
                                for (LogRecord record : result.getUserLogs()) {
                                    System.err.println(record.getMessage());                                    
                                }
                                throw e;
                            }
                            
                            URL url = resultObject.getUrl();
                            body.append("<p>").append(url);
                            body.append("<img src=\"").append(url).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();
                            displayImage("Parker Model", url, logs);
                        }
                        
                        if (config.contains("ace")) {
                            body.append(desPlot(helioClient, AcePlotterServiceImpl.MISSION, AcePlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (config.contains("sta")) {
                            body.append(desPlot(helioClient, StaPlotterServiceImpl.MISSION, StaPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (config.contains("stb")) {
                            body.append(desPlot(helioClient, StbPlotterServiceImpl.MISSION, StbPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (config.contains("ulysses")) {
                            body.append(desPlot(helioClient, UlyssesPlotterServiceImpl.MISSION, UlyssesPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (config.contains("wind")) {
                            body.append(desPlot(helioClient, WindPlotterServiceImpl.MISSION, WindPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        
                        return null;
                    } catch (Exception e) {
                        body.append("<p>exception: ").append(e.getMessage()).append("</p>");
                        e.printStackTrace();
                        throw e;
                    } finally {
                        sb.append(body.toString());
                    }
                }
            });
        }
        
        executor.invokeAll(tasks);
        Thread.sleep(5000);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        sb.append("</body></html>");
        File file = new File("cxs.html");
        FileUtils.writeStringToFile(file, sb.toString());
        System.out.println("Result written to: " + file.getAbsolutePath());
    }
    
    /**
     * Create a des plot
     * @param mission the mission
     * @param variant the name of the registered bean.
     * @param counter the counter value
     * @return HTML stub created by this call.
     */
    protected static String desPlot(HelioClient helioClient, String mission, String variant, int counter) {
        StringBuilder body = new StringBuilder();
        ProcessingResult<UrlProcessingResultObject> result = null;
        try {
            DesPlotterService desPlotter = (DesPlotterService)helioClient.getServiceInstance(HelioServiceName.DES, variant, null);
            Calendar cal = Calendar.getInstance();
            cal.set(2007, Calendar.JANUARY, 1, 0, 0, 0);
            cal.add(Calendar.MONTH, counter);
            Date startTime =  cal.getTime();
            cal.set(2007, Calendar.JANUARY, 3, 0, 0, 0);
            cal.add(Calendar.MONTH, counter);
            Date endTime =  cal.getTime();
            body.append("<h1>" + mission + " plot for " + SimpleDateFormat.getDateInstance().format(startTime) + "</h1>\n");
            result = desPlotter.desPlot(startTime, endTime);
            UrlProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
            URL url = resultObject.getUrl();
            body.append("<p>").append(url);
            body.append("<img src=\"").append(url).append("\"/></p>\n");
    
            LogRecord[] logs = result.getUserLogs();
            displayImage(mission + " PLOT", url, logs);
        } catch (Exception e) {
            body.append("<p>exception: ").append(e.getMessage()).append("</p>");
            e.printStackTrace();
        }
        if (result != null) {
            StringBuilder sb = new StringBuilder("User log: ");
            for (LogRecord logRecord : result.getUserLogs()) {
                if (sb.length() > 0) {
                    sb.append(",\n");
                }
                sb.append(logRecord.getMessage());
            }
            System.out.println(sb.toString());
        }

        return body.toString();
    }

    /**
     * Number of plots
     */
    private static int plotCounter = 0;
    
    /**
     * Display the image in a window
     * @param title the window title.
     * @param url the URL of the image. 
     * @param logs the logs to display
     */
    private synchronized static void displayImage(final String title, final URL url, final LogRecord[] logs) {
        plotCounter++;

        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                JFrame frame = new JFrame(title);
                frame.setSize(900, 800);
                frame.setLocation(plotCounter*25, 25+plotCounter*16);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                Container content = frame.getContentPane();
                content.setLayout(new BorderLayout());
                
                content.add(new JTextField(url.toExternalForm()), BorderLayout.NORTH);
                
                JScrollPane pane = new JScrollPane(new JLabel(new ImageIcon(url)));
                content.add(pane, BorderLayout.CENTER);
                Object[] columnData = new Object[] {"severity", "message"};
                Object[][] rowData = new Object[logs.length][columnData.length];
                for (int i = 0; i < logs.length; i++) {
                    rowData[i][0] = logs[i].getLevel();
                    rowData[i][1] = logs[i].getMessage();
                }
                JTable table = new JTable(rowData, columnData);
               
                content.add(new JScrollPane(table), BorderLayout.SOUTH);
                frame.setVisible(true);
            }
        });
    }
}
