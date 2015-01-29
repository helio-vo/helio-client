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
public interface QueryService extends HelioService {
	
	/**
	 * Execute a query on a HELIO query service.
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
	 * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
	 * @param from the table to query. Must not be null.
	 * @param maxrecords max number of records to display. 0 means all. defaults to 0.
	 * @param startindex position of first record to return. Starting at 0. 
	 * @param join ???
	 * @return returns a result object to access the returned data in a generic way.
	 * @throws JobExecutionException in case of any problem during execution of the query.
	 * @throws IllegalArgumentException if any of the submitted arguments is not valid.
	 */
	public HelioQueryResult query(
			String startTime, 
			String endTime, 
			String from, 
			Integer maxrecords, 
			Integer startindex, 
			String join)
	throws JobExecutionException, IllegalArgumentException;

	/**
	 * Execute a query on a HELIO query service.
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list.
     * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null or empty list. Must match the size of 'startTime'
	 * @param from the table to query. Must not be null or empty.
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
			Integer maxrecords, 
			Integer startindex, 
			String join)
				throws JobExecutionException, IllegalArgumentException;

	/**
	 * Execute a time query on a HELIO query service.
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
	 * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
	 * @param from the table to query. Must not be null.
	 * @param maxrecords max number of records to display
	 * @param startindex position of first record.
	 * @return returns an result object to access the returned data.
	 * @throws JobExecutionException in case of any problem during execution of the query.
	 * @throws IllegalArgumentException if any of the submitted arguments is not valid.
	 */
	public HelioQueryResult timeQuery(
			String startTime, 
			String endTime,
			String from,
			Integer maxrecords,
			Integer startindex) 
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

	/**
	 * Execute the query and get back the result object
	 * @return the result object used to access the result.
	 */
	public HelioQueryResult execute();
	
	/**
	 * The from property.
	 * @param from the value domain of the from property depends on the called service. Use BeanInfo to read the value domain
	 */
	public void setFrom(List<String> from);
	
	/**
	 * Get the from property
	 * @return the from property.
	 */
	public List<String> getFrom();
	
	/**
	 * Set the start time of the query.
	 * @param startTime the list of start times.
	 */
	public void setStartTime(List<String> startTime);
	
	/**
	 * Get the start time of the query.
	 * @return the start time.
	 */
	public List<String> getStartTime();

	/**
	 * Set the end time of the query.
	 * @param endTime the list of end times.
	 */
	public void setEndTime(List<String> endTime);
	
	/**
	 * Get the end time of the query.
	 * @return the end time.
	 */
	public List<String> getEndTime();
	
	/**
	 * Get the number of max records
	 * @return max records
	 */
	public Integer getMaxRecords();
	
	/**
	 * Set the number of max records to return.
	 * @param maxRecords the max records
	 */
	public void setMaxRecords(Integer maxRecords);
	
	/**
	 * Get the first index to start from
	 * @return the start index
	 */
	public Integer getStartIndex();
	
	/**
	 * Set the start index
	 * @param startIndex the start index
	 */
	public void setStartIndex(Integer startIndex);
    
    /**
     * Get the currently available where clauses. If a catalog does not support where clauses
     * the where clause will have an empty helioFieldDescriptor field.  
     * There will be one where clause per entry in the from property.
     * @return a list for where clauses, if the from property is set. An empty list otherwise.
     */
    public List<WhereClause> getWhereClauses();
    
    /**
     * Get a where clause by a given catalogName. If a catalog does not support where clauses
     * the where clause will have an empty helioFieldDescriptor field.  
     * There will be one where clause per entry in the from property.
     * @param catalogName the name of the catalog to look up. Equals to the "from" property value.
     * @return a list for where clauses, if the from property is set. An empty list otherwise.
     */
    public WhereClause getWhereClauseByCatalogName(String catalogName);

	/**
	 * Join two tables (expert feature)
	 * @return the joined tables
	 */
	public String getJoin();
	
	/**
	 * Join two tables (expert) 
	 * @param join the join list
	 */ 
	public void setJoin(String join);

	/**
	 * Get the type of this query.
	 * @return the query type
	 */
	public QueryType getQueryType();
	
	/**
	 * Set the query type of this service. Must not be null.
	 * @param queryType the query type.
	 */
    public void setQueryType(QueryType queryType);
}