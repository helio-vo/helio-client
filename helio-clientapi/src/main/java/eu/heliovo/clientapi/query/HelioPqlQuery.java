package eu.heliovo.clientapi.query;

import eu.heliovo.clientapi.result.HelioQueryResult;

/**
 * Perform advanced queries by issuing PQL statements ().
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioPqlQuery {
	
	/**
	 * 
	 * @param select
	 * @param from
	 * @param where
	 * @return
	 */
	public HelioQueryResult querySync(String select, String from, String where);
	
	/**
	 * 
	 * @param select
	 * @param from
	 * @param where
	 * @return
	 */
	public HelioQueryResult queryAsync(String select, String from, String where);
}
