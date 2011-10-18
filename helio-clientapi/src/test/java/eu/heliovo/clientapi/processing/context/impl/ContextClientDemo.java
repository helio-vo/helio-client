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

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.context.ContextServiceFactory;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.FlarePlotterService;
import eu.heliovo.clientapi.processing.context.GoesPlotterService;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Demo for the context client
 * @author MarcoSoldati
 *
 */
public class ContextClientDemo {
    /**
     * The context service factory.
     */
    final static ContextServiceFactory factory = ContextServiceFactory.getInstance();

    /**
     * Main method for demo
     * @param args ignored
     * @throws Exception in case of a problem.
     */
    public static void main(String[] args) throws Exception {
        //DebugUtils.enableDump();
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
                        boolean parker = false;
                        boolean flare = false;
                        boolean goes = false;
                        boolean ace = true;
                        boolean sta = true;
                        boolean stb = true;
                        boolean ulysses = true;
                        boolean wind = true;
                        
                        if (flare) {
                            FlarePlotterService flarePlotterService = factory.getFlarePlotterService((AccessInterface[])null);
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeZone(TimeZone.getTimeZone("UTC"));

                            cal.setTimeInMillis(0);
                            cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            body.append("<h1>flare plot for " + SimpleDateFormat.getDateInstance().format(cal.getTime()) + "</h1>\n");
                            ProcessingResult result = flarePlotterService.flarePlot(cal.getTime());

                            URL url = result.asURL(60, TimeUnit.SECONDS);
                            body.append("<p>").append(url);
                            body.append("<img src=\"").append(url).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();

                            displayImage("Flare Plot", url, logs);
                        }

                        
                        if (goes){
                            Calendar cal = Calendar.getInstance();
                            GoesPlotterService goesPlotterService = factory.getGoesPlotterService((AccessInterface[])null);
                            cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            Date startDate =  cal.getTime();
                            cal.set(2003, Calendar.JANUARY, 3, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            Date endDate =  cal.getTime();
                            body.append("<h1>goes plot for " + SimpleDateFormat.getDateInstance().format(startDate) + "-" + SimpleDateFormat.getDateInstance().format(endDate) + "</h1>\n");
                            ProcessingResult result = goesPlotterService.goesPlot(startDate, endDate);

                            URL url = result.asURL(60, TimeUnit.SECONDS);
                            body.append("<p>").append(url);
                            body.append("<img src=\"").append(url).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();
                            displayImage("Goes Plot", url, logs);
                        }
                        
                        if (parker) {
                            Calendar cal = Calendar.getInstance();
                            SimpleParkerModelService parkerModelService = factory.getSimpleParkerModelService((AccessInterface[])null);
                            cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            Date startDate =  cal.getTime();
                            body.append("<h1>parker plot for " + SimpleDateFormat.getDateInstance().format(startDate) + "</h1>\n");
                            ProcessingResult result = parkerModelService.parkerModel(startDate);

                            URL url = result.asURL(60, TimeUnit.SECONDS);
                            body.append("<p>").append(url);
                            body.append("<img src=\"").append(url).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();
                            displayImage("Parker Model", url, logs);
                        }
                        
                        if (ace) {
                            body.append(desPlot(AcePlotterServiceImpl.MISSION, AcePlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (sta) {
                            body.append(desPlot(StaPlotterServiceImpl.MISSION, StaPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (stb) {
                            body.append(desPlot(StbPlotterServiceImpl.MISSION, StbPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (ulysses) {
                            body.append(desPlot(UlyssesPlotterServiceImpl.MISSION, UlyssesPlotterServiceImpl.SERVICE_VARIANT, j));
                        }
                        if (wind) {
                            body.append(desPlot(WindPlotterServiceImpl.MISSION, WindPlotterServiceImpl.SERVICE_VARIANT, j));
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
     * @param serviceVariant the service variant
     * @param counter the counter value
     * @return HTML stub created by this call.
     */
    protected static String desPlot(String mission, String serviceVariant, int counter) {
        StringBuilder body = new StringBuilder();
        ProcessingResult result = null;
        try {
            DesPlotterService desPlotter = (DesPlotterService)factory.getHelioService(HelioServiceName.DES, serviceVariant, (AccessInterface[])null);
            Calendar cal = Calendar.getInstance();
            cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
            cal.add(Calendar.MONTH, counter);
            Date startDate =  cal.getTime();
            cal.set(2003, Calendar.JANUARY, 3, 0, 0, 0);
            cal.add(Calendar.MONTH, counter);
            Date endDate =  cal.getTime();
            body.append("<h1>" + mission + " plot for " + SimpleDateFormat.getDateInstance().format(startDate) + "</h1>\n");
            result = desPlotter.desPlot(startDate, endDate);
    
            URL url = result.asURL(60, TimeUnit.SECONDS);
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
