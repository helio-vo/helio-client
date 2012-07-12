package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.Collections;
import java.util.List;

import eu.heliovo.clientapi.model.service.ServiceFactory;
import eu.heliovo.clientapi.query.AbstractQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Base implementation of the long running query. This wraps the client stub to access all
 * HELIO services that implement the long running query service. 
 * Instances of this class should be retrieved through the {@link ServiceFactory}.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncQueryServiceImpl extends AbstractQueryServiceImpl implements AsyncQueryService {

	/**
	 * Call timeout in ms.
	 */
    protected static final long CALL_TIMEOUT = AsyncCallUtils.DEFAULT_TIMEOUT;

	/**
	 * Default constructor.
	 */
	public AsyncQueryServiceImpl() {
	}
	
    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.ASYNC_QUERY_SERVICE;
    }

    @Override
    public void setAccessInterfaces(AccessInterface... accessInterfaces) {
        for (AccessInterface accessInterface : accessInterfaces) {
            if (!ServiceCapability.ASYNC_QUERY_SERVICE.equals(accessInterface.getCapability())) {
                throw new IllegalArgumentException("AccessInterface.Capability must be " + ServiceCapability.ASYNC_QUERY_SERVICE + ", but is " + accessInterface.getCapability());
            }
            if (!AccessInterfaceType.SOAP_SERVICE.equals(accessInterface.getInterfaceType())) {
                throw new IllegalArgumentException("AccessInterfaceType must be " + AccessInterfaceType.SOAP_SERVICE + ", but is " + accessInterface.getInterfaceType());
            }
        }
        super.setAccessInterfaces(accessInterfaces);
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
        setQueryDelegate(new AsyncQueryDelegate());
        setStartTime(startTime);
        setEndTime(endTime);
        setFrom(from);
        setWhere(where);
        setMaxRecords(maxrecords);
        setStartIndex(startindex);
        setJoin(join);
        return execute();
	}
	
    @Override
	public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
		HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), maxrecords, startindex);
		return result;
	}

	@Override
	public HelioQueryResult timeQuery(List<String> startTime, List<String> endTime, List<String> from, Integer maxrecords, Integer startindex) throws JobExecutionException,
			IllegalArgumentException {
        setQueryDelegate(new AsyncTimeQueryDelegate());
        setStartTime(startTime);
        setEndTime(endTime);
        setFrom(from);
        setMaxRecords(maxrecords);
        setStartIndex(startindex);
        return execute();
	}
			
	/**
	 * Timeout for the initial call.
	 * @return the initial call timeout
	 */
	protected long getCallTimout() {
	    return CALL_TIMEOUT;
	}
}
