package ch.heliovo.clientapi.query.impl;

import java.util.Collections;

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
		HelioQueryResult result = iesQueryService.query(Collections.singletonList("2002-02-14T00:00:00"), 
				Collections.singletonList("2002-02-15T00:00:00"), Collections.singletonList("rhessi_hxr_flare"), 
				Collections.singletonList("RHESSI__HESSI_HXR"), 0, 0);
		
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
