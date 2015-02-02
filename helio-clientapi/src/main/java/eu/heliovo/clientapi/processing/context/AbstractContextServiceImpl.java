package eu.heliovo.clientapi.processing.context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.astrogrid.applications.cea.CommonExecutionConnector;
import org.astrogrid.applications.cea.CommonExecutionConnectorService;
import org.astrogrid.schema.agparameterdefinition.v1.ParameterValue;
import org.astrogrid.schema.agworkflow.v1.Tool;
import org.astrogrid.schema.ceatypes.v1.ExecutionPhase;
import org.astrogrid.schema.ceatypes.v1.JobIdentifierType;
import org.astrogrid.schema.ceatypes.v1.MessageType;

import eu.heliovo.clientapi.model.service.AbstractRemoteServiceImpl;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.FileUtil;

/**
 * Base class for services that implement the context services.
 * @author MarcoSoldati
 *
 */
public abstract class AbstractContextServiceImpl extends AbstractRemoteServiceImpl implements ProcessingService<UrlProcessingResultObject> {
    /**
     * The logger instance
     */
    static final Logger _LOGGER = Logger.getLogger(AbstractContextServiceImpl.class);
    
    /**
     * Name of the context service
     */
    private static final QName QNAME = new QName("urn:cea.applications.astrogrid.org", "CommonExecutionConnectorService");

    /**
     * Store the port to access the context service. Apparently the port is thread-safe and can be reused.
     */
    protected CommonExecutionConnector currentPort;
    
    /**
     * store the currently active access interface
     */
    protected AccessInterface currentAccessInterface;

    /**
     * Default constructor
     */
    public AbstractContextServiceImpl() {
    	setCapabilites(ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE);
    }
    
    /**
     * Detect the best endpoint to be used for this request.
     */
    protected void updateCurrentInterface() {
        this.currentAccessInterface=loadBalancer.getBestEndPoint(accessInterfaces);
        this.currentPort = getPort(currentAccessInterface);
        //_LOGGER.info("Using service at: " + currentAccessInterface);
    }
    
    /**
     * Use JAXWS to create a new service port for a given set of WSDL locations.
     * @param accessInterface the service endpoint
     * @return the port to access the service.
     */
    protected CommonExecutionConnector getPort(AccessInterface accessInterface) {
        AssertUtil.assertArgumentNotNull(accessInterface, "accessInterface");
    
        CommonExecutionConnectorService commonExecutionConnectorService = new CommonExecutionConnectorService(accessInterface.getUrl(), QNAME);
        CommonExecutionConnector connector = commonExecutionConnectorService.getCommonExecutionConnectorService();
           
        if (_LOGGER.isDebugEnabled()) {
            _LOGGER.debug("Created " + connector.getClass().getSimpleName() + " for " + accessInterface.getUrl());
        }
        return connector;
    }
    
