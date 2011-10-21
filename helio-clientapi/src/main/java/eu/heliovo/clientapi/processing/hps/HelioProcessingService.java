package eu.heliovo.clientapi.processing.hps;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.processing.hps.HelioProcessingResult.ProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for context services.
 * @author MarcoSoldati
 * @param <T> object to access the return values of a call.
 *
 */
public interface HelioProcessingService<T extends ProcessingResultObject> extends HelioService {    
    /**
     * Submit the request to the remote service
     * @return This object can be used to access the ProcessingResult.
     * @throws JobExecutionException if anything fails while executing the remote job.
     */
    public HelioProcessingResult<T> execute() throws JobExecutionException;

}
