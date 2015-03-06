package ch.heliovo.clientapi.query.impl;

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
		HelioQueryResult result = iesQueryService.execute();
		System.out.println("xml saved in " + result.asURL().getPath());
	}
	
	public static IesQueryServiceImpl getIesQueryService() {
		
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
		IesQueryServiceImpl iesQueryService = context.getBean(IesQueryServiceImpl.class);
//		iesQueryService.setHecQueryService((LocalQueryServiceImpl) context.getBean("localHecQueryService"));
//		iesQueryService.setIcsQueryService((LocalQueryServiceImpl) context.getBean("localIcsQueryService"));
//		iesQueryService.setServiceName(HelioServiceName.IES);
//		iesQueryService.setServiceVariant(null);
		HelioClient helioClient = context.getBean(HelioClient.class);
        QueryService dpasService = (QueryService)helioClient.getServiceInstance(HelioServiceName.DPAS, null, ServiceCapability.SYNC_QUERY_SERVICE);
		iesQueryService.setDpasQueryService(dpasService);

		return iesQueryService;
	}
	
}
