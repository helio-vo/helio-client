package eu.heliovo.clientapi.query;

import eu.heliovo.registryclient.HelioServiceName;

/**
 * Helper object to build up a HELIO query
 * 
 * @author MarcoSoldati
 * 
 */
public class HelioQuery {

	private final HelioServiceName serviceName; 
	private final String from;
	
	private String startTime;
	private String endTime;
	
	private WhereClause whereClause;
	
	private int startIndex;
	private int maxRecords;
	
	public HelioQuery(HelioServiceName serviceName, String from) {
		this.serviceName = serviceName;
		this.from = from;
	}
	
	void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}
	void setWhereClause(WhereClause whereClause) {
		this.whereClause = whereClause;
	}

	public HelioServiceName getServiceName() {
		return serviceName;
	}

	public String getFrom() {
		return from;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}
	
	public WhereClause getWhereClause() {
		return whereClause;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getMaxRecords() {
		return maxRecords;
	}
}