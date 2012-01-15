package eu.heliovo.clientapi.processing;

import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.workerservice.HelioWorkerServiceHandler;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for a processing result.
 * @author MarcoSoldati
 * @param <T> the type of the processing result for a particular service implementation.
 */
public interface ProcessingResult <T extends ProcessingResultObject> extends HelioWorkerServiceHandler {
    /**
     * Provide access to an object holding the result.
     * <p>The method will block until either a result is available or a default timeout occurs. 
     * The default timeout is specified by the implementation as it may vary between different types of services.</p>
     * @return object holding the result data.
     * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
     */
    public T asResultObject() throws JobExecutionException;

    /**
     * Provide access to an object holding the result.
     * <p>The method will block until either a result is available or the specified timeout occurs. </p>
     * @param timeout the maximum time to block for a result. 0 waits forever.
     * @param unit the unit of the timeout value.
     * @return object holding the result data.
     * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
     */
    public T asResultObject(long timeout, TimeUnit unit) throws JobExecutionException;

    
    /**
     * Get log messages that are of particular interest for the end user.
     * This information is not intended for tracing and debugging and should be kept small and clean.
     * Typical User log messages are:
     * <ul>
     * <li>Too many results found. The result set has been limited to ${count} entries.</li>
     * <li>You do not have the permissions to access service ${service.name}.</li>
     * <li>Unable to connect to service ${service.name}. Failing over to ${alternative.name}.</li>
     * <li>...</li>
     * </ul>
     * @return the user log messages.
     */
    public LogRecord[] getUserLogs();
}
