package eu.heliovo.clientapi.query.local;

import uk.ac.starlink.table.StarTable;

/**
 * Data access object for locally installed hec database.
 * @author junia schoch at fhnw ch
 *
 */
public interface LocalHecQueryDao {
	
	/**
	 * Execute a query on a HELIO query service.
	 * @param starttime the start date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[.SSS]]). Must not be null.
	 * @param endtime the end date and time of the query range. Expected format is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[.SSS]]). Must not be null.
	 * @param from the table to query. Must not be null.
	 * @param maxrecords max number of records to display. 0 means all. defaults to 0.
	 * @param startindex position of first record to return. Starting at 0. 
	 */
	public StarTable query(
			String startTime, 
			String endTime, 
			String from, 
			int startindex, 
			int maxrecords);
	
	/**
	 * Execute a query on a HELIO query service.
	 * @param whereClause sql statement. Must not be null.
	 * @param from the table to query. Must not be null.
	 * @param maxrecords max number of records to display. 0 means all. defaults to 0.
	 * @param startindex position of first record to return. Starting at 0. 
	 */
	public StarTable query(
			String whereClause, 
			String from,
			int startindex, 
			int maxrecords);
}
