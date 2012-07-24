package eu.heliovo.clientapi.query.delegate;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.LogRecord;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService_Service;
import eu.heliovo.clientapi.query.BaseQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.util.AssertUtil;


/**
 * Delegate to handle a HQI long running query with all available parameters.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncQueryDelegate implements QueryDelegate {
	/**
	 * The logger instance
	 */
	static final Logger _LOGGER = Logger.getLogger(AsyncQueryDelegate.class);
	
	/**
	 * Name of the long query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/LongQueryService/v0.9", "LongHelioQueryService");

	/**
	 * Call timeout in ms.
	 */
    protected static final long CALL_TIMEOUT = AsyncCallUtils.DEFAULT_TIMEOUT;

	/**
	 * Default constructor.
	 */
	public AsyncQueryDelegate() {
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
    public HelioQueryResult callWebService(final BaseQueryServiceImpl queryService, final AccessInterface accessInterface)
            throws WebServiceException, JobExecutionException {
        long jobStartTime = System.currentTimeMillis();
        final LongHelioQueryService port = getPort(accessInterface);
        final String callId = getCallId(accessInterface);
        
        // wait for result
        String resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String resultId = port.longQuery(
                        queryService.getStartTime(), queryService.getEndTime(), 
                        queryService.getFrom(), queryService.getWhere(), 
                        null, // instruments is not used.  
                        queryService.getMaxRecords(), queryService.getStartIndex());
                return resultId;
            }
        }, callId, getCallTimout());

        if (resultId == null) {
            throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        }

        // prepare return value
        HelioQueryResult helioQueryResult = createQueryResult(
                resultId, port, callId, jobStartTime, queryService.getLogRecords());

        return helioQueryResult;
    }

    @Override
    public String getMethodName() {
        return "longQuery";
    }
	
    public String getCallId(AccessInterface accessInterface) {
        return accessInterface.getUrl() + "::" + getMethodName();
    }
    
    @Override
    public boolean supportsCapabilty(ServiceCapability capability) {
        return ServiceCapability.ASYNC_QUERY_SERVICE.equals(capability);
    }
		
	/**
	 * Timeout for the initial call.
	 * @return the initial call timeout
	 */
	protected long getCallTimout() {
	    return CALL_TIMEOUT;
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
