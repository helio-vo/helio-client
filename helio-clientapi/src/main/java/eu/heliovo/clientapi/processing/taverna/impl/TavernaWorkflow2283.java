package eu.heliovo.clientapi.processing.taverna.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Required;

import uk.org.taverna.ns._2010.xml.server.soap.BadStateChangeException;
import uk.org.taverna.ns._2010.xml.server.soap.NoDirectoryEntryException;
import uk.org.taverna.ns._2010.xml.server.soap.NoUpdateException;
import uk.org.taverna.ns._2010.xml.server.soap.UnknownRunException;
import eu.heliovo.clientapi.processing.HelioProcessingServiceResultObject;
import eu.heliovo.clientapi.processing.taverna.AbstractTavernaServiceImpl;
import eu.heliovo.clientapi.processing.taverna.TavernaWorkflowException;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283.TavernaWorkflow2283ResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.DateUtil;
import eu.heliovo.tavernaserver.Run;

/**
 * Client implementation of Taverna Workflow
 * http://www.myexperiment.org/workflows/2283.html
 * @author MarcoSoldati
 *
 */
public class TavernaWorkflow2283 extends AbstractTavernaServiceImpl<TavernaWorkflow2283ResultObject> {
    
    /**
     * the workflow id.
     */
    private static final String WORKFLOW_ID = "2283";

    /**
     * Name of the variant
     */
    public static final String SERVICE_VARIANT = HelioServiceName.TAVERNA_SERVER.getServiceId() + "/" + WORKFLOW_ID;

    /**
     * The first catalog
     */
    private String catalogue1;
    
    /**
     * The second catalog
     */
    private String catalogue2;    

    /**
     * time_start;  //time period - start Format: yyyy-mm-ddThh:MM:ss
     */
    private Date startTime;
    
    /**
     *  time_end;    //time period - end Format: yyyy-mm-ddThh:MM:ss
     */
    private Date endTime;
    
    /**
     * time_delta //time delta by which the time interval gets enlarged delta in minutes (integer)
     */
    private int timeDelta;  
    
    /**
     * location_delta //Search radius around which an event is thought to come from the same location (float)
     */
    private double locationDelta;   

    /**
     * Set the helio file util
     */
    private transient HelioFileUtil helioFileUtil; 
    
    /**
     * Create the Taverna workflow instance
     */
    public TavernaWorkflow2283() {
    }

    @Override
    protected String getWorkflowId() {
        return WORKFLOW_ID;
    }
    
    @Override
    public TavernaWorkflow2283ResultObject createResultObject(Run run) {
        TavernaWorkflow2283ResultObject resultObject = 
                new TavernaWorkflow2283ResultObject(run, helioFileUtil.getHelioTempDir("taverna"));
        return resultObject;
    }
    
    public String getCatalogue1() {
        return catalogue1;
    }

    public void setCatalogue1(String catalogue1) {
        this.catalogue1 = catalogue1;
    }

    public String getCatalogue2() {
        return catalogue2;
    }

    public void setCatalogue2(String catalogue2) {
        this.catalogue2 = catalogue2;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(int timeDelta) {
        this.timeDelta = timeDelta;
    }

    public double getLocationDelta() {
        return locationDelta;
    }

    public void setLocationDelta(double locationDelta) {
        this.locationDelta = locationDelta;
    }
    
    
    /**
     * @return the helioFileUtil
     */
    public HelioFileUtil getHelioFileUtil() {
        return helioFileUtil;
    }

    /**
     * @param helioFileUtil the helioFileUtil to set
     */
    @Required
    public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
        this.helioFileUtil = helioFileUtil;
    }

    @Override
    protected void initParameters(Run run, List<LogRecord> logRecords) throws JobExecutionException {
        List<String> logs = new ArrayList<String>(6);
        logs.add(initParameter(run, "catalogue1", getCatalogue1()));
        logs.add(initParameter(run, "catalogue2", getCatalogue2()));
        logs.add(initParameter(run, "time_start", DateUtil.toIsoDateString(getStartTime())));
        logs.add(initParameter(run, "time_end", DateUtil.toIsoDateString(getEndTime())));
        logs.add(initParameter(run, "time_delta", ""+getTimeDelta()));
        logs.add(initParameter(run, "location_delta", ""+getLocationDelta()));
        logRecords.add(new LogRecord(Level.FINE, "Parameter map: " + logs.toString()));
    }
    
    /**
     * Init a single parameter.
     * @param run the current run.
     * @param key the key.
     * @param value the value.
     * @return the string as returned to the caller.
     */
    private String initParameter(Run run, String key, String value) {
        try {
            run.setInput(key, value, Charset.defaultCharset());
            return key + "=" + value;
        } catch (NoUpdateException e) {
            throw new JobExecutionException("NoUpdateException while setting parameter '" + key + "': " + e.getMessage(), e);
        } catch (UnknownRunException e) {
            throw new JobExecutionException("UnknownRunException while setting parameter '" + key + "': " + e.getMessage(), e);
        } catch (BadStateChangeException e) {
            throw new JobExecutionException("BadStateChangeException while setting parameter '" + key + "': " + e.getMessage(), e);
        }        
    }

