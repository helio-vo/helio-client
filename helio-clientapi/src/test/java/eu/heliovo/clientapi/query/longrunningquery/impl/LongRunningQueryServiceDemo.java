package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.LongRunningServiceDescriptor;

public class LongRunningQueryServiceDemo {
	public static void main(String[] args) throws Exception {
		testLongRunningService(LongRunningServiceDescriptor.ASYNC_ICS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testLongRunningService(LongRunningServiceDescriptor.ASYNC_ILS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("trajectories"), null);
		testLongRunningService(LongRunningServiceDescriptor.ASYNC_DPAS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testLongRunningService(LongRunningServiceDescriptor.ASYNC_HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("goes_xray_flares"), null);
		testLongRunningService(LongRunningServiceDescriptor.ASYNC_UOC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("test"), null);
		testLongRunningService(LongRunningServiceDescriptor.ASYNC_MDES, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("ACE"), "SIR.DELTAT,100;SIR.DELTAV,/900;SIR.AVERAGETIME,600", null);
	}
	
	private static synchronized void testLongRunningService(HelioServiceDescriptor serviceDescriptor, List<String> startTime, List<String> endTime, List<String> from, String saveto) {
		System.out.println("--------------------" + serviceDescriptor.getLabel() + "--------------------");
		try {
			LongRunningQueryServiceFactory queryServiceFactory = LongRunningQueryServiceFactory.getInstance();
			LongRunningQueryService queryService = queryServiceFactory.getLongRunningQueryService(serviceDescriptor);
			HelioQueryResult result = queryService.longTimeQuery(startTime, endTime, from, 100, 0, saveto);

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
			}
			System.out.println();
		} catch (Exception e) {
			System.err.println("Error occured: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static synchronized void testLongRunningService(HelioServiceDescriptor serviceDescriptor, List<String> startTime, List<String> endTime, List<String> from, String where, String saveto) {
		System.out.println("--------------------" + serviceDescriptor.getLabel() + "--------------------");
		try {
			LongRunningQueryServiceFactory queryServiceFactory = LongRunningQueryServiceFactory.getInstance();
			LongRunningQueryService queryService = queryServiceFactory.getLongRunningQueryService(serviceDescriptor);
			HelioQueryResult result = queryService.longQuery(startTime, endTime, from, where, 100, 0, saveto);
			
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
