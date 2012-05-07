package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.List;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * 
 * @author MarcoSoldati
 *
 */
public class DesAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    
    /**
     * Create the DES query support.
     * @param serviceName name of the service. Must be equal to {@link HelioServiceName#DES}
     * @param serviceVariant of the service variant.
     * @param description a description of the servcie from the registry
     * @param accessInterfaces the interfaces to use for this service.
     */
    public DesAsyncQueryServiceImpl(HelioServiceName serviceName, String serviceVariant) {
        super(serviceName, serviceVariant);
        AssertUtil.assertArgumentEquals(HelioServiceName.DES, serviceName,  "serviceName");
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
    
    /**
     * Increase call timeout for des.
     */
    @Override
    protected long getCallTimout() {
        return 60000;
    }
}