package eu.heliovo.clientapi.query.local;

public interface TableNameFilter {

	/**
	 * Returns correct table name from given catalog name
	 * @author junia schoch at fhnw ch
	 */
	public String getTableName(String catalogName);
}
