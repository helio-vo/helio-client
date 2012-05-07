package eu.heliovo.clientapi.processing;

import java.net.URL;
import java.util.List;

import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Interface to access the output values of a processing result.
 * In its most generic form the interface provides a single getter method for all results. 
 * More specialized implementations can provide convenience getters for well-known output parameters.
 * @author MarcoSoldati
 *
 */
public interface HelioProcessingServiceResultObject {
    /**
     * Get the names of the output fields.
     * @return the output fields.
     * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
     */
    public List<String> getOutputNames() throws JobExecutionException;
    
    /**
     * Get the value of a specify output
     * @param outputName the output name
     * @return the output value with the given name, which could be an {@link URL}, a {@link Number} or a {@link String}.
     * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
     */
    public Object getOutput(String outputName) throws JobExecutionException;
}