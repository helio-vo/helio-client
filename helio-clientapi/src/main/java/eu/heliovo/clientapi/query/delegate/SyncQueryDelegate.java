package eu.heliovo.clientapi.query.delegate;

import java.io.File;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.helio_vo.xml.queryservice.v0.HelioQueryServiceService;
import eu.heliovo.clientapi.query.BaseQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Abstract base implementation of the sync query service
 * @author MarcoSoldati
 *
 */
public class SyncQueryDelegate implements QueryDelegate {

	/**
	 * The logger instance
	 */
	private static final Logger _LOGGER = Logger.getLogger(SyncQueryDelegate.class);
    
	/**
	 * Name of the query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/QueryService/v0.1", "HelioQueryServiceService");
	
	/**
	 * The default time in ms to wait for a result.
	 */
	static final long DEFAULT_TIMEOUT = 600000;

	/**
	 * The timeout to wait for a result.
	 */
	private long timeout = DEFAULT_TIMEOUT;
	
	/**
	 * Hold reference to the file util.
	 */
	private transient HelioFileUtil helioFileUtil;
	
    /**
	 * Default constructor.
	 */
	public SyncQueryDelegate() {
	}
	
	/**
	 * Use JAXWS to create a new service port for a given WSDL location.
	 * @param accessInterface the service endpoint
	 * @return the port to access the service.
	 */
	protected HelioQueryService getPort(AccessInterface accessInterface) {
		AssertUtil.assertArgumentNotNull(accessInterface, "accessInterface");
		
		HelioQueryServiceService queryService = new HelioQueryServiceService(accessInterface.getUrl(), SERVICE_NAME);
		HelioQueryService port = queryService.getHelioQueryServicePort();
		if (_LOGGER.isDebugEnabled()) {
			_LOGGER.debug("Created " + port.getClass().getSimpleName() + " for " + accessInterface);
		}
		return port;
	}

	@Override
	public HelioQueryResult callWebService(final BaseQueryServiceImpl queryService, final AccessInterface accessInterface)
	        throws WebServiceException, JobExecutionException {
	    long jobStartTime = System.currentTimeMillis();
	    
        final HelioQueryService port = getPort(accessInterface);
        
        // wait for the result
        // FIXME: change to prevent marshalling the result
        VOTABLE votable = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
            @Override
            public VOTABLE call() throws Exception {
                VOTABLE result = port.query(
                        queryService.getStartTime(), queryService.getEndTime(), 
                        queryService.getFrom(), queryService.getWhere(), 
                        null, // instruments is not used.  
                        queryService.getMaxRecords(), queryService.getStartIndex(), queryService.getJoin());
                return result;
            }
        }, getCallId(accessInterface), timeout);
    
        if (votable == null) {
            throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        }
        int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
        HelioQueryResult result = createHelioSyncQueryResult(votable, executionDuration, queryService);

        return result;
	}

    @Override
	public String getMethodName() {
	    return "query";
	}
	
    public String getCallId(AccessInterface accessInterface) {
        return accessInterface.getUrl() + "::" + getMethodName();
    }
    
    @Override
    public boolean supportsCapabilty(ServiceCapability capability) {
        return ServiceCapability.SYNC_QUERY_SERVICE.equals(capability);
    }
    
    public long getCallTimeout() {
        return timeout;
    }
    
	/**
	 * Create the result object
	 * @param votable the votable wrapped by the object
	 * @param executionDuration the time it took to get the data
	 * @param queryService the query service object.
	 * @return a new result instance
	 */
    protected HelioQueryResult createHelioSyncQueryResult(VOTABLE votable,
            int executionDuration, BaseQueryServiceImpl queryService) {
        SyncQueryResultImpl result = new SyncQueryResultImpl(votable, executionDuration, queryService.getLogRecords());
        File tempDir = helioFileUtil.getHelioTempDir(queryService.getServiceName().getServiceName().toLowerCase() + "_sync");
        result.setTempDir(tempDir);
        result.setServiceName(queryService.getServiceName());
        return result;
    }
	
    /**
     * @return the helioFileUtil
     */
    @Required
    public HelioFileUtil getHelioFileUtil() {
        return helioFileUtil;
    }

    /**
     * @param helioFileUtil the helioFileUtil to set
     */
    public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
        this.helioFileUtil = helioFileUtil;
    }

}
