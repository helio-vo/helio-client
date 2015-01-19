package eu.heliovo.clientapi.query.local;

import eu.heliovo.clientapi.query.HelioQueryResult;

/**
 * Query to a local hec database. The result is formatted as xml VOTable.
 * @author junia schoch at fhnw ch
 *
 */
public interface LocalHecQueryService {

	public HelioQueryResult query(
			String startTime, 
			String endTime, 
			String from,
			int startindex, 
			int maxrecords);
	
	public HelioQueryResult query(
			String whereClause, 
			String from,
			int startindex, 
			int maxrecords);
}
