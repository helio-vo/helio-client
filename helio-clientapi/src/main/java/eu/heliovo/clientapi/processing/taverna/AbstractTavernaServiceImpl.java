package eu.heliovo.clientapi.processing.taverna;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import uk.org.taverna.ns._2010.xml.server.Status;
import uk.org.taverna.ns._2010.xml.server.soap.BadStateChangeException;
import uk.org.taverna.ns._2010.xml.server.soap.NoUpdateException;
import uk.org.taverna.ns._2010.xml.server.soap.UnknownRunException;
import eu.heliovo.clientapi.model.service.AbstractRemoteServiceImpl;
import eu.heliovo.clientapi.processing.HelioProcessingServiceResultObject;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ResultObjectFactory;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.myexperiment.Group;
import eu.heliovo.myexperiment.Repository;
import eu.heliovo.myexperiment.Workflow;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.FileUtil;
import eu.heliovo.tavernaserver.Run;
import eu.heliovo.tavernaserver.Server;

/**
 * Base class for services that access the HPS
 * @author MarcoSoldati
 * @param <T> Type of the result object returned by a call to this class.
 *
 */
public abstract class AbstractTavernaServiceImpl<T extends HelioProcessingServiceResultObject> extends AbstractRemoteServiceImpl implements TavernaService<T>, ResultObjectFactory<T, Run> {
    /**
     * The logger instance
     */
    static final Logger _LOGGER = Logger.getLogger(AbstractTavernaServiceImpl.class);
    
    /**
     * The workflow implemented by this service.
     */
    private Workflow workflow;
    
    /**
     * The URL of the my experiment 
     */
    private static URL myExperimentUrl = FileUtil.asURL("http://www.myexperiment.org/search.xml");
    
    /**
     * The default constructor.
     */
    public AbstractTavernaServiceImpl() {
    }
    
    /**
     * Initialize the taverna service.
     */
    public void init() {
        workflow = getHelioWorkflow(getMyExperimentInterface(), getWorkflowId());
    }
    
