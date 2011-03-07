package eu.heliovo.mockclient.query.longrunningquery;

import java.util.Arrays;
import java.util.logging.LogRecord;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

public class LongRunningQueryServiceDemo {

	
	public static void main(String[] args) throws Exception {
		//		URL url = new URL("http://localhost:8080/helio-queryservice-server/HelioLongQueryService?wsdl");

		//		URL url = new URL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ics-r3/HelioLongQueryService?wsdl");
		//URL url = new URL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ics-longrunning/HelioLongQueryService?wsdl");
		//URL url = new URL("http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService_MDES.wsdl");

//		BaseLongRunningQueryService baseLongRunningQueryService = new BaseLongRunningQueryService(url);
		
		LongRunningQueryService icsService = LongRunningQueryServiceFactory.getInstance().getIcsService();
		HelioQueryResult result = icsService.longTimeQuery(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), 100, 0, null);

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
	}
}
