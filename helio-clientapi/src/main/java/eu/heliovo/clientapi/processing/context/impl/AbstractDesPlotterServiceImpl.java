package eu.heliovo.clientapi.processing.context.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.config.service.ServiceFactory;
import eu.heliovo.clientapi.model.service.AbstractRemoteServiceImpl;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;
import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.clientapi.query.QueryType;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.util.DateUtil;

/**
 * Default implementation of the des plotter service.
 * @author MarcoSoldati
 *
 */
public abstract class AbstractDesPlotterServiceImpl extends AbstractRemoteServiceImpl implements DesPlotterService {
    
    /**
     * Create the DesPlotterServiceImpl
     * @param mission the mission to use by this service impl.
     */
    public AbstractDesPlotterServiceImpl(String mission) {
        this.mission = mission;
        setCapabilites(ServiceCapability.ASYNC_QUERY_SERVICE);
    }

    /**
     * The start date of the date range
     */
    private Date startTime;
    
    /**
     * The end date of the time range.
     */
    private Date endTime;
    
    /**
     * The mission
     */
    private final String mission;

    /**
     * The stil utils
     */
    private STILUtils stilUtils;

    /**
     * The service factory.
     */
    private ServiceFactory serviceFactory;

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public STILUtils getStilUtils() {
        return stilUtils;
    }

    @Required
    public void setStilUtils(STILUtils stilUtils) {
        this.stilUtils = stilUtils;
    }
    

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    @Required
    public void setServiceFactory(
            ServiceFactory serviceFactory) {
        this.serviceFactory =  serviceFactory;
    }

