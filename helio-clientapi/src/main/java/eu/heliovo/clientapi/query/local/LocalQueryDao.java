package eu.heliovo.clientapi.query.local;

import uk.ac.starlink.table.StarTable;

/**
 * Data access object for locally installed hec database.
 * @author junia schoch at fhnw ch
 *
 */
public interface LocalQueryDao {
	
	/**
	 * Execute a query on a HELIO query service.
	 * @param select: select statement that contains the field names
	 * @param from: table name
	 * @param where: where statement
	 * @param startindex: named 'OFFSET' in postgresql
	 * @param maxrecords: named 'LIMIT' in postgresql
	 */
	public StarTable query(String select, String from, String where, 
			int startindex, int maxrecords);
}
