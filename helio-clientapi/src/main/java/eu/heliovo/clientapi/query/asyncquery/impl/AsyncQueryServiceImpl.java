package eu.heliovo.clientapi.query.asyncquery.impl;


import java.io.IOException;
import java.io.InputStream;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService_Service;
import eu.helio_vo.xml.longqueryservice.v0.ResultInfo;
import eu.helio_vo.xml.longqueryservice.v0.Status;
import eu.helio_vo.xml.longqueryservice.v0.StatusValue;
import eu.heliovo.clientapi.help.annotation.Description;
import eu.heliovo.clientapi.help.annotation.TypeHelp;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.shared.util.AssertUtil;


/**
 * Base implementation of the long running query. This wraps the client stub to access all
 * HELIO services that implement the long running query service. 
 * Instances of this class should be retrieved through the {@link AsyncQueryServiceFactory}.
 * @author marco soldati at fhnw ch
 *
 */
class AsyncQueryServiceImpl implements AsyncQueryService, HelioService {
	/**
	 * The logger instance
	 */
	private static final Logger _LOGGER = Logger.getLogger(AsyncQueryServiceImpl.class);
	
	/**
	 * Name of the long query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/LongQueryService/v0.9", "LongHelioQueryService");

	/**
	 * The location of the target WSDL file
	 */
	private final URL wsdlLocation;

	/**
	 * Store the port to access the long running query service. Apparently the port is thread-safe and can be reused.
	 */
	private final LongHelioQueryService port;
	
	/**
	 * Name of the service
	 */
	private final String name;
	
	/**
	 * Description of this service.
	 */
	private final String description;
	
	/**
	 * Create the connector and open the connection to the WSDL file.
	 * @param name the name of the service
	 * @param description a short text to describe the service
	 * @param wsdlLocation the location of the wsdl. Must not be null
	 */
	public AsyncQueryServiceImpl(URL wsdlLocation, String name, String description) {
		this(getPort(wsdlLocation), wsdlLocation, name, description);
	}

	/**
	 * Use JAXWS to create a new service port for a given WSDL location.
	 * @param wsdlLocation the service endpoint
	 * @return the port to access the service.
	 */
	private static LongHelioQueryService getPort(URL wsdlLocation) {
		AssertUtil.assertArgumentNotNull(wsdlLocation, "wsdlLocation");
		LongHelioQueryService_Service queryService = new LongHelioQueryService_Service(wsdlLocation, SERVICE_NAME);		
		LongHelioQueryService port = queryService.getLongHelioQueryServicePort();
		if (_LOGGER.isDebugEnabled()) {
			_LOGGER.debug("Created " + port.getClass().getSimpleName() + " for " + wsdlLocation);
		}
		return port;
	}

	/**
	 * Create the connector and open the connection to the WSDL file. This constructor can be used to submit the port from outside. This
	 * is of particular interest for testing purposes.
	 * @param port the port to be used by this query service
	 * @param wsdlLocation the location of the wsdl. Must not be null
	 * @param name the name of the service
	 * @param description a short text to describe the service
	 */
	AsyncQueryServiceImpl(LongHelioQueryService port, URL wsdlLocation, String name, String description) {
		AssertUtil.assertArgumentNotNull(port, "port");
		AssertUtil.assertArgumentNotNull(wsdlLocation, "wsdlLocation");
		AssertUtil.assertArgumentNotNull(name, "name");
		this.port = port;
		this.wsdlLocation = wsdlLocation;
		this.name = name;
		this.description = description;
	}

