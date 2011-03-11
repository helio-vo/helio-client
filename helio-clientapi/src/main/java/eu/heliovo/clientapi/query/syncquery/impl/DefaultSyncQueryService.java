package eu.heliovo.clientapi.query.syncquery.impl;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.log4j.Logger;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.helio_vo.xml.queryservice.v0.HelioQueryServiceService;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Abstract base implementation of the sync query service
 * @author MarcoSoldati
 *
 */
class DefaultSyncQueryService implements SyncQueryService {

	/**
	 * The logger instance
	 */
	private static final Logger _LOGGER = Logger.getLogger(DefaultSyncQueryService.class);
	
	/**
	 * Name of the query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/QueryService/v0.1", "HelioQueryService");

	/**
	 * The default time in ms to wait for a result.
	 */
	private static final long DEFAULT_TIMEOUT = 15000;

	/**
	 * The location of the target WSDL file
	 */
	private final URL wsdlLocation;

	/**
	 * Store the port to access the query service. Apparently the port is thread-safe and can be reused.
	 */
	private final HelioQueryService port;
	
	/**
	 * Name of the service
	 */
	private final String name;
	
	/**
	 * Description of this service.
	 */
	private final String description;

	/**
	 * The timeout to wait for a result.
	 */
	private long timeout = DEFAULT_TIMEOUT;
	
	/**
	 * Create the connector and open the connection to the WSDL file.
	 * @param name the name of the service
	 * @param description a short text to describe the service
	 * @param wsdlLocation the location of the wsdl. Must not be null
	 */
	public DefaultSyncQueryService(URL wsdlLocation, String name, String description) {
		this(getPort(wsdlLocation), wsdlLocation, name, description);
	}
	/**
	 * Use JAXWS to create a new service port for a given WSDL location.
	 * @param wsdlLocation the service endpoint
	 * @return the port to access the service.
	 */
	private static HelioQueryService getPort(URL wsdlLocation) {
		AssertUtil.assertArgumentNotNull(wsdlLocation, "wsdlLocation");
		HelioQueryServiceService queryService = new HelioQueryServiceService(wsdlLocation, SERVICE_NAME);		
		HelioQueryService port = queryService.getHelioQueryServicePort();
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
	public DefaultSyncQueryService(HelioQueryService port, URL wsdlLocation, String name, String description) {
		AssertUtil.assertArgumentNotNull(port, "port");
		AssertUtil.assertArgumentNotNull(wsdlLocation, "wsdlLocation");
		AssertUtil.assertArgumentNotNull(name, "name");
		this.port = port;
		this.wsdlLocation = wsdlLocation;
		this.name = name;
		this.description = description;
	}

	
	@Override
	public VOTABLE query(final List<String> startTime, final List<String> endTime,
			final List<String> from, final String where, final String instrument,
			final Integer maxrecords, final Integer startindex, final String join) {
		//final long jobStartTime = System.currentTimeMillis();
		
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from);
		}
		
		String callId = wsdlLocation + "::syncQuery";

		StringBuilder message = new StringBuilder();
		message.append("Executing 'result=query(");
		message.append("startTime=").append(startTime);
		message.append(", ").append("endTime=").append(endTime);
		message.append(", ").append("from=").append(from);
		message.append(", ").append("where=").append(where);
		message.append(", ").append("instrument=").append(instrument);
		message.append(", ").append("maxrecords=").append(maxrecords);
		message.append(", ").append("startIndex=").append(startindex);
		message.append(", ").append("join=").append(join);
		message.append(")'");
		
		if (_LOGGER.isTraceEnabled()) {
			_LOGGER.trace(message.toString());
		}

		// wait for result
		VOTABLE result = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
			@Override
			public VOTABLE call() throws Exception {
				VOTABLE result = port.query(startTime, endTime, from, where, null, maxrecords, startindex, join);
				return result;
			}
		}, callId, timeout);
		
		if (result == null) {
			throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
		}
		
		return result;
	}

	@Override
	public VOTABLE timeQuery(final List<String> startTime, final List<String> endTime,
			final List<String> from, final Integer maxrecords, final Integer startindex) {
		//final long jobStartTime = System.currentTimeMillis();
		
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from);
		}
		
		String callId = wsdlLocation + "::syncTimeQuery";

		StringBuilder message = new StringBuilder();
		message.append("Executing 'result=timeQuery(");
		message.append("startTime=").append(startTime);
		message.append(", ").append("endTime=").append(endTime);
		message.append(", ").append("from=").append(from);
		message.append(", ").append("maxrecords=").append(maxrecords);
		message.append(", ").append("startIndex=").append(startindex);
		message.append(")'");
		
		if (_LOGGER.isTraceEnabled()) {
			_LOGGER.trace(message.toString());
		}

		// wait for result
		VOTABLE result = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
			@Override
			public VOTABLE call() throws Exception {
				VOTABLE result = port.timeQuery(startTime, endTime, from, maxrecords, startindex);
				return result;
			}
		}, callId, timeout);
		
		if (result == null) {
			throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
		}
		
		return result;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	
	@Override
	public String getDescription() {
		return description;
	}


}
