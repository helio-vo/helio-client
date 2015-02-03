package eu.heliovo.clientapi.query.local;


public class IcsTableNameFilter implements TableNameFilter {

	@Override
	public String getTableName(String catalogName) {
		return "ics";
	}


}