    @Override
    public ProcessingResult<UrlProcessingResultObject> execute() throws JobExecutionException {
        final long jobStartTime = System.currentTimeMillis();

        List<LogRecord> logRecords = new ArrayList<LogRecord>();
        
        String callId =  "desplot:" + mission + ":" + this.startTime + ":" + this.endTime + "::execute";
        logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));
                
        ProcessingResultImpl processingResult = new ProcessingResultImpl(this.startTime, this.endTime, mission, callId, jobStartTime, logRecords, accessInterfaces);
        processingResult.setStilUtils(stilUtils);
        processingResult.setServiceFactory(serviceFactory);
        return processingResult;
    }
    
    @Override
    public ProcessingResult<UrlProcessingResultObject> desPlot(Date startTime, Date endTime) {
        setStartTime(startTime);
        setEndTime(endTime);
        return execute();
    }

    /**
     * ProcessingResult object that handles results from the DES plotting service.
     * @author marco soldati at fhnw ch
     */
    static class ProcessingResultImpl implements ProcessingResult<UrlProcessingResultObject> {
        
        /**
         * The logger instance
         */
        private static final Logger _LOGGER = Logger.getLogger(ProcessingResultImpl.class);

        /**
         * Default timeout to wait for a result is 60 seconds.
         */
        private final static long DEFAULT_TIMEOUT = 60000;

        /**
         * Default timeout between two polls in ms.
         */
        private static final long DEFAULT_POLL_INTERVAL = 500;

        /**
         * the current execution phase.
         */
        private Phase phase;

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
         * Cache the URL pointing to the resulting VOTable.
         */
        private URL resultURL;

        /**
         * the future that provides access to the result once it is there.
         */
        private final Future<HelioQueryResult> future;

        /**
         * Reference to the STIL utils. 
         */
        private STILUtils stilUtils;
        
        /**
         * The query factory used to lookup the async query service for DES.
         */
        private ServiceFactory serviceFactory;
        
        /**
         * Create a HELIO processing result.
         * @param startTime the start time of the plot range.
         * @param endTime the end time of the plot range.
         * @param mission the mission to use.
         * @param callId identifier of the called function. For user feedback.
         * @param jobStartTime the time when this call has been started.
         * @param logRecords the log records from the parent query. 
         * @param accessInterfaces the interfaces to use for getting the plot.
         * 
         */            
        public ProcessingResultImpl(final Date startTime, final Date endTime, final String mission, String callId, long jobStartTime, List<LogRecord> logRecords, final AccessInterface ... accessInterfaces) {
            this.callId = callId;
            this.jobStartTime = jobStartTime;
            this.phase = Phase.QUEUED;
            this.userLogs.addAll(logRecords);

            this.future = AsyncCallUtils.callLater(new Callable<HelioQueryResult>() {
                @Override
                public HelioQueryResult call() throws Exception {
                    QueryService desService = (QueryService)serviceFactory.getHelioServices(HelioServiceName.DES, DesPlotterService.SERVICE_VARIANT, null, accessInterfaces)[0];
                    desService.setQueryType(QueryType.ASYNC_QUERY);
                    HelioQueryResult result = desService.timeQuery(DateUtil.toIsoDateString(startTime), DateUtil.toIsoDateString(endTime), mission, 0, 0);
                    return result;
                }
            }, callId);
        }

        /**
         * Set the stilUtils bean.
         * @param stilUtils the stil utils bean.
         */
        public void setStilUtils(STILUtils stilUtils) {
            this.stilUtils = stilUtils;
        }
        
        /**
         * Set the async query factory to be used.
         * @param serviceFactory the service factory
         */
        public void setServiceFactory(
                ServiceFactory serviceFactory) {
            this.serviceFactory = serviceFactory;
        }

        @Override
        public Phase getPhase() {
            if (future.isDone()) {
                Phase currentPhase;
                try {
                    currentPhase = future.get().getPhase();
                    Collections.addAll(this.userLogs, future.get().getUserLogs());
                } catch (InterruptedException e) {
                    throw new JobExecutionException("Got interrupted while calling '" + callId + "': " + e.getMessage(), e);
                } catch (ExecutionException e) {
                    throw new JobExecutionException("Exception while calling '" + callId + "': " + e.getMessage(), e);
                }
                
                // set end time
                switch (currentPhase) {
                case ABORTED:
                case COMPLETED:
                case ERROR:
                    phase = currentPhase;
                    jobEndTime = System.currentTimeMillis();
                    break;
				case EXECUTING:
				case HELD:
				case PENDING:
				case QUEUED:
				case SUSPENDED:
				case UNKNOWN:
				default:
					break;  
                }
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
         * Get access to the URL of the png.
         * @param timeout time to wait until the call fails 
         * @param unit time unit
         * @return the URL pointing to the remote service.
         * @throws JobExecutionException if anything goes wrong during execution of the job.
         */
        protected URL asURL(long timeout, TimeUnit unit) throws JobExecutionException {
            if (resultURL != null) {
                return resultURL;
            }
            
            Phase currentPhase = pollStatus(timeout, unit);
            
            if (_LOGGER.isDebugEnabled()) {
                _LOGGER.debug("Current phase is " + currentPhase);
            }
            
            switch (currentPhase) {
            case ERROR:
                throw new JobExecutionException("An error occurred while requesting the plot. Check the logs for more information (call getUserLogs() on this object).");
            case ABORTED:
                throw new JobExecutionException("Execution of the query has been aborted by the remote host. Check the logs for more information (call getUserLogs() on this object).");                
            case UNKNOWN:
                throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue. Affected class: " + AbstractDesPlotterServiceImpl.class.getName());
            case PENDING:
                throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time (" + MessageUtils.formatSeconds(TimeUnit.MILLISECONDS.convert(timeout, unit)) + "). CallId: " + callId);
            case COMPLETED:
                // just continue
                break;
            default:
                throw new RuntimeException("Internal error: unexpected status occurred: " + currentPhase);
            }
            
            // now we can read the url of the png fron the vo table.
            StarTable[] tables;
            try {
                tables = stilUtils.read(future.get().asURL());
            } catch (IOException e) {
                throw new JobExecutionException("Unable to read VOTable from DES plotter: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                throw new JobExecutionException("Got interrupted while reading result for '" + callId + "': " + e.getMessage(), e);
            } catch (ExecutionException e) {
                throw new JobExecutionException("Exception reading result for '" + callId + "': " + e.getMessage(), e);
            }
            
            
            if (tables == null || tables.length != 1) {
                throw new JobExecutionException("Result from DES should contain exactly one table but got " + (tables == null ? null : tables.length));
            }
            
            StarTable table = tables[0];
            
            String plotUrl;
            try {
                plotUrl = (String)table.getCell(0, 0);
            } catch (IOException e) {
                throw new JobExecutionException("Unable to extract DES plot URL from VOTable: " + e.getMessage(), e);
            }
            
            try {
                resultURL = new URL(plotUrl);
            } catch (MalformedURLException e) {
                throw new JobExecutionException("Failed to create URL (" + plotUrl + ") for des plot " + e.getMessage(), e);
            }

            return resultURL;
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
            sb.append("phase:").append(phase).append(", ");
            sb.append("wsdl:").append(callId).append("}");
            return sb.toString();
        }

    }
}
