package eu.heliovo.clientapi.processing.processing.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.processing.ProcessingServiceFactory;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.hps.CmePropagationModel;
import eu.heliovo.clientapi.processing.hps.CmePropagationModel.CmeProcessingResultObject;
import eu.heliovo.clientapi.processing.hps.impl.CmePropagationModelImpl;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Demo for the processing client
 * @author MarcoSoldati
 *
 */
public class ProcessingClientDemo {
    /**
     * The context service factory.
     */
    final static ProcessingServiceFactory factory = ProcessingServiceFactory.getInstance();

    /**
     * Main method for demo
     * @param args ignored
     * @throws Exception in case of a problem.
     */
    public static void main(String[] args) throws Exception {
        new HelioClient().init();
        
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
                        boolean cme = true;
                        
                        if (cme) {
                            CmePropagationModel cmePMService = (CmePropagationModel)factory.getHelioService(HelioServiceName.HPS, CmePropagationModelImpl.SERVICE_VARIANT, (AccessInterface[])null);
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeZone(TimeZone.getTimeZone("UTC"));

                            cal.setTimeInMillis(0);
                            cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
                            cal.add(Calendar.MONTH, j);
                            body.append("<h1>CME plot for " + SimpleDateFormat.getDateInstance().format(cal.getTime()) + "</h1>\n");
                            float longitude = 0;
                            float width = 45;
                            float speed = 100;
                            float speedError = 0;
                            ProcessingResult<CmeProcessingResultObject> result = cmePMService.execute(cal.getTime(), longitude, width, speed, speedError);
                            CmeProcessingResultObject resultObject = result.asResultObject(60, TimeUnit.SECONDS);
                            
                            body.append("<p>VOTable: ").append(resultObject.getVoTableUrl());
                            body.append("<a href=\"").append(resultObject.getVoTableUrl()).append("\">")
                                .append(resultObject.getVoTableUrl()).append("</a></p>\n");
                            
                            body.append("<p>Inner plot: ").append(resultObject.getInnerPlotUrl());
                            body.append("<img src=\"").append(resultObject.getInnerPlotUrl()).append("\"/></p>\n");

                            body.append("<p>Outer plot: ").append(resultObject.getOuterPlotUrl());
                            body.append("<img src=\"").append(resultObject.getOuterPlotUrl()).append("\"/></p>\n");

                            LogRecord[] logs = result.getUserLogs();

                            displayImage("Inner Plot", resultObject.getInnerPlotUrl(), logs);
                            displayImage("Outer Plot", resultObject.getOuterPlotUrl(), logs);
                            displayImage("Voyager Plot", resultObject.getVoyagerPlot(), logs);
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
        System.out.println("Result written to:\nfile://" + file.getAbsolutePath());
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
