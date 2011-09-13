package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.List;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * 
 * @author MarcoSoldati
 *
 */
class DesAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    
    public DesAsyncQueryServiceImpl(HelioServiceName name, String description, AccessInterface ... accessInterfaces) {
        super(name, description, accessInterfaces);
    }
    
    @Override
    public HelioQueryResult timeQuery(List<String> startTime, List<String> endTime, List<String> from, Integer maxrecords, Integer startindex) throws JobExecutionException,
            IllegalArgumentException {
        throw new UnsupportedOperationException("The DES does not support time queries. Use query() instead.");
    }
    
    @Override
    public HelioQueryResult timeQuery(List<String> startTime, List<String> endTime, List<String> from, Integer maxrecords, Integer startindex, String saveto)
            throws JobExecutionException {
        throw new UnsupportedOperationException("The DES does not support time queries. Use query() instead.");
    }
    
    @Override
    public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
        throw new UnsupportedOperationException("The DES does not support time queries. Use query() instead.");
    }
    
    @Override
    public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex, String saveto) throws JobExecutionException {
        throw new UnsupportedOperationException("The DES does not support time queries. Use query() instead.");
    }
}