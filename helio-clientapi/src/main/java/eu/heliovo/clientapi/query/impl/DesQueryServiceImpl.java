package eu.heliovo.clientapi.query.impl;

import java.util.List;

import eu.heliovo.clientapi.query.BaseQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryMethodType;
import eu.heliovo.clientapi.query.QueryType;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * 
 * @author MarcoSoldati
 *
 */
public class DesQueryServiceImpl extends BaseQueryServiceImpl {
    
    /**
     * Create the DES query support.
     */
    public DesQueryServiceImpl() {
        // override default of Query type.
        this.setQueryType(QueryType.ASYNC_QUERY);
    }
    
    @Override
    public void setQueryMethodType(QueryMethodType queryMethodType) {
        if (QueryMethodType.TIME_QUERY.equals(queryMethodType)) {
            throw new UnsupportedOperationException("The DES does not support time queries. Use FULL_QUERY() instead.");            
        } 
        super.setQueryMethodType(queryMethodType);
    }
    
    @Override
    public HelioQueryResult timeQuery(List<String> startTime, List<String> endTime, List<String> from, Integer maxrecords, Integer startindex) throws JobExecutionException,
            IllegalArgumentException {
        throw new UnsupportedOperationException("The DES does not support time queries. Use query() instead.");
    }
    
    @Override
    public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
        throw new UnsupportedOperationException("The DES does not support time queries. Use query() instead.");
    }
}