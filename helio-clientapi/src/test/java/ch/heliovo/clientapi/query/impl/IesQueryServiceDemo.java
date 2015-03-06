package ch.heliovo.clientapi.query.impl;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.impl.DpasQueryServiceImpl;
import eu.heliovo.clientapi.query.impl.IesQueryServiceImpl;
import eu.heliovo.clientapi.query.local.LocalQueryServiceImpl;
import eu.heliovo.registryclient.HelioServiceName;

public class IesQueryServiceDemo {
	
	public static void main(String[] args) {
		IesQueryServiceImpl iesQueryService = getIesQueryService();
		HelioQueryResult result = iesQueryService.execute();
		System.out.println("xml saved in " + result.asURL().getPath());
	}
	
	public static IesQueryServiceImpl getIesQueryService() {
		IesQueryServiceImpl iesQueryService = new IesQueryServiceImpl();
		
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
		iesQueryService.setHecQueryService((LocalQueryServiceImpl) context.getBean("localHecQueryService"));
		iesQueryService.setIcsQueryService((LocalQueryServiceImpl) context.getBean("localIcsQueryService"));
		iesQueryService.setDpasQueryService((DpasQueryServiceImpl) context.getBean("dpasQueryService"));
		iesQueryService.setServiceName(HelioServiceName.IES);
		iesQueryService.setServiceVariant(null);
		
		return iesQueryService;
	}
	
}
