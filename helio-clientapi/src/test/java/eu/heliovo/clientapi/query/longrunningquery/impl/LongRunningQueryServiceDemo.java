package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.longrunningquery.AsyncQueryService;
import eu.heliovo.clientapi.utils.DebugUtils;

public class LongRunningQueryServiceDemo {
	public static void main(String[] args) throws Exception {
		testLongRunningService("ICS", Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testLongRunningService("ILS", Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("trajectories"), null);
		testLongRunningService("DPAS", Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("SOHO__CDS"), null);
		DebugUtils.enableDump();
		testLongRunningService("ICS", Arrays.asList("2003-02-01T00:00:00", "2005-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2005-02-01T00:00:00"), Arrays.asList("instrument"), null);
		DebugUtils.disableDump();
		testLongRunningService("UOC", Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("test"), null);
		testLongRunningService("MDES", Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("ACE"), "SIR.DELTAT,100;SIR.DELTAV,/900;SIR.AVERAGETIME,600", null);
	}
	
	private static synchronized void testLongRunningService(String serviceName, List<String> startTime, List<String> endTime, List<String> from, String saveto) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
			AsyncQueryServiceFactory queryServiceFactory = AsyncQueryServiceFactory.getInstance();
			AsyncQueryService queryService = queryServiceFactory.getAsyncQueryService(serviceName);
			HelioQueryResult result = queryService.timeQuery(startTime, endTime, from, 100, 0, saveto);

			System.out.println(result);
			if (result != null) {
				System.out.println("Phase: " + result.getPhase());
				Thread.sleep(1000);
				System.out.println("Phase: " + result.getPhase());
				System.out.println("Result URL: " + result.asURL());
				System.out.println("Result VOTable: " + result.asVOTable());
				System.out.println("Result VOTable as String: " + result.asString());
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

	private static synchronized void testLongRunningService(String serviceName, List<String> startTime, List<String> endTime, List<String> from, String where, String saveto) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
			AsyncQueryServiceFactory queryServiceFactory = AsyncQueryServiceFactory.getInstance();
			AsyncQueryService queryService = queryServiceFactory.getAsyncQueryService(serviceName);
			HelioQueryResult result = queryService.query(startTime, endTime, from, where, 100, 0, null, saveto);
			
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
