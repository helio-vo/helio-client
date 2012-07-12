package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.List;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * 
 * @author MarcoSoldati
 *
 */
public class DesAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    
    /**
     * Create the DES query support.
     */
    public DesAsyncQueryServiceImpl() {
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
        
    /**
     * Increase call timeout for des.
     */
    @Override
    protected long getCallTimout() {
        return 60000;
    }
}