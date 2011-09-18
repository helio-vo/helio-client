package eu.heliovo.clientapi.processing.context.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.context.ContextServiceFactory;
import eu.heliovo.clientapi.processing.context.FlarePlotterService;
import eu.heliovo.clientapi.processing.context.GoesPlotterService;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService;
import eu.heliovo.registryclient.AccessInterface;

public class ContextClientDemo {

    public static void main(String[] args) {
        //DebugUtils.enableDump();
        ContextServiceFactory factory = ContextServiceFactory.getInstance();
        
        FlarePlotterService flarePlotterService = factory.getFlarePlotterService((AccessInterface[])null);
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        cal.setTimeInMillis(0);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        ProcessingResult result = flarePlotterService.flarePlot(cal.getTime());
        
        URL url = result.asURL(60, TimeUnit.SECONDS);
        
        LogRecord[] logs = result.getUserLogs();
        
        displayImage("Flare Plot", url, logs);
        
        
        GoesPlotterService goesPlotterService = factory.getGoesPlotterService((AccessInterface[])null);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate =  cal.getTime();
        cal.set(2003, Calendar.JANUARY, 3, 0, 0, 0);
        Date endDate =  cal.getTime();
        result = goesPlotterService.goesPlot(startDate, endDate);
        
        url = result.asURL(60, TimeUnit.SECONDS);
        logs = result.getUserLogs();
        displayImage("Goes Plot", url, logs);
        
        SimpleParkerModelService parkerModelService = factory.getSimpleParkerModelService((AccessInterface[])null);
        cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
        startDate =  cal.getTime();
        result = parkerModelService.parkerModel(startDate);
        
        url = result.asURL(60, TimeUnit.SECONDS);
        logs = result.getUserLogs();
        displayImage("Parker Model", url, logs);
        
    }
    
    /**
     * Number of plots
     */
    private static int plotCounter = 0;
    
    private static void displayImage(final String title, final URL url, final LogRecord[] logs) {
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
                plotCounter++;
            }
        });
    }
}
