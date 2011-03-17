package eu.heliovo.clientapi.query;

import java.util.List;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Super interface of the sync and async query service, such that they can be
 * used in the same way.
 * @author MarcoSoldati
 *
 */
public interface HelioQueryService extends HelioService {

	/**
	 * Execute a query on a HELIO query service.
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list. Must match the size of 'startTime'
	 * @param from the table to query. Must not be null or empty.
	 * @param where where clause of the query in PQL.
	 * @param maxrecords max number of records to display. 0 means all. defaults to 0.
	 * @param startindex position of first record to return. Starting at 0. 
	 * @param join ???
	 * @return returns a result object to access the returned data in a generic way.
	 * @throws JobExecutionException in case of any problem during execution of the query.
	 * @throws IllegalArgumentException if any of the submitted arguments is not valid.
	 */
	public HelioQueryResult query(
			List<String> startTime, 
			List<String> endTime, 
			List<String> from, 
			String where, 
			Integer maxrecords, 
			Integer startindex, 
			String join)
				throws JobExecutionException, IllegalArgumentException;

	/**
	 * Execute a time query on a HELIO query service.
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list. Must match the size of 'startTime'
	 * @param from the table to query. Must not be null or empty.
	 * @param maxrecords max number of records to display
	 * @param startindex position of first record.
	 * @return returns an result object to access the returned data.
	 * @throws JobExecutionException in case of any problem during execution of the query.
	 * @throws IllegalArgumentException if any of the submitted arguments is not valid.
	 */
	public HelioQueryResult timeQuery(
			List<String> startTime, 
			List<String> endTime,
			List<String> from,
			Integer maxrecords,
			Integer startindex) 
				throws JobExecutionException, IllegalArgumentException;

}