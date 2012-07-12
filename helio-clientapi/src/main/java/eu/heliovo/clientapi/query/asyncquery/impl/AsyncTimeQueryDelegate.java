package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.heliovo.clientapi.query.AbstractQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;

/**
 * Delegate to handle the asnyc time query
 * @author MarcoSoldati
 *
 */
class AsyncTimeQueryDelegate extends AsyncQueryDelegate {

    /**
     * Calls the actual web service
     * @param accessInterface the access interface to use
     * @param callId the id of the call for logging
     * @param jobStartTime the start time of the job
     * @param logRecords the log records.
     * @return the created query result
     */
    public HelioQueryResult callWebService(final AbstractQueryServiceImpl queryService, final AccessInterface accessInterface)
            throws WebServiceException, JobExecutionException {
        long jobStartTime = System.currentTimeMillis();
        final LongHelioQueryService port = getPort(accessInterface);
        final String callId = getCallId(accessInterface);

        String resultId = AsyncCallUtils.callAndWait(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String result = port.longTimeQuery(
                        queryService.getStartTime(), queryService.getEndTime(), 
                        queryService.getFrom(), 
                        queryService.getMaxRecords(), queryService.getStartIndex());
                return result;
            }
        }, callId, getCallTimout());

        if (resultId == null) {
            throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        }

        // prepare return value
        HelioQueryResult helioQueryResult = createQueryResult(resultId, port, callId, jobStartTime, queryService.getLogRecords());
        return helioQueryResult;
    }

    @Override
    public String getMethodName() {
        return "longTimeQuery";
    }
}
