package eu.heliovo.clientapi.query.local;

import java.util.Collections;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.query.HelioQueryResult;

/**
 * Demo class for {@link LocalHecQueryServiceImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryDemo {
	public static void main(String[] args) throws Exception {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-localquery-test.xml");
		LocalHecQueryServiceImpl service = (LocalHecQueryServiceImpl) context.getBean("localHecQueryService");
		
		String startTime = "2003-08-03 08:00:00";
		String endTime = "2003-08-05 08:00:00";
		String from = "hec__rhessi_hxr_flare";
		Integer maxRecords = 0;
		Integer startIndex = 0;
		
		//Service
		HelioQueryResult result = service.query(Collections.singletonList(startTime), 
				Collections.singletonList(endTime), Collections.singletonList(from), maxRecords, startIndex, null);
		
		System.out.println("xml saved in " + result.asURL());	
	}
}
