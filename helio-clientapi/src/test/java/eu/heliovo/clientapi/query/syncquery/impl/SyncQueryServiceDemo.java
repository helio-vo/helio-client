package eu.heliovo.clientapi.query.syncquery.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

public class SyncQueryServiceDemo {
	public static void main(String[] args) throws Exception {
	       // init the system
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
        
        HelioClient client = (HelioClient) context.getBean("helioClient");

	    
		testSyncService(client, HelioServiceName.ICS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testSyncService(client, HelioServiceName.ILS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("ils_trajectories"), null);
//        DebugUtils.enableDump();
		testSyncService(client, HelioServiceName.DPAS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
//       DebugUtils.disableDump();
		testSyncService(client, HelioServiceName.HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("goes_xray_flares"), null);
		testSyncService(client, HelioServiceName.UOC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("test"), null);
	}
	
	private static synchronized void testSyncService(HelioClient client, HelioServiceName serviceName, List<String> startTime, List<String> endTime, List<String> from, String saveto) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
		    QueryService queryService = (QueryService) client.getServiceInstance(serviceName, null, ServiceCapability.SYNC_QUERY_SERVICE);
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
			System.out.println(result.asString());
		} catch (Exception e) {
			System.err.println("Error occured: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
    private static synchronized void testSyncService(HelioClient client, HelioServiceName serviceName, List<String> startTime, List<String> endTime, List<String> from, String where, String saveto) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
            QueryService queryService = (QueryService) client.getServiceInstance(serviceName, null, ServiceCapability.SYNC_QUERY_SERVICE);
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
