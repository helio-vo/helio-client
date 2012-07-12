package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import net.ivoa.xml.votable.v1.VOTABLE;
import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.heliovo.clientapi.query.AbstractQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;

/**
 * The handler for sync timeQuery calls.
 * @author MarcoSoldati
 */
class SyncTimeQueryDelegate extends SyncQueryDelegate {
    
    public HelioQueryResult callWebService(final AbstractQueryServiceImpl queryService, final AccessInterface accessInterface)
            throws WebServiceException, JobExecutionException {
        long jobStartTime = System.currentTimeMillis();

        final HelioQueryService port = getPort(accessInterface);
        
        // wait for the result
        // FIXME: change to prevent marshalling the result
        VOTABLE votable = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
            @Override
            public VOTABLE call() throws Exception {
                VOTABLE result = port.timeQuery(
                        queryService.getStartTime(), queryService.getEndTime(), 
                        queryService.getFrom(), 
                        queryService.getMaxRecords(), queryService.getStartIndex());
                return result;
            }
        }, getCallId(accessInterface), getCallTimeout());
    
        if (votable == null) {
            throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        }
    
        int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
        HelioQueryResult result = createHelioSyncQueryResult(votable, executionDuration, queryService);
        return result;
    }

    @Override
    public String getMethodName() {
        return "timeQuery";
    }
}