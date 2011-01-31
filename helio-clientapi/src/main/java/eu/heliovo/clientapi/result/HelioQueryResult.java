package eu.heliovo.clientapi.result;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogRecord;

/**
 * Container object for a set of results. 
 */
public interface HelioQueryResult extends HelioWorkerService {

	/**
	 * Return the unique id of this result set. This id can be further used to query
	 * the system for meta information.
	 * @return the id. 
	 */
	public ResultId getResultId();
	
	/**
	 * Provide access to the result as {@link InputStream} that reads a VOTable.
	 * <p>It is up to the implementation to either download the file locally and
	 * then return an InputStream or to provide an input stream that directly accesses the data online.
	 * Therefore the client cannot make any assumption about the possible Exceptions
	 * thrown while retrieving the data.</p> 
	 * <p>The method will block until either a result is available or a timeout occurs.</p>
	 * @return the data as VOTable.
	 * @throws IOException in case of any IO error.
	 */
	public InputStream asVOTable() throws JobExecutionException;
	
	/**
	 * Provide access to the result through an {@link InputStream} that reads a VOTable.
	 * <p>The method will terminate after {@code timeout unit}s of inactivity.</p>
	 * @param timeout the maximum idle time to wait.
     * @param unit the time unit of the timeout argument
     * @return the data as XML VOTable stream.
     * @throws IOException in case of any IO error.
     * @throws CancellationException if the computation was canceled.
     * @throws ExecutionException if the computation threw an exception.
     * @throws InterruptedException if the current thread was interrupted while working.
     * @throws TimeoutException if the wait timed out.
	 */
	public InputStream asVOTable(long timeout, TimeUnit unit)    
    	throws JobExecutionException;
	

	
	/**
	 * Download the data as VOTable and covert it to the HELIO Object Model.
	 * @return the data as object model. TODO: change return type to object model root. 
	 */
	public Object asObjectModel();
	
	/**
	 * Download the data as VOTable and convert it to the HELIO Object Model.
	 * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
	 * @return the data as object model. TODO: change return type to object model root. 
     * @throws CancellationException if the computation was canceled
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException if the wait timed out 
	 */
	public Object asObjectModel(long timeout, TimeUnit unit)    
		throws CancellationException, InterruptedException, ExecutionException, TimeoutException;
	
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
