package eu.heliovo.clientapi.query.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ivoa.xml.votable.v1.DATA;
import net.ivoa.xml.votable.v1.RESOURCE;
import net.ivoa.xml.votable.v1.TABLE;
import net.ivoa.xml.votable.v1.TD;
import net.ivoa.xml.votable.v1.TR;
import net.ivoa.xml.votable.v1.VOTABLE;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.query.local.LocalQueryServiceImpl;

/**
 * IesQueryService combines hec, ics and dpas
 * @author junia schoch at fhnw ch
 *
 */
public class IesQueryServiceImpl extends AbstractServiceImpl{
	LocalQueryServiceImpl hecQueryService;
	LocalQueryServiceImpl icsQueryService;
	QueryService dpasQueryService;
	
	private transient WhereClauseFactoryBean whereClauseFactoryBean;
	private List<WhereClause> whereClauses = new ArrayList<WhereClause>();
	private transient Map<String, WhereClause> whereClauseCache = new HashMap<String, WhereClause>();
	
	private List<String> startTime = new ArrayList<String>();
	private List<String> endTime = new ArrayList<String>();
	private List<String> hecStartTime;
	private List<String> hecEndTime;
	private Integer maxRecords =  0;
	private Integer startIndex = 0;
	private List<String> fromHec;
	private List<String> fromIcs;
	private List<String> fromDpas = new ArrayList<String>();
	private List<String> instruments;
	private String join = null;
	
	/**
	 * Query to find Observation data by events and instruments.
	 * @param startTime, endTime timeRanges for the hec query
	 * @param fromHec names of the event tables
	 * @param fromIcs names of the chosen instruments, column "obsinst_key"
	 * @maxRecords maximum number of results
	 * @startIndex named 'OFFSET' in postgresql
	 */
	public HelioQueryResult query(List<String> startTime, List<String> endTime, List<String> fromHec, 
			List<String> fromIcs, List<String> instruments, int maxRecords, int startIndex) {
		this.fromHec = fromHec;
		this.fromIcs = fromIcs;
		this.maxRecords = maxRecords;
		this.startIndex = startIndex;
		this.hecStartTime = startTime;
		this.hecEndTime = endTime;
		this.instruments = instruments;
		
		HelioQueryResult result = getQueryResult();
		return result;
	}
	
	private HelioQueryResult getQueryResult() {
		// 1. set timeRanges from hecQueryService events
		VOTABLE hecVoTable = getHecVoTable();
		setTimeRangesFromVoTable(hecVoTable);
		
		// 2. get instruments for given time range
		VOTABLE icsVoTable = getIcsVoTable();
		List<String> icsInstruments = getIcsInstruments(icsVoTable);
		
		// 3. combine fromIcs and instruments to set fromDpas
		for(String s:icsInstruments) {
			if(instruments.contains(s)) {
				fromDpas.add(s);
			}
		}
		
		// 4. get observation data from dpasQueryService
		HelioQueryResult result = getObservationData();
		return result;
	}
	
	protected VOTABLE getHecVoTable() {
		// set hecQueryService query properties
		hecQueryService.setStartTime(hecStartTime);
		hecQueryService.setEndTime(hecEndTime);
		hecQueryService.setFrom(fromHec);
		hecQueryService.setMaxRecords(maxRecords);
		hecQueryService.setStartIndex(startIndex);
		hecQueryService.setJoin(join);
		
		HelioQueryResult result = hecQueryService.execute();
		VOTABLE voTable = result.asVOTable();
		return voTable;
	}
	
	protected void setTimeRangesFromVoTable(VOTABLE voTable) {
		//iterate over VOTABLE to get timeRanges
		if(voTable != null) {
			List<RESOURCE> resources = voTable.getRESOURCE();
			for (RESOURCE r:resources) {
				List<TABLE> tables = r.getTABLE();
				for(TABLE t:tables) {
					DATA data = t.getDATA();
					List<TR> trs = data.getTABLEDATA().getTR();
					for(TR tr:trs ) {
						List<TD> td = tr.getTD();
						if(td.size() >= 3) {
							startTime.add(td.get(1).getValue()); //td = column startTime
							endTime.add(td.get(3).getValue()); // td = column endTime
						}
					}
				}
			}
		}
	}
	
