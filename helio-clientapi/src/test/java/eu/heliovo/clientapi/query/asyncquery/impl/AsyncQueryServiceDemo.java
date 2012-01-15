package eu.heliovo.clientapi.query.asyncquery.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.utils.DebugUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Query program for the async query.
 * @author MarcoSoldati
 *
 */
public class AsyncQueryServiceDemo {
    /**
     * The main method
     * @param args will be ignored
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
	    // init the system
	    HelioClient client = new HelioClient();
	    client.init();
	    
	    //client.getServiceInstance(HelioServiceName.ICS, ServiceCapability.ASYNC_QUERY_SERVICE, null);
	    
	    //DebugUtils.enableDump();
//	    long start = System.currentTimeMillis();
//	    testLongRunningService(HelioServiceName.DPAS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("SOHO__CDS"), "", (String)null, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE, HelioFileUtil.asURL("http://localhost:8080/helio-queryservice-server/HelioLongQueryService?wsdl")));
//	    long end = System.currentTimeMillis();
//	    System.out.println("query took " + (end - start) + "ms.");
//	    start = System.currentTimeMillis();
//	    testLongRunningService(HelioServiceName.DPAS, Arrays.asList("1890-10-20T20:30:56"), Arrays.asList("2009-10-20T20:30:56"), Arrays.asList("HXT"), "helio", (String)null, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE, HelioFileUtil.asURL("http://localhost:8080/helio-queryservice-server/HelioLongQueryService?wsdl")));
//	    end = System.currentTimeMillis();
//	    System.out.println("query took " + (end - start) + "ms.");
	    testLongRunningService(HelioServiceName.DPAS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("SOHO__CDS"), "", (String)null);
		testLongRunningService(HelioServiceName.ICS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), null);
		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("cactus_soho_flow"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("cactus_soho_flow"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("1900-01-01T00:00:00"), Arrays.asList("2020-12-31T00:00:00"), Arrays.asList("hec_catalogue"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("noaa_active_region_summary"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2007-03-01T00:00:00"), Arrays.asList("2007-03-31T00:00:00"), Arrays.asList("goes_xray_flare"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2007-03-01T00:00:00"), Arrays.asList("2007-03-31T00:00:00"), Arrays.asList("goes_xray_flare"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2007-03-01T00:00:00"), Arrays.asList("2007-03-31T00:00:00"), Arrays.asList("goes_xray_flare"), null);
//		testLongRunningService(HelioServiceName.HEC, Arrays.asList("2007-03-01T00:00:00"), Arrays.asList("2007-03-31T00:00:00"), Arrays.asList("goes_xray_flare"), null);
		testLongRunningService(HelioServiceName.ILS, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("trajectories"), null);
		testLongRunningService(HelioServiceName.ICS, Arrays.asList("2003-02-01T00:00:00", "2005-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2005-02-01T00:00:00"), Arrays.asList("instrument"), null);
		testLongRunningService(HelioServiceName.UOC, Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("test"), null);
	    testLongRunningService(HelioServiceName.DES, Arrays.asList("2007-07-10T12:00:00"), Arrays.asList("2007-07-11T12:00:00"), Arrays.asList("ACE"), "VAR,ACE:N:/16.0:1200.0:60.0", null, (AccessInterface[])null);	//DebugUtils.disableDump();
	    DebugUtils.disableDump();
	}
	
	/**
	 * Test the timeQuery method
	 * @param serviceName
	 * @param startTime
	 * @param endTime
	 * @param from
	 * @param saveto
	 * @param accessInterfaces
	 */
	private static synchronized void testLongRunningService(HelioServiceName serviceName, List<String> startTime, List<String> endTime, List<String> from, String saveto, AccessInterface... accessInterfaces) {
		System.out.println("--------------------" + serviceName.getServiceName() + "--------------------");
		try {
			AsyncQueryServiceFactory queryServiceFactory = AsyncQueryServiceFactory.getInstance();
			AsyncQueryService queryService = queryServiceFactory.getHelioService(serviceName, null, accessInterfaces);
			HelioQueryResult result = queryService.timeQuery(startTime, endTime, from, 100, 0, saveto);

			System.out.println(result);
			if (result != null) {
				System.out.println("Phase: " + result.getPhase());
				Thread.sleep(1000);
				System.out.println("Phase: " + result.getPhase());
				System.out.println("Result URL: " + result.asURL());
				System.out.println("Result VOTable: " + result.asVOTable());
				//System.out.println("Result VOTable as String: " + result.asString());
				StringBuilder sb = new StringBuilder("User log: ");
				for (LogRecord logRecord : result.getUserLogs()) {
					if (sb.length() > 0) {
						sb.append(",\n");
					}
					sb.append(logRecord.getMessage());
				}
				System.out.println(sb.toString());
			}
			System.out.println();
	         
            //StarTable[] table = STILUtils.read(result.asURL());

		} catch (Exception e) {
			System.err.println("Error occured: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Test the query methods
	 * @param serviceName
	 * @param startTime
	 * @param endTime
	 * @param from
	 * @param where
	 * @param saveto
	 * @param accessInterfaces
	 */
	private static synchronized void testLongRunningService(HelioServiceName serviceName, List<String> startTime, List<String> endTime, List<String> from, String where, String saveto, AccessInterface... accessInterfaces) {
		System.out.println("--------------------" + serviceName + "--------------------");
		try {
			AsyncQueryServiceFactory queryServiceFactory = AsyncQueryServiceFactory.getInstance();
			AsyncQueryService queryService = queryServiceFactory.getHelioService(serviceName, null, accessInterfaces);
			HelioQueryResult result = queryService.query(startTime, endTime, from, where, 100, 0, null, saveto);
			
			System.out.println(result);
			if (result != null) {
			    try {
    				System.out.println("Phase: " + result.getPhase());
    				System.out.println("Result URL: " + result.asURL(30, TimeUnit.SECONDS));
    				System.out.println("Result VOTable: " + result.asVOTable());
    				//System.out.println(result.asString());
			    } catch (JobExecutionException e) {
			        e.printStackTrace();
                }
			    // try to display logs
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
	
}
