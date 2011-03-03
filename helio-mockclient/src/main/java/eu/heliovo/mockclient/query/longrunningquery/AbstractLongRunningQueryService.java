package eu.heliovo.mockclient.query.longrunningquery;


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

import org.apache.log4j.Logger;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService_Service;
import eu.helio_vo.xml.longqueryservice.v0.ResultInfo;
import eu.helio_vo.xml.longqueryservice.v0.Statusvalues;
import eu.heliovo.clientapi.help.annotation.TypeHelp;
import eu.heliovo.clientapi.help.annotation.Description;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.mockclient.util.AsyncCallUtils;
import eu.heliovo.shared.util.AssertUtil;


/**
 * Base implementation of the long running query. This wraps the client stub to access all
 * HELIO services that implement the long running query service. 
 * @author marco soldati at fhnw ch
 *
 */
abstract class AbstractLongRunningQueryService implements LongRunningQueryService {
	/**
	 * The logger instance
	 */
	private static final Logger _LOGGER = Logger.getLogger(AbstractLongRunningQueryService.class);
	
	/**
	 * Name of the long query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/LongQueryService/v0.1", "LongHelioQueryService");
//	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/LongQueryService/MDES/v0.1", "LongHelioQueryService");
//	private static final QName PORT_NAME = new QName("http://helio-vo.eu/xml/LongQueryService/MDES/v0.1", "LongHelioQueryServicePort");

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
	public AbstractLongRunningQueryService(URL wsdlLocation, String name, String description) {
		this.name = name;
		this.description = description;
		if (wsdlLocation == null) {
			throw new IllegalArgumentException("Argument 'wsdlLocation' must not be null.");
		}
		this.wsdlLocation = wsdlLocation;
		LongHelioQueryService_Service queryService = new LongHelioQueryService_Service(wsdlLocation, SERVICE_NAME);		
		port = queryService.getLongHelioQueryServicePort();
		if (_LOGGER.isDebugEnabled()) {
			_LOGGER.debug("Created " + port.getClass().getSimpleName() + " for " + wsdlLocation);
		}
	}

	@Override
	public HelioQueryResult longQuery(String starttime, String endtime, String from, String where, 
			Integer maxrecords, Integer startindex, String saveto) throws JobExecutionException {
		HelioQueryResult[] result = longQuery(Collections.singletonList(starttime), Collections.singletonList(endtime), Collections.singletonList(from), where, maxrecords, startindex, saveto);
		if (result.length != 1) {
			throw new RuntimeException("Internal Error: Expected result of size 1, but got: " + result.length);
		} 
		return result[0];
	}
	
	@Override
	public HelioQueryResult[] longQuery(final List<String> startTime, final List<String> endTime, final List<String> from, final String where,
			final Integer maxrecords, final Integer startindex, final String saveto) throws JobExecutionException {
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		AssertUtil.assertArgumentHasText(where, "where");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from);
		}
		
		String callId = wsdlLocation + "::longQuery";

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

		// wait for result
		List<ResultInfo> resultInfos = AsyncCallUtils.callAndWait(new Callable<List<ResultInfo>>() {
			@Override
			public List<ResultInfo> call() throws Exception {
				List<ResultInfo> result = port.longQuery(startTime, endTime, from, where, null, maxrecords, startindex, saveto);
				return result;
			}
		}, callId);
		
		if (resultInfos == null) {
			throw new JobExecutionException("Unspecified error occured on service provider. Got back null. ", new NullPointerException("Exception in remote implementation of long running query service: return value should never be null."));
		}
		
		// prepare return value
		HelioQueryResult [] helioQueryResults = new HelioQueryResult[resultInfos.size()];
		for (int i = 0; i < helioQueryResults.length; i++) {
			ResultInfo resultInfo = resultInfos.get(i);
			if (resultInfo == null) {
				throw new JobExecutionException("Unspecified error occured on service provider. Unexpected null value. ", new NullPointerException("Exception in remote implementation of long running query service: item " + i + " of resultSet is null."));
			}
			String id = resultInfo.getID();
			if (id == null) {
				throw new JobExecutionException("Unspecified error occured on service provider. Unexpected null value. ", new NullPointerException("Exception in remote implementation of long running query service: property 'ID' of item " + i + " of resultSet is null."));
			}			
			helioQueryResults[i] = new LongRunningQueryResultImpl(id, port, callId, new LogRecord(Level.INFO, message.toString()));
		}
		
		return helioQueryResults;
	}

	@Override
	public HelioQueryResult longTimeQuery(String starttime, String endtime, String from, Integer maxrecords,
			Integer startindex, String saveto) throws JobExecutionException {
		HelioQueryResult[] result = longTimeQuery(Collections.singletonList(starttime), Collections.singletonList(endtime), Collections.singletonList(from), maxrecords, startindex, saveto);
		if (result.length != 1) {
			throw new RuntimeException("Internal Error: Expected result of size 1, but got: " + result.length);
		} 
		return result[0];
	}

	@Override
	public HelioQueryResult[] longTimeQuery(final List<String> startTime, final List<String> endTime, final List<String> from,
			final Integer maxrecords, final Integer startindex, final String saveto) throws JobExecutionException {
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from);
		}

		String callId = wsdlLocation + "::longTimeQuery";
		
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
		
		// wait for result
		List<ResultInfo> resultInfos = AsyncCallUtils.callAndWait(new Callable<List<ResultInfo>>() {
			@Override
			public List<ResultInfo> call() throws Exception {
				List<ResultInfo> result = port.longTimeQuery(startTime, endTime, from, maxrecords, startindex, saveto);
				return result;
			}
		}, callId);
		
		if (resultInfos == null) {
			throw new JobExecutionException("Unspecified error occured on service provider. Got back null. ", new NullPointerException("Exception in remote implementation of long running query service: return value should never be null."));
		}
		
		// prepare return value
		HelioQueryResult [] helioQueryResults = new HelioQueryResult[resultInfos.size()];
		for (int i = 0; i < helioQueryResults.length; i++) {
			ResultInfo resultInfo = resultInfos.get(i);
			if (resultInfo == null) {
				throw new JobExecutionException("Unspecified error occured on service provider. Unexpected null value. ", new NullPointerException("Exception in remote implementation of long running query service: item " + i + " of resultSet is null."));
			}
			String id = resultInfo.getID();
			if (id == null) {
				throw new JobExecutionException("Unspecified error occured on service provider. Unexpected null value. ", new NullPointerException("Exception in remote implementation of long running query service: property 'ID' of item " + i + " of resultSet is null."));
			}			
			helioQueryResults[i] = new LongRunningQueryResultImpl(id, port, callId, new LogRecord(Level.INFO, message.toString()));
		}
		
		return helioQueryResults;
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
	 * Get the remote location of the WSDL that this service is connected to 
	 * @return the remote WSDL location.
	 */
	public URL getWsdlLocation() {
		return wsdlLocation;
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
		 * Default timeout is 30 seconds
		 */
		private final static long DEFAULT_TIMEOUT = 30000;

		/**
		 * Default number of polls.
		 */
		private static final int DEFAULT_POLL_RETRY = 10;

		/**
		 * Default timeout for the poll.
		 */
		private static final long DEFAULT_POLL_TIMEOUT = 200;

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
		 * number of retries when polling for a result.
		 */
		private int pollRetry = DEFAULT_POLL_RETRY;

		/**
		 * number of milliseconds to wait when polling.
		 */
		private long pollTimeout = DEFAULT_POLL_TIMEOUT;

		/**
		 * Create a Helio query result
		 * 
		 * @param id the assigned id
		 * @param port the port to be used to retrieve the results of this call.
		 * @param callId identifier of the called function. For user feedback.
		 * @param logRecords 
		 */
		LongRunningQueryResultImpl(String id, LongHelioQueryService port, String callId, LogRecord ... logRecords) {
			this.id = id;
			this.port = port;
			this.callId = callId;
			this.phase = Phase.QUEUED;
			Collections.addAll(this.userLogs, logRecords);
		}
			
		@Override
		public Phase getPhase() {
			// phase cannot change anymore if in any of these states.
			if (phase == Phase.COMPLETED || phase == Phase.ABORTED || phase == Phase.ERROR) {
				return phase;
			}
			
			List<ResultInfo> resultInfos = AsyncCallUtils.callAndWait(new Callable<List<ResultInfo>>() {
				@Override
				public List<ResultInfo> call() throws Exception {
					List<ResultInfo> result = port.getStatus(id);
					return result;
				}
			}, callId);
			
			if (resultInfos == null) {
				throw new JobExecutionException("Unspecified error occured on '" + callId  + "'. Got back null. ", new NullPointerException("Exception in remote implementation of long running query service: return value should never be null."));
			}
			
			if (resultInfos.size() != 1) {
				throw new JobExecutionException("Error on service provider '" + callId + "'. Size of result should be 1 but is " + resultInfos.size(), new RuntimeException("Exception in remote implementation of long running query service: return value should have size 1."));
			}
			
			Statusvalues status = resultInfos.get(0).getStatus();
			switch (status) {
			case COMPLETED:
				phase = Phase.COMPLETED;
				break;
			case ERROR:
				phase = Phase.ERROR;
				break;
			case PENDING: 
				phase = Phase.PENDING;
				break;
			default:
				phase = Phase.UNKNOWN;
				break;
			}
			
			String message = resultInfos.get(0).getStatusdescription();
			if (message != null) {
				userLogs.add(new LogRecord(Level.INFO, message));
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
			
			// poll the service status until it is not pending anymore.
			Phase currentPhase = getPhase();
			for (int i = 0; i < pollRetry && currentPhase == Phase.PENDING; i++, currentPhase = getPhase()) {
				try {
					Thread.sleep(pollTimeout);
				} catch (InterruptedException e) {
					// ignore this exception
				}
//				System.out.println(currentPhase);
			}
//			System.out.println(currentPhase);
			
			if (_LOGGER.isDebugEnabled()) {
				_LOGGER.debug("Current phase is " + currentPhase);
			}
			
			switch (currentPhase) {
			case ERROR:
				throw new JobExecutionException("An error occurred while executing the query. Check the logs for more information (call getUserLogs() on this object).");				
			case UNKNOWN:
				throw new JobExecutionException("Internal Error: an unknown error occurred while executing the query. Please report this issue.");
			case PENDING:
				throw new JobExecutionException("Remote Job did not terminate in a reasonable amount of time: " + formatSeconds(pollRetry * pollTimeout));
			case COMPLETED:
				// just continue
				break;
			default:
				throw new RuntimeException("Internal error: unexpected status occurred: " + currentPhase);
			}
			
			List<ResultInfo> resultInfos;
			try {
				resultInfos = AsyncCallUtils.callAndWait(new Callable<List<ResultInfo>>() {
					@Override
					public List<ResultInfo> call() throws Exception {
						List<ResultInfo> result = port.getResults(id);
						return result;
					}
				}, callId);
			} catch (JobExecutionException e) {
				phase = Phase.ERROR;
				// re throw
				throw e;
			}
			if (resultInfos == null) {
				throw new JobExecutionException(new RuntimeException("Exception on server side: ResultInfo must not be null."));
			}
			if (resultInfos.size() != 1) {
				throw new JobExecutionException(new RuntimeException("Exception on server side: ResultInfo should have size 1, but is: " + resultInfos.size()));				
			}
			
			String resultURI = resultInfos.get(0).getResultURI();
			if (resultURI == null) {
				throw new JobExecutionException(new RuntimeException("Exception on server side: Did not get back a valid URL."));
			}
			
			String message = resultInfos.get(0).getStatusdescription();
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
		 * Format milliseconds as string, converting large values to seconds.
		 * @param time time in Milliseconds 
		 * @return user friendly string representation of the time.
		 */
		private String formatSeconds(long time) {
			if (time > 999) {
				return String.format("%1$1.3fs", (double)time/1000.0);
			} 
			return String.format("%1$1dms", time);
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
		public LogRecord[] getUserLogs() {
			return userLogs.toArray(new LogRecord[userLogs.size()]);
		}

		@Override
		public int getExecutionDuration() {
			return 0;
		}

		@Override
		public Date getDestructionTime() {
			return null;
		}

		
		/**
		 * Create a LogRecord for a user message at a given time.
		 * @param level the log level. May be null.
		 * @param message the message. May be null.
		 * @param e any throwable. May be null.
		 * @return
		 */
		static LogRecord getMessage(Level level, String message,
				Throwable e) {
			LogRecord msg = new LogRecord(level, message);
			msg.setThrown(e);
			return msg;
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
