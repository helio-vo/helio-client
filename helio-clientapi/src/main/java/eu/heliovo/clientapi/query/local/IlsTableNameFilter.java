package eu.heliovo.clientapi.query.local;


public class IlsTableNameFilter implements TableNameFilter {

	@Override
	public String getTableName(String catalogName) {
		switch(catalogName) {
		case "trajectories":
			return "ils";
		default:
			return catalogName;
	}
	}


}
