package eu.heliovo.clientapi.query.longrunningquery;

import java.util.List;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.workerservice.JobExecutionException;


public interface LongRunningQueryService extends HelioService {
	/**
	 * Execute a long running query on HELIO 
	 * @param starttime start time of the query
	 * @param endtime
	 * @param from
	 * @param where
	 * @param instrument
	 * @param maxrecords
	 * @param startindex
	 * @param saveto
	 * @return
	 */
    public HelioQueryResult longQuery(
        String starttime,
        String endtime,
        String from,
        String where,
        Integer maxrecords,
        Integer startindex,
        String saveto) throws JobExecutionException;
    
    
    /**
     * Execute a long running query on HELIO 
     * @param starttime
     * @param endtime
     * @param from
     * @param where
     * @param instrument
     * @param maxrecords
     * @param startindex
     * @param saveto
     * @return
     */
    public HelioQueryResult[] longQuery(
    		List<String> starttime,
    		List<String> endtime,
    		List<String> from,
    		String where,
    		Integer maxrecords,
    		Integer startindex,
    		String saveto) throws JobExecutionException;
    
    /**
     * Submit a query to the service end point.
     * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param from the list from which to select the data. The available lists can be queried from ...
     * @param maxrecords maximum number of records to return. How to get all????
     * @param startindex first index from which to return the data (this feature can be used for recieving many tables in small chunks).
     * @param saveto name of the subfolder where to store the result to. Must be a String of [a-zA-Z]. 
     * @return current status.			
     */
    public HelioQueryResult longTimeQuery(
    		String starttime,
    		String endtime,
    		String from,
    		Integer maxrecords,
    		Integer startindex,
    		String saveto) throws JobExecutionException;

    /**
     * 
     * Submit a query to the service end point.
     * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list. Size of startdate must match size of end date.
     * @param from the list from which to select the data. The available lists can be queried from ...
     * @param maxrecords maximum number of records to return. How to get all????
     * @param startindex first index from which to return the data (this feature can be used for recieving many tables in small chunks).
     * @param saveto name of the subfolder where to store the result to. Must be a String of [a-zA-Z]. 
     * @return current status. 
     */
    public HelioQueryResult[] longTimeQuery(
        List<String> starttime,
        List<String> endtime,
        List<String> from,
        Integer maxrecords,
        Integer startindex,
        String saveto) throws JobExecutionException;
}
