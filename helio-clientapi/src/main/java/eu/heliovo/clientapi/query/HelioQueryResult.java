package eu.heliovo.clientapi.query;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.workerservice.HelioWorkerServiceHandler;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

import net.ivoa.xml.votable.v1.VOTABLE;

/**
 * Container object for a set of results. 
 */
public interface HelioQueryResult extends HelioWorkerServiceHandler {

	/**
	 * Return the unique id of this result set. This id can be further used to query
	 * the system for meta information.
	 * @return the id. 
	 */
	//public ResultId getResultId();

	
	/**
	 * Provide access to the URL pointing to the result.
	 * <p>The method will block until either a result is available or a default timeout occurs. 
	 * The default timeout is specified by the implementation as it may vary between different types of services.</p>
	 * @return URL pointing to the data.
	 * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
	 */
	public URL asURL() throws JobExecutionException;

	/**
	 * Provide access to the URL pointing to the result.
	 * <p>The method will block until either a result is available or the specified timeout occurs. </p>
	 * @param timeout the maximum time to block for a result. 0 waits forever.
	 * @param unit the unit of the timeout value.
	 * @return URL pointing to the data.
	 * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
	 */
	public URL asURL(long timeout, TimeUnit unit) throws JobExecutionException;
	
	/**
	 * Provide access to the result as VOTable populated through JAXB.
	 * <p>The method will block until either a result is available or a timeout occurs.</p>
	 * @return the data as VOTable.
	 * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
	 */
	public VOTABLE asVOTable() throws JobExecutionException;
	
	/**
	 * Provide access to the result as VOTable populated through JAXB.
	 * <p>The method will block until either a result is available or a timeout occurs.</p>
	 * @param timeout the maximum time to block for a result. 0 waits forever.
	 * @param unit the unit of the timeout value.
	 * @return the data as VOTable.
	 * @throws JobExecutionException wrapper exception for all exception that may occur during execution of this job.
	 */
	public VOTABLE asVOTable(long timeout, TimeUnit unit)    
    	throws JobExecutionException;
		
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
