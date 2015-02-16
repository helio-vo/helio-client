package eu.heliovo.clientapi.query.local;


public class HecTableNameFilter implements TableNameFilter {

	@Override
	public String getTableName(String catalogName) {
		return "hec2__" + catalogName;
	}


}
