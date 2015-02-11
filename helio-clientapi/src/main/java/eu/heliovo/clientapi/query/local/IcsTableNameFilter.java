package eu.heliovo.clientapi.query.local;


public class IcsTableNameFilter implements TableNameFilter {

	@Override
	public String getTableName(String catalogName) {
		switch(catalogName) {
			case "instrument":
				return "instrument_pat";
			default:
				return catalogName;
		}
	}


}
