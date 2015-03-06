package eu.heliovo.clientapi.query.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ivoa.xml.votable.v1.DATA;
import net.ivoa.xml.votable.v1.RESOURCE;
import net.ivoa.xml.votable.v1.TABLE;
import net.ivoa.xml.votable.v1.TR;
import net.ivoa.xml.votable.v1.VOTABLE;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.local.LocalQueryServiceImpl;

/**
 * IesQueryService combines hec, ics and dpas
 * @author junia schoch at fhnw ch
 *
 */
public class IesQueryServiceImpl extends AbstractServiceImpl{
	LocalQueryServiceImpl hecQueryService;
	LocalQueryServiceImpl icsQueryService;
	DpasQueryServiceImpl dpasQueryService;
	
	public IesQueryServiceImpl() {
		
	}
	
	public HelioQueryResult execute() {
		// 1. get timeRanges from hecQueryService events
		HashMap<String, String> eventTimeRanges = getHecTimeRanges();
		
		// 2. get instruments form icsQueryService
		List<String> startTimes = new ArrayList<String>();
		List<String> endTimes = new ArrayList<String>();
		for(Map.Entry<String, String> entry:eventTimeRanges.entrySet()) {
			startTimes.add(entry.getKey());
			endTimes.add(entry.getValue());
		}
	 	
		List<String> instruments = getIcsInstruments(startTimes, endTimes);
		
		// 3. get observation data from dpasQueryService
		HelioQueryResult result = getObservationData(instruments);
		
		return null;
	}
	
	private HashMap<String, String> getHecTimeRanges() {
		HashMap<String, String> timeRangeMap = new HashMap<String, String>();
		VOTABLE voTable = getHecVoTable();
		
		//iterate over VOTABLE to get timeRanges
		List<RESOURCE> resources = voTable.getRESOURCE();
		for (RESOURCE r:resources) {
			List<TABLE> tables = r.getTABLE();
			for(TABLE t:tables) {
				DATA data = t.getDATA();
				List<TR> trs = data.getTABLEDATA().getTR();
				for(TR tr:trs ) {
					String startTime = tr.getTD().get(1).getValue(); //startTime
					String endTime = tr.getTD().get(3).getValue(); //endTime
					timeRangeMap.put(startTime, endTime);
				}
			}
		}
		
		return timeRangeMap;
	}
	
	private VOTABLE getHecVoTable() {
		// set hecQueryService query properties
		hecQueryService.setStartTime(Collections.singletonList("2003-08-03 08:00:00"));
		hecQueryService.setEndTime(Collections.singletonList("2003-08-05 08:00:00"));
		hecQueryService.setFrom(Collections.singletonList("rhessi_hxr_flare"));
		hecQueryService.setMaxRecords(0);
		hecQueryService.setStartIndex(0);
		hecQueryService.setJoin(null);
		
		HelioQueryResult result = hecQueryService.execute();
		VOTABLE voTable = result.asVOTable();
		return voTable;
	}
	
	private List<String> getIcsInstruments(List<String> startTimes, List<String> endTimes) {
		List<String> instruments = new ArrayList<String>();
		VOTABLE voTable = getIcsVoTable(startTimes, endTimes);
		
		//iterate over VOTABLE to get timeRanges
		List<RESOURCE> resources = voTable.getRESOURCE();
		for (RESOURCE r:resources) {
			List<TABLE> tables = r.getTABLE();
			for(TABLE t:tables) {
				DATA data = t.getDATA();
				List<TR> trs = data.getTABLEDATA().getTR();
				for(TR tr:trs ) {
					instruments.add(tr.getTD().get(2).getValue()); // obsinst_key
				}
			}
		}
		
		return instruments;
	}
	
	private VOTABLE getIcsVoTable(List<String> startTimes, List<String> endTimes) {
		icsQueryService.setStartTime(startTimes);
		icsQueryService.setEndTime(endTimes);
		icsQueryService.setFrom(Collections.singletonList("instrument_pat"));
		icsQueryService.setMaxRecords(0);
		icsQueryService.setStartIndex(0);
		icsQueryService.setJoin(null);
		
		HelioQueryResult result = icsQueryService.execute();
		VOTABLE voTable = result.asVOTable();
		
		return voTable;
	}
	
	private HelioQueryResult getObservationData(List<String> instruments) {
		dpasQueryService.setFrom(instruments);
		dpasQueryService.setStartTime(Collections.singletonList("2003-08-03 08:00:00"));
		dpasQueryService.setEndTime(Collections.singletonList("2015-03-31 08:00:00"));
		dpasQueryService.setMaxRecords(0);
		dpasQueryService.setJoin(null);
		
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
	
	public DpasQueryServiceImpl getDpasQueryService() {
		return dpasQueryService;
	}
	
	public void setDpasQueryService(DpasQueryServiceImpl dpasQueryService) {
		this.dpasQueryService = dpasQueryService;
	}
}
