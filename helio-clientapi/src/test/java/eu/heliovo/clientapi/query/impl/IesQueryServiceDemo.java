package eu.heliovo.clientapi.query.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.clientapi.query.impl.IesQueryServiceImpl;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

public class IesQueryServiceDemo {
	
	public static void main(String[] args) {
		IesQueryServiceImpl iesQueryService = getIesQueryService();
		
		List<String> startTime = Collections.singletonList("2002-02-14T00:00:00");
		List<String> endTime = Collections.singletonList("2002-02-15T00:00:00");
		List<String> fromHec = Collections.singletonList("rhessi_hxr_flare");
		List<String> instruments = Collections.singletonList("RHESSI__HESSI_HXR"); 
		List<String> fromIcs = Collections.singletonList("instrument_pat");
		
		HelioQueryResult result = iesQueryService.query(startTime, endTime, fromHec, fromIcs, instruments, 0, 0);
		
		System.out.println("xml saved in " + result.asURL().getPath());
	}
	
	public static IesQueryServiceImpl getIesQueryService() {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
		IesQueryServiceImpl iesQueryService = context.getBean(IesQueryServiceImpl.class);
		HelioClient helioClient = context.getBean(HelioClient.class);
        QueryService dpasService = (QueryService)helioClient.getServiceInstance(HelioServiceName.DPAS, null, ServiceCapability.SYNC_QUERY_SERVICE);
		iesQueryService.setDpasQueryService(dpasService);
		return iesQueryService;
	}
	
}