	protected List<String> getIcsInstruments(VOTABLE voTable) {
		List<String> instruments = new ArrayList<String>();
		
		//iterate over VOTABLE to get timeRanges
		if(voTable != null) {
			List<RESOURCE> resources = voTable.getRESOURCE();
			if(resources.size() > 0) {
				for (RESOURCE r:resources) {
					List<TABLE> tables = r.getTABLE();
					for(TABLE t:tables) {
						DATA data = t.getDATA();
						List<TR> trs = data.getTABLEDATA().getTR();
						for(TR tr:trs ) {
							List<TD> td = tr.getTD();
							instruments.add(td.get(2).getValue()); // obsinst_key
						}
					}
				}	
			}
		}
		
		return instruments;
	}
	
	protected VOTABLE getIcsVoTable() {
		icsQueryService.setStartTime(startTime);
		icsQueryService.setEndTime(endTime);
		icsQueryService.setFrom(fromIcs);
		icsQueryService.setMaxRecords(maxRecords);
		icsQueryService.setStartIndex(startIndex);
		icsQueryService.setJoin(join);
		
		HelioQueryResult result = icsQueryService.execute();
		VOTABLE voTable = result.asVOTable();
		
		return voTable;
	}
	
	private HelioQueryResult getObservationData() {
		dpasQueryService.setFrom(fromDpas);
		dpasQueryService.setStartTime(startTime);
		dpasQueryService.setEndTime(endTime);
		dpasQueryService.setMaxRecords(maxRecords);
		dpasQueryService.setStartIndex(startIndex);
		dpasQueryService.setJoin(join);
		
		HelioQueryResult result = dpasQueryService.execute();
		return result;
	}
	
	public LocalQueryServiceImpl getHecQueryService() {
		return hecQueryService;
	}
	
	public void setHecQueryService(LocalQueryServiceImpl hecQueryService) {
		this.hecQueryService = hecQueryService;
	}
	
	public LocalQueryServiceImpl getIcsQueryService() {
		return icsQueryService;
	}
	
	public void setIcsQueryService(LocalQueryServiceImpl icsQueryService) {
		this.icsQueryService = icsQueryService;
	}
	
	public QueryService getDpasQueryService() {
		return dpasQueryService;
	}
	
	public void setDpasQueryService(QueryService dpasQueryService) {
		this.dpasQueryService = dpasQueryService;
	}

	public List<String> getStartTime() {
		return startTime;
	}

	public List<String> getEndTime() {
		return endTime;
	}

	public Integer getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(Integer maxRecords) {
		this.maxRecords = maxRecords;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public List<String> getFromHec() {
		return fromHec;
	}

	public void setFromHec(List<String> fromHec) {
		this.fromHec = fromHec;
	}

	public List<String> getFromIcs() {
		return fromIcs;
	}

	public void setFromIcs(List<String> fromIcs) {
		this.fromIcs = fromIcs;
	}

	public List<String> getFromDpas() {
		return fromDpas;
	}

	public void setFromDpas(List<String> fromDpas) {
		this.fromDpas = fromDpas;
	}

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}
	
	public WhereClauseFactoryBean getWhereClauseFactoryBean() {
		return whereClauseFactoryBean;
	}

	public void setWhereClauseFactoryBean(
			WhereClauseFactoryBean whereClauseFactoryBean) {
		this.whereClauseFactoryBean = whereClauseFactoryBean;
	}
	
	public List<WhereClause> getWhereClauses() {
		return whereClauses;
	}

	public WhereClause getWhereClauseByCatalogName(String catalogName) {
		for (WhereClause clause : whereClauses) {
            if (catalogName.equals(clause.getCatalogName())) {
                return clause;
            }
        }
        return null;
	}
	
	/**
     * Update the where clauses, depending on the from property. 
     */
    private void updateWhereClauses(List<String> from) {
        // empty the where clauses
        whereClauses.clear();
        
        // and repopulate from cache or create new clause
        for (String catalogue : from) {
            WhereClause clause = whereClauseCache.get(catalogue);
            if (clause == null) {
                clause = whereClauseFactoryBean.createWhereClause(getServiceName(), catalogue);
                whereClauseCache.put(catalogue, clause);
            }
            whereClauses.add(clause);
        }
    }
}