    /**
     * Provide access to the results.
     * @author MarcoSoldati
     *
     */
    public static class TavernaWorkflow2283ResultObject implements HelioProcessingServiceResultObject {
        /**
         * The list of outputs for this workflow
         */
        private static final List<String> OUTPUT_NAMES = Collections.singletonList("votable_url");

        /**
         * Result URL
         */
        private final URL voTableUrl;

        /**
         * Create the result object with a pointer to the run after it successfully terminated.
         * @param run the run.
         */
        public TavernaWorkflow2283ResultObject(Run run, File tavernaTmp) {
            File zipFile = new File(tavernaTmp, run.getId() + ".zip");

            // get the result of the run and store it
            zipFile = persistOutputZip(run, zipFile);
            
            File outDir = new File(tavernaTmp, run.getId());
            try {
                FileUtils.forceMkdir(outDir);
            } catch (IOException e) {
                throw new JobExecutionException("Unable to create output directory: " + outDir + ". Cause: " + e.getMessage(), e);
            }
            File outFile = new File(outDir, "VOTable.votable.xml");
            outFile = persistZipEntry(zipFile, "VOTable", outFile);
            
            // compute result URL
            
            try {
                voTableUrl = outFile.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new JobExecutionException("Unable to create File URL: " + outFile + ". Cause: " + e.getMessage(), e);
            }
        }

        /**
         * Persist the output of the run into a local file.
         * @param run the current run
         * @param zipFile the target file.
         */
        private File persistOutputZip(Run run, File zipFile) {
            byte[] zipArray;
            try {
                zipArray = run.getOutputZip();
            } catch (NoDirectoryEntryException e) {
                throw new RuntimeException("Internal Error: unable to retrieve results: " + e.getMessage(), e);
            } catch (UnknownRunException e) {
                throw new RuntimeException("Internal Error: unable to retrieve results: " + e.getMessage(), e);
            }
            
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(zipFile);
                fos.write(zipArray);
                fos.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Internal Error: unable to create target ZIP " + zipFile.getAbsolutePath() + ". Cause: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Internal Error: unable to create target ZIP: " + e.getMessage(), e);
            }
            return zipFile;
        }

        /**
         * Extract an entry from a ZIP file and persist to disk 
         * @param zipFile the zipfile to extract
         * @param entryName the entry name
         * @param outFile the out file
         * @return the outFile
         */
        private File persistZipEntry(File zipFile, String entryName, File outFile) {
            try (ZipFile zip = new ZipFile(zipFile)) {
            	ZipEntry votableEntry = zip.getEntry(entryName);
            	boolean error = false;
            	if (votableEntry == null) {
            		votableEntry = zip.getEntry(entryName + ".error");
            		outFile = new File(outFile.getAbsolutePath() + ".error");
            		error = true;
            		
            		if (votableEntry == null) {
            			throw new JobExecutionException("Failed to read result from ZIP file " + zipFile, null);
            		}
            	}
            	try {
            		InputStream inputStream = zip.getInputStream(votableEntry);
            		FileUtils.copyInputStreamToFile(inputStream, outFile);
            	} catch (IOException e) {
            		throw new RuntimeException("Internal Error: unable to read entry from previously stored ZIP-file: " + e.getMessage(), e);
            	}
            	
            	if (error) {
            		List<String> context;
            		try {
            			context = Collections.singletonList("Content of " + votableEntry.getName() + ": " +FileUtils.readFileToString(outFile));
            		} catch (IOException e) {
            			// unable to read context.
            			context = Collections.singletonList("Unable to read content of " + votableEntry.getName() + ": " + e.getMessage());
            		}
            		throw new TavernaWorkflowException("Failed to load data from file " + entryName , context);
            	}
            } catch (ZipException e) {
                throw new RuntimeException("Internal Error: unable to read previously stored ZIP-file: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Internal Error: unable to read previously stored ZIP-file: " + e.getMessage(), e);
            }
            return outFile;
        }

        @Override
        public List<String> getOutputNames() throws JobExecutionException {
            return OUTPUT_NAMES;
        }

        @Override
        public Object getOutput(String outputName) throws JobExecutionException {
            if (OUTPUT_NAMES.get(0).equals(outputName)) {
                return getVoTableUrl();
            }
            return null;
        }
        
        /**
         * Get the URL pointing to the rmeote VOTable.
         * @return the VOTable as string.
         */
        public URL getVoTableUrl() {
            return voTableUrl;
        }        
    }
}
