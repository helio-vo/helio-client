package eu.heliovo.clientapi.processing.taverna.impl;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import uk.org.taverna.ns._2010.xml.server.soap.BadStateChangeException;
import uk.org.taverna.ns._2010.xml.server.soap.NoUpdateException;
import uk.org.taverna.ns._2010.xml.server.soap.UnknownRunException;
import eu.heliovo.clientapi.processing.ProcessingResultObject;
import eu.heliovo.clientapi.processing.taverna.AbstractTavernaServiceImpl;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283.TavernaWorkflow2283ResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
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
     * Create the Taverna workflow instance
     * @param myExperimentInterface the myExperiment interface.
     * @param tavernaInterfaces the Taverna Server instances to use.
     */
    public TavernaWorkflow2283(AccessInterface[] tavernaInterfaces) {
        super(HelioServiceName.TAVERNA_SERVER, null, getMyExperimentInterface(), tavernaInterfaces);
    }

    /**
     * Get the my experiment interface. This is hard-coded as the Workflow exists only there.
     * @return the myExperimentInterface
     */
    private static AccessInterface getMyExperimentInterface() {
        return new AccessInterfaceImpl(AccessInterfaceType.REST_SERVICE, ServiceCapability.MYEXPERIMENT_REGISTRY, HelioFileUtil.asURL("http://www.myexperiment.org/search.xml"));
    }

    @Override
    protected String getWorkflowId() {
        return WORKFLOW_ID;
    }
    
    @Override
    public TavernaWorkflow2283ResultObject createResultObject(Run run) {
        TavernaWorkflow2283ResultObject resultObject = new TavernaWorkflow2283ResultObject(run);
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
    public static class TavernaWorkflow2283ResultObject implements ProcessingResultObject {
        /**
         * The list of outputs for this workflow
         */
        private static final List<String> OUTPUT_NAMES = Collections.singletonList("votable_url");

        /**
         * Result URL
         */
        private final URL voTableUrl;
        
        
        /**
         * Create the result object with a pointer to the run
         * @param run the run.
         */
        public TavernaWorkflow2283ResultObject(Run run) {
            // compute result URL
            
            // FIXME: hardcoded!
            voTableUrl = HelioFileUtil.asURL("https://eric.rcs.manchester.ac.uk:8443/taverna-server-2/rest/runs/" + run.getId() + "/wd/out/VOTable");
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
