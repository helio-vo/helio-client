package eu.heliovo.clientapi.query.asyncquery.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService_Service;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;


/**
 * Base implementation of the long running query. This wraps the client stub to access all
 * HELIO services that implement the long running query service. 
 * Instances of this class should be retrieved through the {@link AsyncQueryServiceFactory}.
 * @author marco soldati at fhnw ch
 *
 */
class AsyncQueryServiceImpl extends AbstractServiceImpl implements AsyncQueryService, HelioService {
	/**
	 * The logger instance
	 */
	static final Logger _LOGGER = Logger.getLogger(AsyncQueryServiceImpl.class);
	
	/**
	 * Name of the long query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/LongQueryService/v0.9", "LongHelioQueryService");

    /**
     * Store the port to access the long running query service. Apparently the port is thread-safe and can be reused.
     */
    //protected LongHelioQueryService currentPort;
    
    /**
     * store the currently active access interface
     */
    //protected AccessInterface currentAccessInterface;

	/**
	 * Create a client stub to a specific {@link AccessInterface}. 
	 * @param name the name of the service
	 * @param serviceVariant TODO
	 * @param description a short text to describe the service
	 * @param accessInterfaces concrete implementation of an AccessInterface. Must not be null
	 */
	public AsyncQueryServiceImpl(HelioServiceName name, String serviceVariant, String description, AccessInterface ... accessInterfaces) {
	    super(name, null, description, accessInterfaces);
	}

	/**
	 * Get the best access interface.
	 * @return the best known access interface
	 */
	protected AccessInterface getBestAccessInterface() {
        AccessInterface bestAccessInterface = loadBalancer.getBestEndPoint(accessInterfaces);
        return bestAccessInterface;
    }

	/**
	 * Use JAXWS to create a new service port for a given set of WSDL locations.
	 * @param accessInterface the service endpoint
	 * @return the port to access the service.
	 */
	protected LongHelioQueryService getPort(AccessInterface accessInterface) {
	    AssertUtil.assertArgumentNotNull(accessInterface, "accessInterface");
	    
        LongHelioQueryService_Service queryService = new LongHelioQueryService_Service(accessInterface.getUrl(), SERVICE_NAME);		
        LongHelioQueryService port = queryService.getLongHelioQueryServicePort();
        if (_LOGGER.isDebugEnabled()) {
            _LOGGER.debug("Created " + port.getClass().getSimpleName() + " for " + accessInterface.getUrl());
        }
	    return port;
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

		Set<AccessInterface> triedInterfaces = new HashSet<AccessInterface>();
		
		while (true) {
    		// get the end point
    		final AccessInterface bestAccessInterface = getBestAccessInterface();
    		final LongHelioQueryService port = getPort(bestAccessInterface);
    		if (!triedInterfaces.add(bestAccessInterface)) {
    		    throw new JobExecutionException("All registered remote services are unavailable. Tried to access: " + triedInterfaces.toString());
    		};
    		
    		// log the call.
    		String callId = bestAccessInterface.getUrl() + "::longQuery";
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
            
            _LOGGER.info(message.toString());            
            logRecords.add(new LogRecord(Level.INFO, message.toString()));

    		// do the call
    		try {
    		    // wait for result
    		    String resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
    		        @Override
    		        public String call() throws Exception {
    		            long start = System.currentTimeMillis(); 
    		            String resultId = port.longQuery(startTime, endTime, from, where, null, maxrecords, startindex);
    		            loadBalancer.updateAccessTime(bestAccessInterface, System.currentTimeMillis() - start);
    		            return resultId;
    		        }
    		    }, callId);

    		    if (resultId == null) {
    		        throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
    		    }

    		    // prepare return value
    		    HelioQueryResult helioQueryResult = createQueryResult(resultId, port, callId, jobStartTime, logRecords);

    		    return helioQueryResult;
    		} catch (JobExecutionException e) { // handle timeout
    		    if (e.getCause() instanceof TimeoutException) {
    		        String msg = "Timeout occurred. Trying to failover.";
    		        logRecords.add(new LogRecord(Level.INFO, msg));
    		        _LOGGER.info(msg);
    		        loadBalancer.updateAccessTime(bestAccessInterface, -1);    		        
    		    } else {
    		        throw e;
    		    }
    		}
		}
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

        Set<AccessInterface> triedInterfaces = new HashSet<AccessInterface>();

		while (true) {
	          // get the end point
            final AccessInterface bestAccessInterface = getBestAccessInterface();
            final LongHelioQueryService port = getPort(bestAccessInterface);
            if (!triedInterfaces.add(bestAccessInterface)) {
                throw new JobExecutionException("All registered service instances are unavailable. Tried to access: " + triedInterfaces.toString());
            };

    		String callId = bestAccessInterface.getUrl() + "::longTimeQuery";
    		logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));
    		
    		{
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
    		}
    		
    		// wait for result
    		String resultId = null;
    		try {
        		resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
        			@Override
        			public String call() throws Exception {
        			    long start = System.currentTimeMillis(); 
        				String result = port.longTimeQuery(startTime, endTime, from, maxrecords, startindex);
        				loadBalancer.updateAccessTime(bestAccessInterface, System.currentTimeMillis() - start);
        				return result;
        			}
        		}, callId);

        		if (resultId == null) {
        		    throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        		}
        		
        		// prepare return value
        		HelioQueryResult helioQueryResult = createQueryResult(resultId, port, callId, jobStartTime, logRecords);
        		return helioQueryResult;
        		
    		} catch (JobExecutionException e) { // handle timeout
    		    if (e.getCause() instanceof TimeoutException) {
    		        String msg = "Timeout occurred. Trying to failover.";
    		        logRecords.add(new LogRecord(Level.INFO, msg));
    		        _LOGGER.info(msg);
    		        loadBalancer.updateAccessTime(bestAccessInterface, -1);
    		    } else {
    		        throw e;
    		    }
            }
		}
		
	}
	
	/**
	 * Create a new HelioQueryResult. Subclasses may overwrite this method to define their own HelioQueryResult.
	 * @param resultId the result id.
	 * @param currentPort the currently active port.
	 * @param callId a string to identify the call.
	 * @param jobStartTime when did the job start.
	 * @param logRecords list of log records to append further information.
	 * @return a new instance of a HelioQueryResult.
	 */
	protected HelioQueryResult createQueryResult(String resultId, LongHelioQueryService currentPort, String callId, long jobStartTime, List<LogRecord> logRecords) {
	    return new AsyncQueryResultImpl(resultId, currentPort, callId, jobStartTime, logRecords);
	}
}