    /**
     * Get the my experiment interface. This is hard-coded as the Workflow exists only there.
     * @return the myExperimentInterface
     */
    private AccessInterface getMyExperimentInterface() {
        return new AccessInterfaceImpl(AccessInterfaceType.REST_SERVICE, ServiceCapability.MYEXPERIMENT_REGISTRY, myExperimentUrl);
    }
    
    
    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.TAVERNA_SERVER;
    }

    /**
     * Get the best access interface
     * @return the "best" access interface
     */
    protected AccessInterface getBestAccessInterface() {
        AccessInterface currentAccessInterface = this.loadBalancer.getBestEndPoint(accessInterfaces);
        return currentAccessInterface;
    }
    
    /**
     * Get the best Taverna Server instance.
     * @param accessInterface the service endpoint
     * @return the port to access the service.
     */
    protected Server getPort(AccessInterface accessInterface) {
        AssertUtil.assertArgumentNotNull(accessInterface, "accessInterface");
        Server server = new Server(accessInterface.getUrl().toExternalForm(), "taverna", "taverna");
        return server;
    }
    
    /**
     * Get the id of a known HELIO workflow (e.g. "2283")
     * @return the workflow ID.
     */
    protected abstract String getWorkflowId();

    /**
     * Get a specific workflow by id.
     * @param myExperimentInterface the myExperiment registry to use.
     * @param workflowId the id to lookup
     * @return the workflow by id or null if not found.
     */
    protected Workflow getHelioWorkflow(AccessInterface myExperimentInterface, String workflowId) {
        Repository repository;
        try {
            repository = new Repository(myExperimentInterface.getUrl().toExternalForm());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to connect to my Experiment: " + e.getMessage(), e);
        }
        Group group;
        try {
            group = repository.getGroup("HELIO");
            // lookup workflow
            Workflow workflow = null;
            for (Workflow wf : group.getWorkflows()) {
                if (wf.getId().equals(workflowId)) {
                    workflow = wf;
                    break;
                }
            }
            return workflow;
        } catch (IOException e) {
            throw new RuntimeException("Internal Error: Unable to read workflow description: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new RuntimeException("Internal Error: Unable to parse workflow description: " + e.getMessage(), e);
        }
    }
    
    /**
     * Submit the request to Taverna.
     * @return This object can be used to access the ProcessingResult.
     * @throws JobExecutionException if anything fails while executing the remote job.
     */
    public ProcessingResult<T> execute() throws JobExecutionException {
        final long jobStartTime = System.currentTimeMillis();

        List<LogRecord> logRecords = new ArrayList<LogRecord>();
        
        final AccessInterface currentAccessInterface = getBestAccessInterface();
        final Server tavernaServer = getPort(currentAccessInterface);
        String callId = currentAccessInterface.getUrl() + "::execute";
        logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));
        
        // start job
        Run run = AsyncCallUtils.callAndWait(new Callable<Run>() {
            @Override
            public Run call() throws Exception {
                Run run = workflow.submit(tavernaServer);
                return run;
            }
        }, callId);
        
        if (run == null) {
            throw new JobExecutionException("Remote job did return 'null' as execution ID for  call: " + callId);
        }
        
        // add params
        initParameters(run, logRecords);
        
        logRecords.add(new LogRecord(Level.INFO, "Starting workflow with id " + run.getId()));
        ProcessingResult<T> processingResult = new ProcessingResultImpl<T>(run, this, callId, jobStartTime, logRecords);
        
        return processingResult; 
    }

    /**
     * Initialize the run with the parameters of this run.
     * @param run the run
     * @param logRecords user logs for feedback.
     */
    protected abstract void initParameters(Run run, List<LogRecord> logRecords) throws JobExecutionException;

    /* (non-Javadoc)
     * @see eu.heliovo.clientapi.processing.taverna.ResultObjectFactory#createResultObject(eu.heliovo.registryclient.AccessInterface, eu.heliovo.tavernaserver.Run)
     */
    @Override
    public abstract T createResultObject(Run run);

    /**
     * @return the myExperimentUrl
     */
    public static URL getMyExperimentUrl() {
        return myExperimentUrl;
    }

    /**
     * @param myExperimentUrl the myExperimentUrl to set
     */
    public static void setMyExperimentUrl(URL myExperimentUrl) {
        AbstractTavernaServiceImpl.myExperimentUrl = myExperimentUrl;
    }
    
    /**
     * ProcessingResult object that handles results from Taverna Server.
     * @author marco soldati at fhnw ch
     * @param <T> Type of the result object returned by this ProcessingResult.
     * 
     */
    public static class ProcessingResultImpl<T extends HelioProcessingServiceResultObject> implements ProcessingResult<T> {
        /**
         * The logger instance
         */
        private static final Logger _LOGGER = Logger.getLogger(ProcessingResultImpl.class);
        
        /**
         * Current run.
         */
        private final Run run;

        /**
         * Default timeout to wait for a result is 120 seconds.
         */
        private final static long DEFAULT_TIMEOUT = 120000;

        /**
         * Default timeout between two polls in ms.
         */
        private static final long DEFAULT_POLL_INTERVAL = 500;

        /**
         * the current execution phase.
         */
        private Phase phase;
        
        /**
         * Cache the result object.
         */
        private T resultObject;

        /**
         * List that holds the user logs.
         */
        private final List<LogRecord> userLogs = Collections.synchronizedList(new ArrayList<LogRecord>());

        /**
         * Id of the call, used for user feedback only.
         */
        private final String callId;

        /**
         * number of milliseconds between two polls.
         */
        private long pollInterval = DEFAULT_POLL_INTERVAL;

        /**
         * The system time when this call has been started.
         */
        private final long jobStartTime;
        
        /**
         * Time when the status turned to completed
         */
        private long jobEndTime = 0;

        /**
         * The result object to use for accessing the results.
         */
        private final ResultObjectFactory<T, Run> resultObjectFactory;
        
        /**
         * Create a HELIO workflow result.
         * @param run the run assigned with this workflow.
         * @param resultObjectFactory the factory to create a result object.
         * @param callId identifier of the called function. For user feedback.
         * @param jobStartTime the time when this call has been started.
         * @param logRecords the log records from the parent query. 
         */
        ProcessingResultImpl(Run run, ResultObjectFactory<T, Run> resultObjectFactory, String callId, long jobStartTime, List<LogRecord> logRecords) {
            this.run = run;
            this.resultObjectFactory = resultObjectFactory;
            this.callId = callId;
            this.jobStartTime = jobStartTime;
            this.phase = Phase.QUEUED;
            this.userLogs.addAll(logRecords);
            
            // start process
            try {
                run.start();
            } catch (UnknownRunException e) {
                throw new JobExecutionException("Internal Error: UnknownRunException: " + e.getMessage(), e);
            } catch (NoUpdateException e) {
                throw new JobExecutionException("Internal Error: NoUpdateException: " + e.getMessage(), e);
            } catch (BadStateChangeException e) {
                throw new JobExecutionException("Internal Error: BadStateChangeException: " + e.getMessage(), e);
            }
        }
            
        @Override
        public Phase getPhase() {
            // phase cannot change anymore if in any of these states.
            if (phase == Phase.COMPLETED || phase == Phase.ABORTED || phase == Phase.ERROR) {
                return phase;
            }
            
            Status exeStatus;
            try {
                exeStatus = run.getStatus();
            } catch (UnknownRunException e) {
                throw new RuntimeException("Internal Error: Unknown Run: " + e.getMessage(), e);
            }
            
            // map the execution status to a phase.
            switch (exeStatus) {
            case INITIALIZED:
                phase = Phase.PENDING;
                break;
            case OPERATING:
                phase = Phase.EXECUTING;
                break;
            case FINISHED:
                jobEndTime = System.currentTimeMillis();
                String exitcode;
                try {
                    exitcode = run.getListener("io").getProperty("exitcode");
                } catch (UnknownRunException e) {
                    throw new RuntimeException("Internal Error: Unknown Run: " + e.getMessage(), e);
                }
                if (Integer.parseInt(exitcode) != 0) {
                    phase = Phase.ERROR;                    
                } else {
                    phase = Phase.COMPLETED;
                }
                break;
            case STOPPED:
                jobEndTime = System.currentTimeMillis();
                phase = Phase.ABORTED;
                break;
            default:
                break;
            }
            
            if (jobEndTime != 0) {
                userLogs.add(new LogRecord(Level.INFO, "Taverna workflow terminated in " + MessageUtils.formatSeconds(getExecutionDuration()) + " with status '" + phase + "'"));
                addLog(run, "Standard out", "stdout");
                addLog(run, "Standard Error", "stderr");
                addLog(run, "Exit code", "exitcode");
                addLog(run, "Usage Record", "usageRecord");
                addDirectoryLog(run, "Directory");
            }
        
            if (_LOGGER.isTraceEnabled()) {
                _LOGGER.trace("Current phase is " + phase);
            }
            
            return phase;
        }
        
        private void addDirectoryLog(Run run2, String string) {
            try {
                Collection<String> dir = run.listDirectory("out");
                userLogs.add(new LogRecord(Level.FINE, "Output directory: " + dir.toString()));
            } catch (Exception e) {
                _LOGGER.info("Unable to list result directory: " + e.getMessage(), e);
            }
        }

        /**
         * Add a record to the user logs for all return types.
         * @param run the run.
         * @param title the title to add.
         * @param property the property to use.
         */
        private void addLog(Run run, String title, String property) {
            try {
                String log = run.getListener("io").getProperty(property);
                userLogs.add(new LogRecord(Level.FINE, title + ":\n" + log.trim()));
            } catch (Exception e) {
                _LOGGER.info("Unable to read run.listener('io').property('" + property + "'): " + e.getMessage(), e);
            }
        }

        @Override
        public T asResultObject() throws JobExecutionException {
            return asResultObject(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public T asResultObject(long timeout, TimeUnit unit) throws JobExecutionException {
            if (resultObject != null) {
                return resultObject;
            }
            Phase currentPhase = pollStatus(timeout, unit);
            
            if (_LOGGER.isDebugEnabled()) {
                _LOGGER.debug("Current phase is " + currentPhase);
            }
            
            switch (currentPhase) {
            case ERROR:
                throw new JobExecutionException("An error occurred while executing the query. Check the logs for more information (call getUserLogs() on this object).");
            case ABORTED:
                throw new JobExecutionException("Execution of the query has been aborted by the remote host. Check the logs for more information (call getUserLogs() on this object).");                
            case UNKNOWN:
                throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue. Affected class: " + AbstractTavernaServiceImpl.class.getName());
            case PENDING:
                throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time (" + MessageUtils.formatSeconds(TimeUnit.MILLISECONDS.convert(timeout, unit)) + "). CallId: " + callId);
            case COMPLETED:
                // just continue
                break;
            default:
                throw new RuntimeException("Internal error: unexpected status occurred: " + currentPhase);
            }
                        
            try {
                resultObject = resultObjectFactory.createResultObject(run);
            } catch (TavernaWorkflowException e) {
                if (e.getContext() != null) {
                    for (String msg : e.getContext()) {
                        userLogs.add(new LogRecord(Level.WARNING, msg));
                    }
                }
                throw e;
            }

            return resultObject;
        }
        
        /**
         * Poll for the status until the status is not PENDING anymore or
         * the given pollTime has exceeded.
         * @param pollTime the time to poll. This time my be exceeded by the timeout value 
         * to wait for a result from the getStatus method on the remote host (see {@link AsyncCallUtils AsyncCallUtils#DEFAULT_TIMEOUT}).
         * @param unit the unit of the poll time. Microseconds will be truncated to milliseconds.
         * @return the current status.
         */
        public Phase pollStatus(long pollTime, TimeUnit unit) {
            long pollDelay = pollInterval;
            long startTime = System.currentTimeMillis();            
            long pollTimeInMs = TimeUnit.MILLISECONDS.convert(pollTime, unit);
                        
            // poll the service status until it is not pending anymore.
            Phase currentPhase;
            try {
                 currentPhase = getPhase();
            } catch (JobExecutionException e) {
                // decrease polling frequency if server is heavily overloaded.
                if (e.getCause() instanceof TimeoutException) {
                    pollDelay = pollDelay >= 5000 ? pollDelay : pollDelay + 200;
                    userLogs.add(new LogRecord(Level.FINE, "getPhase timedout. Increasing delay between two polls to " + pollDelay));
                    currentPhase = Phase.UNKNOWN;
                } else {
                    throw e;
                }
            }
            long currentTime = System.currentTimeMillis();
            while ((currentPhase == Phase.QUEUED || currentPhase == Phase.PENDING || currentPhase == Phase.EXECUTING || currentPhase == Phase.UNKNOWN) && (pollTimeInMs > (currentTime - startTime))) {
                try {
                    Thread.sleep(pollDelay);
                } catch (InterruptedException e) {
                    // ignore this exception
                }
                try {
                    currentPhase = getPhase();
                } catch (JobExecutionException e) {
                    // decrease polling frequency if server is heavily overloaded.
                    if (e.getCause() instanceof TimeoutException) {
                        pollDelay = pollDelay >= 5000 ? pollDelay : pollDelay + 200;
                        userLogs.add(new LogRecord(Level.FINE, "getPhase timedout. Increasing delay between two polls to " + pollDelay));
                        currentPhase = Phase.UNKNOWN;
                    } else {
                        throw e;
                    }
                }
                currentTime = System.currentTimeMillis();               
            }
            
            return currentPhase;
        }

        @Override
        public LogRecord[] getUserLogs() {
            return userLogs.toArray(new LogRecord[userLogs.size()]);
        }

        @Override
        public int getExecutionDuration() {
            return jobEndTime == 0 ? 0 : (int)(jobEndTime-jobStartTime);
        }

        @Override
        public Date getDestructionTime() {
            return jobEndTime == 0 ? null : new Date(jobEndTime);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getName()).append(": {");
            sb.append("id:").append(run.getId()).append(", ");
            sb.append("phase:").append(phase).append(", ");
            sb.append("wsdl:").append(callId).append("}");
            return sb.toString();
        }

    }
}
