package eu.heliovo.clientapi.query.local;

import java.io.File;

/**
 * Query to a local hec database. The result is formatted as xml VOTable.
 * @author junia schoch at fhnw ch
 *
 */
public interface LocalHecQueryService {

	public File query(
			String startTime, 
			String endTime, 
			String from,
			int startindex, 
			int maxrecords);
	
	public File query(
			String whereClause, 
			String from,
			int startindex, 
			int maxrecords);
}
