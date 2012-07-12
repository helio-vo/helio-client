package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.Collections;
import java.util.List;

import eu.heliovo.clientapi.query.AbstractQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Abstract base implementation of the sync query service
 * @author MarcoSoldati
 *
 */
class SyncQueryServiceImpl extends AbstractQueryServiceImpl implements SyncQueryService {
    /**
     * The file util to use
     */
    private HelioFileUtil helioFileUtil;

    /**
	 * Default constructor.
	 */
	public SyncQueryServiceImpl() {
	}
	
    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.SYNC_QUERY_SERVICE;
    }

	@Override
	public void setAccessInterfaces(AccessInterface... accessInterfaces) {
	    for (AccessInterface accessInterface : accessInterfaces) {
	        if (!ServiceCapability.SYNC_QUERY_SERVICE.equals(accessInterface.getCapability())) {
	            throw new IllegalArgumentException("AccessInterface.Capability must be " + ServiceCapability.SYNC_QUERY_SERVICE + ", but is " + accessInterface.getCapability());
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
	public HelioQueryResult query(final List<String> startTime, final List<String> endTime,
			final List<String> from, final String where, final Integer maxrecords,
			final Integer startindex, final String join) {       
	    setStartTime(startTime);
	    setEndTime(endTime);
	    setFrom(from);
	    setWhere(where);
	    setMaxRecords(maxrecords);
	    setStartIndex(startindex);
	    setJoin(join);
	    SyncQueryDelegate queryDelegate = new SyncQueryDelegate();
	    queryDelegate.setHelioFileUtil(helioFileUtil);
	    setQueryDelegate(queryDelegate);
	    return execute();
	}
	
    @Override
	public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
		HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), maxrecords, startindex);
		return result;
	}
	
	@Override
	public HelioQueryResult timeQuery(final List<String> startTime, final List<String> endTime,
			final List<String> from, final Integer maxrecords, final Integer startindex) {
        setStartTime(startTime);
        setEndTime(endTime);
        setFrom(from);
        setMaxRecords(maxrecords);
        setStartIndex(startindex);
        setQueryDelegate(new SyncTimeQueryDelegate());
        return execute();
    }	
	
	public HelioFileUtil getHelioFileUtil() {
        return helioFileUtil;
    }
	
	public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
        this.helioFileUtil = helioFileUtil;
    }
	
}
