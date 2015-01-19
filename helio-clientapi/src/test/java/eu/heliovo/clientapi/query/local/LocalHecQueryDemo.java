package eu.heliovo.clientapi.query.local;

import java.io.File;

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
		LocalHecQueryService service = (LocalHecQueryServiceImpl) context.getBean("localHecQueryService");
		
		String startTime = "2003-08-03 08:00:00";
		String endTime = "2003-08-05 08:00:00";
		String from = "hec__rhessi_hxr_flare";
		
		//Service
		HelioQueryResult result = service.query(startTime, endTime, from, 0, 0);
		System.out.println("xml saved in " + result.asURL());
		//System.out.println(result.asString());
		
		//Dao
//		LocalHecQueryDaoImpl dao = (LocalHecQueryDaoImpl) context.getBean("localHecQueryDao");
//		StarTable starTable = dao.query(startTime, endTime, from);
//		System.out.println(Arrays.toString(starTable.getRow(0)));
	
	}
}
