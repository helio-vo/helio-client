package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.impl.SyncServiceDescriptor;

public class SyncQueryServiceDemo {
	public static void main(String[] args) throws Exception {
		testSyncService(SyncServiceDescriptor.SYNC_ICS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testSyncService(SyncServiceDescriptor.SYNC_ILS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("trajectories"), null);
		testSyncService(SyncServiceDescriptor.SYNC_DPAS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testSyncService(SyncServiceDescriptor.SYNC_HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("goes_xray_flares"), null);
		testSyncService(SyncServiceDescriptor.SYNC_UOC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("test"), null);
		//testSyncService(SyncServiceDescriptor.SYNC_MDES, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("ACE"), "SIR.DELTAT,100;SIR.DELTAV,/900;SIR.AVERAGETIME,600", null);
	}
	
	private static synchronized void testSyncService(HelioServiceDescriptor serviceDescriptor, List<String> startTime, List<String> endTime, List<String> from, String saveto) {
		System.out.println("--------------------" + serviceDescriptor.getLabel() + "--------------------");
		try {
			SyncQueryServiceFactory queryServiceFactory = SyncQueryServiceFactory.getInstance();
			HelioQueryService queryService = queryServiceFactory.getSyncQueryService(serviceDescriptor);
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

	private static synchronized void testSyncService(HelioServiceDescriptor serviceDescriptor, List<String> startTime, List<String> endTime, List<String> from, String where, String saveto) {
		System.out.println("--------------------" + serviceDescriptor.getLabel() + "--------------------");
		try {
			SyncQueryServiceFactory queryServiceFactory = SyncQueryServiceFactory.getInstance();
			HelioQueryService queryService = queryServiceFactory.getSyncQueryService(serviceDescriptor);
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
