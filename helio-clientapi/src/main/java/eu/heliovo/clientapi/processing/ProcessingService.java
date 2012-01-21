package eu.heliovo.clientapi.processing;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for context services.
 * @author MarcoSoldati
 * @param <T> object to access the return values of a call.
 *
 */
public interface ProcessingService<T extends ProcessingResultObject> extends HelioService {    
    /**
     * Submit the request to the remote service
     * @return This object can be used to access the ProcessingResult.
     * @throws JobExecutionException if anything fails while executing the remote job.
     */
    public ProcessingResult<T> execute() throws JobExecutionException;

}