	@Override
	public HelioQueryResult query(String startTime, String endTime, String from, String where, Integer maxrecords, Integer startindex, String join) throws JobExecutionException,
			IllegalArgumentException {
		HelioQueryResult result = query(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), where, maxrecords, startindex, join);
		return result;
	}
	
	@Override
	public HelioQueryResult query(List<String> startTime, List<String> endTime, List<String> from, String where, Integer maxrecords, Integer startindex, String join)
			throws JobExecutionException, IllegalArgumentException {
		return query(startTime, endTime, from, where, maxrecords, startindex, join, null);
	}

	@Override
	public HelioQueryResult query(String startTime, String endTime, String from, String where, 
			Integer maxrecords, Integer startindex, String join, String saveto) throws JobExecutionException {
		HelioQueryResult result = query(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), where, maxrecords, startindex, join, saveto);
		return result;
	}
	
	@Override
	public HelioQueryResult query(final List<String> startTime, final List<String> endTime, final List<String> from, final String where,
			final Integer maxrecords, final Integer startindex, String join, final String saveto) throws JobExecutionException {
		final long jobStartTime = System.currentTimeMillis();
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from.size());
		}
		
		List<LogRecord> logRecords = new ArrayList<LogRecord>();

		String callId = wsdlLocation + "::longQuery";
		logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));

		StringBuilder message = new StringBuilder();
		message.append("Executing 'result=longTimeQuery(");
		message.append("startTime=").append(startTime);
		message.append(", ").append("endTime=").append(endTime);
		message.append(", ").append("from=").append(from);
		message.append(", ").append("where=").append(where);
		message.append(", ").append("maxrecords=").append(maxrecords);
		message.append(", ").append("startIndex=").append(startindex);
		message.append(", ").append("saveTo=").append(saveto);
		message.append(")'");
		
		if (_LOGGER.isTraceEnabled()) {
			_LOGGER.trace(message.toString());
		}
		
		logRecords.add(new LogRecord(Level.INFO, message.toString()));

		// wait for result
		String resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
			@Override
			public String call() throws Exception {
				String resultId = port.longQuery(startTime, endTime, from, where, null, maxrecords, startindex);
				return resultId;
			}
		}, callId);
		
		if (resultId == null) {
			throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
		}
		
		// prepare return value
		HelioQueryResult helioQueryResult = new LongRunningQueryResultImpl(resultId, port, callId, jobStartTime, logRecords);
		
		return helioQueryResult;
	}
	
	@Override
	public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
		HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), maxrecords, startindex);
		return result;
	}

	@Override
	public HelioQueryResult timeQuery(List<String> startTime, List<String> endTime, List<String> from, Integer maxrecords, Integer startindex) throws JobExecutionException,
			IllegalArgumentException {
		return timeQuery(startTime, endTime, from, maxrecords, startindex, null);
	}

	@Override
	public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords,
			Integer startindex, String saveto) throws JobExecutionException {
		HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), maxrecords, startindex, saveto);
		return result;
	}

	@Override
	public HelioQueryResult timeQuery(final List<String> startTime, final List<String> endTime, final List<String> from,
			final Integer maxrecords, final Integer startindex, final String saveto) throws JobExecutionException {
		final long jobStartTime = System.currentTimeMillis();
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from.size());
		}

		List<LogRecord> logRecords = new ArrayList<LogRecord>();
		String callId = wsdlLocation + "::longTimeQuery";
		logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));
		
		StringBuilder message = new StringBuilder();
		message.append("longTimeQuery(");
		message.append("startTime=").append(startTime);
		message.append(", ").append("endTime=").append(endTime);
		message.append(", ").append("from=").append(from);
		message.append(", ").append("maxrecords=").append(maxrecords);
		message.append(", ").append("startIndex=").append(startindex);
		message.append(", ").append("saveTo=").append(saveto);
		message.append(")");
		
		if (_LOGGER.isTraceEnabled()) {
			_LOGGER.trace(message.toString());
		}
		
		logRecords.add(new LogRecord(Level.INFO, message.toString()));

		// wait for result
		String resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = port.longTimeQuery(startTime, endTime, from, maxrecords, startindex);
				return result;
			}
		}, callId);
		
		if (resultId == null) {
			throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
		}
		
		// prepare return value
		HelioQueryResult helioQueryResult = new LongRunningQueryResultImpl(resultId, port, callId, jobStartTime, logRecords);
		
		return helioQueryResult;
	}	
	
	
	@Override
	public String getName() {
		return name;
	}
	
	
	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * HelioQueryResult object that handles results from the long running query interface.
	 * 
	 * @author marco soldati at fhnw ch
	 * 
	 */
	@TypeHelp(
		help=@Description("")
	)
	static class LongRunningQueryResultImpl implements HelioQueryResult {
		/**
		 * The logger instance
		 */
		private static final Logger _LOGGER = Logger.getLogger(LongRunningQueryResultImpl.class);
		
		/**
		 * The id of the call
		 */
		private final String id;

		/**
		 * Default timeout is 10 seconds
		 */
		private final static long DEFAULT_TIMEOUT = 10000;

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
		LongRunningQueryResultImpl(String id, LongHelioQueryService port, String callId, long jobStartTime, List<LogRecord> logRecords) {
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
				throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue.");
			case PENDING:
				throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time: " + MessageUtils.formatSeconds(TimeUnit.MILLISECONDS.convert(timeout, unit)));
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
		 * @param pollTime the time to poll. This time my be exceeded by the timeout value 
		 * to wait for a result from the getStatus method on the remote host (see {@link AsyncCallUtils#DEFAULT_TIMEOUT}).
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
			if (_LOGGER.isTraceEnabled()) {
				_LOGGER.trace("Convert URL to VOTABLE");
			}
			URL url = asURL(timeout, unit);

			String packageName = VOTABLE.class.getPackage().getName();
			InputStream is;
			try {
				is = url.openStream();
			} catch (IOException e) {
				throw new RuntimeException("IOException while opening '" + url + "': " + e.getMessage(), e);
			}
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance( packageName );
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				JAXBElement<VOTABLE> doc = (JAXBElement<VOTABLE>)unmarshaller.unmarshal(new StreamSource(is), VOTABLE.class);
				return doc.getValue();
			} catch (JAXBException e) {
				throw new RuntimeException("JAXBException while reading " + url + ": " + e.getMessage(), e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					_LOGGER.warn("Unable to close input stream: " + e.getMessage(), e);
				}
			}
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
}
