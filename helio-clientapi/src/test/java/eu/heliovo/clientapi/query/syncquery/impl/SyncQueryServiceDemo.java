package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.model.service.HelioServiceName;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.HelioQueryService;

public class SyncQueryServiceDemo {
	public static void main(String[] args) throws Exception {
		testSyncService(HelioServiceName.ICS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testSyncService(HelioServiceName.ILS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("trajectories"), null);
		testSyncService(HelioServiceName.DPAS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testSyncService(HelioServiceName.HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("goes_xray_flares"), null);
		testSyncService(HelioServiceName.UOC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("test"), null);
		testSyncService(HelioServiceName.MDES, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("ACE"), "SIR.DELTAT,100;SIR.DELTAV,/900;SIR.AVERAGETIME,600", null);
	}
	
	private static synchronized void testSyncService(HelioServiceName serviceName, List<String> startTime, List<String> endTime, List<String> from, String saveto) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
			SyncQueryServiceFactory queryServiceFactory = SyncQueryServiceFactory.getInstance();
			HelioQueryService queryService = queryServiceFactory.getSyncQueryService(serviceName.getName());
			HelioQueryResult result = queryService.timeQuery(startTime, endTime, from, 100, 0);

			System.out.println(result);
			if (result != null) {
				System.out.println("Phase: " + result.getPhase());
				System.out.println("Result VOTable: " + result.asVOTable());
				StringBuilder sb = new StringBuilder("User log: ");
				for (LogRecord logRecord : result.getUserLogs()) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(logRecord.getMessage());
				}
				System.out.println(sb.toString());
			}
			System.out.println();
		} catch (Exception e) {
			System.err.println("Error occured: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static synchronized void testSyncService(HelioServiceName serviceName, List<String> startTime, List<String> endTime, List<String> from, String where, String saveto) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
			SyncQueryServiceFactory queryServiceFactory = SyncQueryServiceFactory.getInstance();
			HelioQueryService queryService = queryServiceFactory.getSyncQueryService(serviceName.getName());
			HelioQueryResult result = queryService.query(startTime, endTime, from, where, 100, 0, null);
			
			System.out.println(result);
			if (result != null) {
				System.out.println("Phase: " + result.getPhase());
				System.out.println("Result URL: " + result.asURL());
				System.out.println("Result VOTable: " + result.asVOTable());
				StringBuilder sb = new StringBuilder("User log: ");
				for (LogRecord logRecord : result.getUserLogs()) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(logRecord.getMessage());
				}
				System.out.println(sb.toString());
			}System.out.println();
		} catch (Exception e) {
			System.err.println("Error occured: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
