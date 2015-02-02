package eu.heliovo.clientapi.processing.hps;

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

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import eu.heliovo.clientapi.model.service.AbstractRemoteServiceImpl;
import eu.heliovo.clientapi.processing.HelioProcessingServiceResultObject;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.hps.server.AbstractApplicationDescription;
import eu.heliovo.hps.server.ApplicationParameter;
import eu.heliovo.hps.server.HPSService;
import eu.heliovo.hps.server.HPSServiceService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.hps.ApplicationExecutionStatus;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Base class for services that access the HPS
 * @author MarcoSoldati
 * @param <T> Type of the result object returned by a call to this class.
 *
 */
public abstract class AbstractHelioProcessingServiceImpl<T extends HelioProcessingServiceResultObject> extends AbstractRemoteServiceImpl implements ProcessingService<T> {
    /**
     * The logger instance
     */
    static final Logger _LOGGER = Logger.getLogger(AbstractHelioProcessingServiceImpl.class);
    
    /**
     * Name of the context service
     */
    private static final QName QNAME = new QName("http://server.hps.heliovo.eu/", "HPSServiceImplService");
    
    /**
     * Name of the port
     */
    private final static QName HPS_SERVICE_PORT = new QName("http://server.hps.heliovo.eu/", "HPSServiceImplPort");

    public static final String[] HIT_OBJECT_DOMAIN = new String[] {
    "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto", 
    "Cassini", "Dawn", "Galileo", "Messenger", "NewHorizons", "Rosetta", "StereoA", "StereoB", 
    "Ulysses", "Voyager1", "Voyager2"};

    /**
     * Create a client stub for the "best" {@link AccessInterface}. 
     */
    public AbstractHelioProcessingServiceImpl() {
    }

    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.HELIO_PROCESSING_SERVICE;
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
     * Use JAXWS to create a new service port for a given set of WSDL locations.
     * @param accessInterface the service endpoint
     * @return the port to access the service.
     */
    protected HPSService getPort(AccessInterface accessInterface) {
        AssertUtil.assertArgumentNotNull(accessInterface, "accessInterface");
    
        HPSServiceService hpsServiceService = new HPSServiceService(accessInterface.getUrl(), QNAME);
        HPSService  hpsService  =   hpsServiceService.getPort(HPS_SERVICE_PORT, HPSService.class);

        if (_LOGGER.isDebugEnabled()) {
            _LOGGER.debug("Created " + hpsService.getClass().getSimpleName() + " for " + accessInterface.getUrl());
        }
        return hpsService;
    }
    
