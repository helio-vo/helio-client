package eu.heliovo.clientapi.query.longrunningquery;

import java.util.List;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Interface for all HELIO web services that implement the LongRunningQueryInterface.
 * @author MarcoSoldati
 *
 */
public interface AsyncQueryService extends HelioQueryService {
	/**
	 * Execute a long running query on a HELIO query service.
	 * This is just a convenience method that delegates to {@link #query(List, List, List, String, Integer, Integer, String, String)}. 
	 */
    public HelioQueryResult query(
        String startTime,
        String endTime,
        String from,
        String where,
        Integer maxrecords,
        Integer startindex,
        String join, 
        String saveto) 
    		throws JobExecutionException, IllegalArgumentException;
    
    
    /**
     * Execute an asynchronous query on a HELIO query service 
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list. Must match the size of 'startTime'
	 * @param from the table to query. Must not be null or empty.
	 * @param where where clause of the query in PQL.
	 * @param maxrecords max number of records to display. 0 means all. defaults to 0.
	 * @param startindex position of first record to return. Starting at 0. 
	 * @param join ???
	 * @param saveto name of the subfolder where to store the result to. Must be a String of [a-zA-Z]. 
	 * @return returns a result object to access the returned data in a generic way.
	 * @throws JobExecutionException in case of any problem during execution of the query.
	 * @throws IllegalArgumentException if any of the submitted arguments is not valid.
     */
    public HelioQueryResult query(
    		List<String> starttime,
    		List<String> endtime,
    		List<String> from,
    		String where,
    		Integer maxrecords,
    		Integer startindex,
    		String join, 
    		String saveto) 
    			throws JobExecutionException, IllegalArgumentException;
    
    /**
     * This is a convenience method that delegates to {@link #timeQuery(List, List, List, Integer, Integer, String)}.
     * Submit a query to the service end point.
     */
    public HelioQueryResult timeQuery(
    		String starttime,
    		String endtime,
    		String from,
    		Integer maxrecords,
    		Integer startindex,
    		String saveto) 
    			throws JobExecutionException, IllegalArgumentException;

    /**
     * 
     * Submit a time query to a HELIO query service.
     * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list. Must match the size of 'startTime'
	 * @param from the list from which to select the data. 
     * @param maxrecords maximum number of records to return. 
     * @param startindex first index from which to return the data (this feature can be used for recieving many tables in small chunks).
     * @param saveto name of the subfolder where to store the result to. Must be a String of [a-zA-Z]. 
	 * @return returns a result object to access the returned data in a generic way.
	 * @throws JobExecutionException in case of any problem during execution of the query.
	 * @throws IllegalArgumentException if any of the submitted arguments is not valid.
     */
    public HelioQueryResult timeQuery(
        List<String> starttime,
        List<String> endtime,
        List<String> from,
        Integer maxrecords,
        Integer startindex,
        String saveto) 
    		throws JobExecutionException, IllegalArgumentException;
}
