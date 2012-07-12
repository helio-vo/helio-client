package eu.heliovo.clientapi.query.asyncquery.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.helio_vo.xml.longqueryservice.v0.ResultInfo;
import eu.helio_vo.xml.longqueryservice.v0.Status;
import eu.helio_vo.xml.longqueryservice.v0.StatusValue;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * HelioQueryResult object that handles results from the long running query interface.
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class AsyncQueryResultImpl implements HelioQueryResult {
	/**
	 * The logger instance
	 */
	public static final Logger _LOGGER = Logger.getLogger(AsyncQueryResultImpl.class);
	
	/**
	 * The id of the call
	 */
	private final String id;

	/**
	 * Default timeout is 120 seconds
	 */
	private final static long DEFAULT_TIMEOUT = 120000;

	/**
	 * Default timeout between two polls in ms.
	 */
	private static final long DEFAULT_POLL_INTERVAL = 200;

	/**
	 * Store the port to access the long running query service. Apparently the port is thread-safe and can be reused.
	 */
	private final LongHelioQueryService port;
	
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
	 * Create a Helio query result
	 * @param id the assigned id
	 * @param port the port to be used to retrieve the results of this call.
	 * @param callId identifier of the called function. For user feedback.
	 * @param jobStartTime the time when this call has been started.
	 * @param logRecords the log records from the parent query. 
	 */
	public AsyncQueryResultImpl(String id, LongHelioQueryService port, String callId, long jobStartTime, List<LogRecord> logRecords) {
		this.id = id;
		this.port = port;
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
		
		Status status = AsyncCallUtils.callAndWait(new Callable<Status>() {
			@Override
			public Status call() throws Exception {
				Status status = port.getStatus(id);
				return status;
			}
		}, callId + "::getPhase");
		
		if (status == null) {
			throw new JobExecutionException("Unspecified error occured on '" + callId  + ".getStatus()'. Got back null.");
		}
					
		StatusValue statusValue = status.getStatus();
		switch (statusValue) {
		case COMPLETED:
			phase = Phase.COMPLETED;
			jobEndTime = System.currentTimeMillis();
			break;	
		case ERROR:
			phase = Phase.ERROR;
			jobEndTime = System.currentTimeMillis();
			break;
		case TIMEOUT:
			phase = Phase.ABORTED;
			jobEndTime = System.currentTimeMillis();
			break;
		case PENDING: 
			phase = Phase.PENDING;
			break;
		default:
			phase = Phase.UNKNOWN;
			break;
		}
		
		if (jobEndTime != 0) {
			userLogs.add(new LogRecord(Level.INFO, "Query terminated in " + MessageUtils.formatSeconds(getExecutionDuration()) + " with status '" + statusValue + "'"));
		}
		
		String message = status.getDescription();
		if (message != null) {
			userLogs.add(new LogRecord(Level.INFO, "Status message returned by service: " + message));
		}
		
		if (_LOGGER.isTraceEnabled()) {
			_LOGGER.trace("Current phase is " + phase);
		}
		
		return phase;
	}
	
	/**
	 * Get access to the URL of the remote data.
	 * @return the URL of this object.
	 */
	@Override
	public URL asURL() {
		return asURL(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Get access to the URL of the remote data.
	 * @param timeout time to wait until the call fails 
	 * @param unit time unit
	 * @return the URL pointing to the remote service.
	 */
	@Override
	public URL asURL(long timeout, TimeUnit unit) throws JobExecutionException {
		if (resultURL != null) {
			return resultURL;
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
			throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue. Affected class: " + AsyncQueryServiceImpl.class.getName());
		case PENDING:
			throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time (" + MessageUtils.formatSeconds(TimeUnit.MILLISECONDS.convert(timeout, unit)) + "). CallId: " + callId);
		case COMPLETED:
			// just continue
			break;
		default:
			throw new RuntimeException("Internal error: unexpected status occurred: " + currentPhase);
		}
		
		ResultInfo resultInfo;
		try {
			resultInfo = AsyncCallUtils.callAndWait(new Callable<ResultInfo>() {
				@Override
				public ResultInfo call() throws Exception {
					ResultInfo result = port.getResult(id);
					return result;
				}
			}, callId + "::asURL");
		} catch (JobExecutionException e) {
			phase = Phase.ERROR;
			// re throw
			throw e;
		}
		if (resultInfo == null) {
			throw new JobExecutionException(new RuntimeException("Exception on server side: ResultInfo must not be null."));
		}
		
		String resultURI = resultInfo.getResultURI();
		if (resultURI == null) {
			throw new JobExecutionException(new RuntimeException("Exception on server side: Did not get back a valid URL."));
		}
		
		String message = resultInfo.getDescription();
		if (message != null) {
			userLogs.add(new LogRecord(Level.INFO, message));
		}

		try {
			resultURL = new URL(resultURI);
		} catch (MalformedURLException e) {
			throw new JobExecutionException("Unable to convert to URL: '"+ resultURI + "'", e);
		}
		
		if (_LOGGER.isDebugEnabled()) {
			_LOGGER.debug("Got URL: " + resultURL);
		}
	
		return resultURL;
	}
	
	/**
	 * Poll for the status until the status is not PENDING anymore or
	 * the given pollTime has exceeded.
	 * @param pollTime the time to poll. This time may be exceeded by the timeout value 
	 * to wait for a result from the getStatus method on the remote host (see {@link AsyncCallUtils}).
	 * @param unit the unit of the poll time. Microseconds will be truncated to milliseconds.
	 * @return the current status.
	 */
	public Phase pollStatus(long pollTime, TimeUnit unit) {
		long startTime = System.currentTimeMillis();			
		long pollTimeInMs = TimeUnit.MILLISECONDS.convert(pollTime, unit);
					
		// poll the service status until it is not pending anymore.
		Phase currentPhase = getPhase();
		long currentTime = System.currentTimeMillis();
		while (currentPhase == Phase.PENDING && (pollTimeInMs > (currentTime - startTime))) {
			try {
				Thread.sleep(pollInterval);
			} catch (InterruptedException e) {
				// ignore this exception
			}
			currentPhase = getPhase();
			currentTime = System.currentTimeMillis();				
		}
		
		return currentPhase;
	}

	public VOTABLE asVOTable() throws JobExecutionException {
		return asVOTable(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	public VOTABLE asVOTable(long timeout, TimeUnit unit) throws JobExecutionException {
		URL url = asURL(timeout, unit);
		return VOTableUtils.getInstance().url2VoTable(url);
	}

    @Override
	public String asString() throws JobExecutionException {
		return asString(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	
	public String asString(long timeout, TimeUnit unit) throws JobExecutionException {
		try {
			return IOUtils.toString(asURL().openStream());
		} catch (IOException e) {
			throw new JobExecutionException("Unable to convert result to String: " + e.getMessage(), e);
		}
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