    /**
     * Submit the request to the remote service
     * @return This object can be used to access the ProcessingResult.
     * @throws JobExecutionException if anything fails while executing the remote job.
     */
    public ProcessingResult<T> execute() throws JobExecutionException {
        final long jobStartTime = System.currentTimeMillis();

        List<LogRecord> logRecords = new ArrayList<LogRecord>();
        
        final AccessInterface currentAccessInterface = getBestAccessInterface();
        final HPSService currentPort = getPort(currentAccessInterface);
        String callId = currentAccessInterface.getUrl() + "::execute";
        logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));
        
        final Boolean fastExecution = true;
        final AbstractApplicationDescription applicationDescription = findApplicationDescriptionById(currentPort, getApplicationId());
        if (applicationDescription == null) {
            throw new JobExecutionException("Unable to find configuration with id: '" + getApplicationId() + ". Probably the configuration on the remote host changed.");
        }
        initApplicationDescription(applicationDescription, logRecords);
        
        final Integer numOfParallelJobs = 1;
        
        // start job
        String executionId = AsyncCallUtils.callAndWait(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String executionId = currentPort.executeApplication(applicationDescription, fastExecution , numOfParallelJobs);
                return executionId;
            }
        }, callId);
        
        if (executionId == null) {
            throw new JobExecutionException("Remote job did return 'null' as execution ID for  call: " + callId);
        }
        

        T resultObject = createResultObject(currentAccessInterface, executionId);
        ProcessingResult<T> processingResult = new HelioProcessingServiceResult<T>(executionId, currentPort, resultObject, callId, jobStartTime, logRecords);
        
        return processingResult; 
    }

    /**
     * Create the result object
     * @param currentAccessInterface the currently used access interface
     * @param executionId the current execution id.
     * @return a new instance of the result object
     */
    protected abstract T createResultObject(AccessInterface currentAccessInterface, String executionId);

    /**
     * Get the application id (currently pm_cme, pm_sw, pm_sw)
     * @return the application id
     */
    protected abstract String getApplicationId();

    /**
     * Find application description by id
     * @param port the HPSService port
     * @param applicationId the application id
     * @return the found application description or null if not applicable.
     */
    protected AbstractApplicationDescription findApplicationDescriptionById(HPSService port, String applicationId) {
        List<AbstractApplicationDescription> applications = port.getPresentApplications();
        for (AbstractApplicationDescription abstractApplicationDescription : applications) {
            if (applicationId.equals(abstractApplicationDescription.getId())) {
                return abstractApplicationDescription;
            }
        }
        return null;
    }

    /**
     * Initialize the application description.
     * @param applicationDescription the description to initialize
     * @param logRecords the log records for logging
     */
    protected void initApplicationDescription(AbstractApplicationDescription applicationDescription, List<LogRecord> logRecords) {
        List<ApplicationParameter> params = applicationDescription.getParameters();
        for (int index = 0; index < params.size(); index++) {
            ApplicationParameter param = params.get(index);
            setParameter(param);
        }
    }
    
    /**
     * Set the value for a particular parameter.
     * @param param the parameter to set.
     */
    protected abstract void setParameter(ApplicationParameter param);

    /**
     * Check the type of the parameter.
     * @param paramName the name of the parameter
     * @param expectedType the expected type
     * @param actualType the actual type.
     * @throws JobExecutionException if expected type and actual type do not match.
     */
    protected void checkType(String paramName, String expectedType, String actualType) throws JobExecutionException {
        if (!expectedType.equals(actualType)) {
            throw new JobExecutionException("Expected type for parameter " + paramName + ": " + expectedType + ", but got " + actualType);
        } 
    }
    
    /**
     * Check if a given value is part of the valuedomain
     * @param paramName the name of the param
     * @param value the value 
     * @param valueDomain the domain.
     */
    protected <U extends Object> void checkValueDomain(String paramName, U value, Collection<U> valueDomain) {
        if (!valueDomain.contains(value)) {
            throw new JobExecutionException("Selected value for parameter " + paramName + " is not part of value domain: " + valueDomain);
        }
    }
    
    /**
     * ProcessingResult object that handles results from the HPS.
     * 
     * @author marco soldati at fhnw ch
     * @param <T> Type of the result object returned by this ProcessingResult.
     * 
     */
    static class HelioProcessingServiceResult<T extends HelioProcessingServiceResultObject> implements ProcessingResult<T> {
        /**
         * The logger instance
         */
        private static final Logger _LOGGER = Logger.getLogger(HelioProcessingServiceResult.class);
        
        /**
         * The id of the call
         */
        private final String resultId;

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
         * The port to use for the call.
         */
        private final HPSService port;

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
        private final T resultObject;
        
        /**
         * Create a Helio query result
         * @param resultId the assigned id
         * @param port the port to be used to retrieve the results of this call.
         * @param resultObject the result object to use to access the results.
         * @param callId identifier of the called function. For user feedback.
         * @param jobStartTime the time when this call has been started.
         * @param logRecords the log records from the parent query. 
         */
        HelioProcessingServiceResult(String resultId, HPSService port, T resultObject, String callId, long jobStartTime, List<LogRecord> logRecords) {
            this.resultId = resultId;
            this.port = port;
            this.resultObject = resultObject;
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
            
            String exeStatus = AsyncCallUtils.callAndWait(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String exeStatus = port.getStatusOfExecution(resultId);
                    return exeStatus;
                }
            }, callId + "::getPhase");
            
            if (ApplicationExecutionStatus.Completed.equals(exeStatus)) {
                phase = Phase.COMPLETED;
                jobEndTime = System.currentTimeMillis();
            } else if (ApplicationExecutionStatus.Running.equals(exeStatus)) {
                phase = Phase.EXECUTING;
            } else if (ApplicationExecutionStatus.Failed.equals(exeStatus)) {
                phase = Phase.ERROR;
                jobEndTime = System.currentTimeMillis();
            } else if (ApplicationExecutionStatus.Undefined.equals(exeStatus)) {
                phase = Phase.UNKNOWN;
            } else {
                throw new JobExecutionException("Unexpected status returned from Helio Processing Service: '" + exeStatus + "'");
            }

            if (jobEndTime != 0) {
                userLogs.add(new LogRecord(Level.INFO, "Processing terminated in " + MessageUtils.formatSeconds(getExecutionDuration()) + " with status '" + phase + "'"));
            }
            
            if (_LOGGER.isTraceEnabled()) {
                _LOGGER.trace("Current phase is " + phase);
            }
            
            return phase;
        }
        
        
        @Override
        public T asResultObject() throws JobExecutionException {
            return asResultObject(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public T asResultObject(long timeout, TimeUnit unit) throws JobExecutionException {
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
                throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue. Affected class: " + AbstractHelioProcessingServiceImpl.class.getName());
            case PENDING:
            case EXECUTING:
                throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time (" + MessageUtils.formatSeconds(TimeUnit.MILLISECONDS.convert(timeout, unit)) + "). CallId: " + callId);
            case COMPLETED:
                // just continue
                break;
            default:
                throw new RuntimeException("Internal error: unexpected status occurred: " + currentPhase);
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
            sb.append("id:").append(resultId).append(", ");
            sb.append("phase:").append(phase).append(", ");
            sb.append("wsdl:").append(callId).append("}");
            return sb.toString();
        }

    }
}