    /**
     * Submit the request to the remote service
     * @return This object can be used to access the ProcessingResult.
     * @throws JobExecutionException if anything fails while executing the remote job.
     */
    public ProcessingResult<UrlProcessingResultObject> execute() throws JobExecutionException {
        updateCurrentInterface();
        final long jobStartTime = System.currentTimeMillis();

        List<LogRecord> logRecords = new ArrayList<LogRecord>();

        final Tool tool = initTool();
        logRecords.add(new LogRecord(Level.INFO, "Parameters: " + toString(tool)));

        final JobIdentifierType jobstepID = getJobstepID();
        
        String callId = currentAccessInterface.getUrl() + "::init";
        logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));

        // setup job
        final String resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String id = currentPort.init(tool, jobstepID);
                return id;
            }
        }, callId);

        callId = currentAccessInterface.getUrl() + "::execute";
        // start job
        boolean success = AsyncCallUtils.callAndWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean success = currentPort.execute(resultId);
                return success;
            }
        }, callId);
        
        if (!success) {
            throw new JobExecutionException("Failed to start remote job on context service for unknown reason. " + callId);
        }
        
        URL resultLocation = computeFileLocation(currentAccessInterface, resultId, getResultFileName());
        URL logOutLocation = computeFileLocation(currentAccessInterface, resultId, getLogOutFileName());
        URL logErrLocation = computeFileLocation(currentAccessInterface, resultId, getLogErrFileName());

        ProcessingResult<UrlProcessingResultObject> processingResult = new ContextServiceResult(resultId, currentPort, resultLocation, logOutLocation, logErrLocation, callId, jobStartTime, logRecords);
        
        return processingResult; 
    }
    
    private String toString(Tool tool) {
        StringBuilder sb = new StringBuilder("input: [");
        boolean first = true;
        for(ParameterValue paramValue : tool.getInput().getParameter()) {
            if (first) 
                first = false;
            else 
                sb.append(", ");
            sb.append(paramValue.getName()).append(": ").append(paramValue.getValue());
        }
        sb.append("], output: [");
        first = true;
        for(ParameterValue paramValue : tool.getOutput().getParameter()) {
            if (first) 
                first = false;
            else 
                sb.append(", ");
            sb.append(paramValue.getName()).append(": ").append(paramValue.getValue());
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * name of the log file.
     * @return name of the log file
     */
    protected abstract String getLogOutFileName();

    /**
     * name of the log file.
     * @return name of the error log file
     */
    protected abstract String getLogErrFileName();

    /**
     * name of the result file.
     * @return name or the result file.
     */
    protected abstract String getResultFileName();

    /**
     * Compute the location of the result file. This is kind of a hack to fix a problem identified with the 
     * current astrogrid cea implementation that cannot work without a explicit storage provider.
     * The workaround was to store the results in the local file system and to access them through a predefined URL.
     * This does not match the SOAP interface, (but it does what it should).
     * @param accessInterface the current interface.
     * @param resultId the id of the current request.
     * @param fileName the name of the file to load.
     * @return an URL object pointing to the result. This URL will not be valid before successful execution of the job.
     */
    protected URL computeFileLocation(AccessInterface accessInterface, String resultId, String fileName) {
        URL accessUrl = accessInterface.getUrl();
        StringBuilder sb = new StringBuilder();
        sb.append(accessUrl.getProtocol())
          .append("://")
          .append(accessUrl.getHost());
        if (accessUrl.getPort() > 0 && accessUrl.getPort() != 80) {
            sb.append(":").append(accessUrl.getPort());
        }
        sb.append("/");
        int slash = accessUrl.getPath().indexOf('/', 1);
        if (slash >= 0) {
            sb.append(accessUrl.getPath().substring(0, slash));
        }
        sb.append("/jobs/")
          .append(resultId)
          .append('/')
          .append(fileName);

        URL url = FileUtil.asURL(sb.toString());
        return url;
    }

    /**
     * Create and populate the tool object to be used by the service. 
     * @return the tool
     */
    protected abstract Tool initTool();

    /**
     * Create the job step id (whatever this is used for)
     * @return the jobstep id
     */
    protected JobIdentifierType getJobstepID() {        
        JobIdentifierType jobstepID = new JobIdentifierType();
        jobstepID.setValue(Long.toHexString(System.currentTimeMillis()));
        return jobstepID;
    }
    
    /**
     * Create a parameter value object
     * @param name the name
     * @param value the value
     * @param indirect ???
     * @return a new param value instance
     */
    protected ParameterValue createParameterValue(String name, String value, boolean indirect) {
        ParameterValue param = new ParameterValue();
        param.setName(name);
        param.setValue(value);
        param.setIndirect(indirect);
        return param;
    };
    
    /**
     * ProcessingResult object that handles results from the CXS service.
     * 
     * @author marco soldati at fhnw ch
     * 
     */
    static class ContextServiceResult implements ProcessingResult<UrlProcessingResultObject> {
        /**
         * The logger instance
         */
        private static final Logger _LOGGER = Logger.getLogger(ContextServiceResult.class);
        
        /**
         * The id of the call
         */
        private final String id;

        /**
         * Default timeout to wait for a result is 120000 milliseconds.
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
         * The port to use for the call.
         */
        private final CommonExecutionConnector port;

        /**
         * List that holds the user logs.
         */
        private final List<LogRecord> userLogs = Collections.synchronizedList(new ArrayList<LogRecord>());

        /**
         * Id of the call, used for user feedback only.
         */
        private final String callId;

        /**
         * Cache the url that points to the result.
         */
        private URL resultURL;

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
         * Location of the result.
         */
        private final URL resultLocation;

        /**
         * Location of the out log file.
         */
        private final URL logOutLocation;

        /**
         * Location of the err log file.
         */
        private final URL logErrLocation;

        /**
         * Create a Helio query result
         * @param id the assigned id
         * @param port the port to be used to retrieve the results of this call.
         * @param resultLocation Location of the expected result.
         * @param logOutLocation location of the expected log out file. 
         * @param logErrLocation location of the expected log err file. 
         * @param callId identifier of the called function. For user feedback.
         * @param jobStartTime the time when this call has been started.
         * @param logRecords the log records from the parent query. 
         */
        ContextServiceResult(String id, CommonExecutionConnector port, URL resultLocation, URL logOutLocation, URL logErrLocation, String callId, long jobStartTime, List<LogRecord> logRecords) {
            this.id = id;
            this.port = port;
            this.resultLocation = resultLocation;
            this.logOutLocation = logOutLocation;
            this.logErrLocation = logErrLocation;
            this.callId = callId;
            this.jobStartTime = jobStartTime;
            this.phase = Phase.QUEUED;
            this.userLogs.addAll(logRecords);
        }
            
        @Override
        public Phase getPhase() {
            // phase cannot change anymore if in any of these states.
            if (phase == Phase.COMPLETED || phase == Phase.ABORTED || phase == Phase.ERROR) {
                return phase;
            }
            
            MessageType status = AsyncCallUtils.callAndWait(new Callable<MessageType>() {
                @Override
                public MessageType call() throws Exception {
                    MessageType status = port.queryExecutionStatus(id);
                    return status;
                }
            }, callId + "::getPhase");
            
            if (status == null) {
                throw new JobExecutionException("Unspecified error occured on '" + callId  + ".queryExecutionStatus()'. Got back null.");
            }
                        
            ExecutionPhase statusValue = status.getPhase();
            switch (statusValue) {
            case COMPLETED:
                phase = Phase.COMPLETED;
                jobEndTime = System.currentTimeMillis();
                break;  
            case ERROR:
                phase = Phase.ERROR;
                jobEndTime = System.currentTimeMillis();
                break;
            case INITIALIZING:
                phase = Phase.QUEUED;
                break;
            case PENDING: 
                phase = Phase.PENDING;
                break;
            case RUNNING:
                phase = Phase.EXECUTING;
                break;
            default:
                phase = Phase.UNKNOWN;
                break;
            }
            
            if (jobEndTime != 0) {
                userLogs.add(new LogRecord(Level.INFO, "Query terminated in " + MessageUtils.formatSeconds(getExecutionDuration()) + " with status '" + statusValue + "'"));
            }
            
            StringBuilder message = new StringBuilder();
            if (status.getSource() != null) {
                message.append(status.getSource());
            }
            if (status.getContent() != null) {
                if (message.length() > 0) {
                    message.append(": ");
                }
                message.append(status.getContent());
            }
//            if (message.length() > 0) {
//                userLogs.add(new LogRecord(Level.INFO, "Status message returned by service: " + message));
//            }
            
            if (_LOGGER.isTraceEnabled()) {
                _LOGGER.trace("Current phase is " + phase);
            }
            
            return phase;
        }
        
        /**
         * Get access to the URL of the remote data.
         * @return the URL of this object.
         */
        
        protected URL asURL() {
            return asURL(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        
        /**
         * Get access to the URL of the remote data.
         * @param timeout time to wait until the call fails 
         * @param unit time unit
         * @return the URL pointing to the remote service.
         * @throws JobExecutionException in case of an error.
         */
        
        protected URL asURL(long timeout, TimeUnit unit) throws JobExecutionException {
            if (resultURL != null) {
                return resultURL;
            }
            
            try {
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
                    throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue. Affected class: " + AbstractContextServiceImpl.class.getName());
                case PENDING:
                    throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time (" + MessageUtils.formatSeconds(TimeUnit.MILLISECONDS.convert(timeout, unit)) + "). CallId: " + callId);
                case COMPLETED:
                    // just continue
                    break;
                default:
                    throw new RuntimeException("Internal error: unexpected status occurred: " + currentPhase);
                }
            } finally {
                addLog(logOutLocation);
                addLog(logErrLocation);
            }
            return resultLocation;
        }
        
        @Override
        public UrlProcessingResultObject asResultObject() throws JobExecutionException {
            return asResultObject(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public UrlProcessingResultObject asResultObject(long timeout, TimeUnit unit) throws JobExecutionException {
            return new UrlProcessingResultObject(asURL(timeout, unit));
        }
                
        /**
         * Add the remote log to the user logs.
         * @param logLocation url pointing to the log.
         */
        private void addLog(URL logLocation) {
            try {
                InputStream logStream = logLocation.openStream();
                String logs = IOUtils.toString(logStream);
                userLogs.add(new LogRecord(Level.FINE, "Log messages from context server:\n" + logs));
                IOUtils.closeQuietly(logStream);
            } catch (IOException e) {
                // ignore the exception
                _LOGGER.info("Unable to read log messages from location " + logLocation + ": " + e.getMessage(), e);
            }
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
            sb.append("id:").append(id).append(", ");
            sb.append("phase:").append(phase).append(", ");
            sb.append("wsdl:").append(callId).append("}");
            return sb.toString();
        }
    }
}
