package eu.heliovo.clientapi.query.local;

import java.util.Collections;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.query.HelioQueryResult;

/**
 * Demo class for {@link LocalHecQueryServiceImpl}
 * 
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryDemo {
	public static void main(String[] args) throws Exception {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
		LocalHecQueryServiceImpl service = (LocalHecQueryServiceImpl) context.getBean("localHecQueryService");

		//Params
		String startTime = "2003-08-03 08:00:00";
		String endTime = "2003-08-05 08:00:00";
		String from = "rhessi_hxr_flare";
		Integer maxRecords = 5;
		Integer startIndex = 0;

	
		service.setFrom(Collections.singletonList(from));
		service.setStartTime(Collections.singletonList(startTime));
		service.setEndTime(Collections.singletonList(endTime));
		service.setMaxRecords(maxRecords);
		service.setStartIndex(startIndex);
		service.setJoin(null);

		HelioQueryResult result = service.execute();
		System.out.println("xml saved in " + result.asURL().getPath());

	}

	private static HelioQueryResult simpleQuery(LocalHecQueryServiceImpl service) {
		String startTime = "2003-08-03 08:00:00";
		String endTime = "2003-08-05 08:00:00";
		String from = "hec__rhessi_hxr_flare";
		Integer maxRecords = 3;
		Integer startIndex = 1;

		// Service
		HelioQueryResult result = service.query(
				Collections.singletonList(startTime),
				Collections.singletonList(endTime),
				Collections.singletonList(from), maxRecords, startIndex, null);

		System.out.println("xml saved in " + result.asURL());
		return result;
	}
}
