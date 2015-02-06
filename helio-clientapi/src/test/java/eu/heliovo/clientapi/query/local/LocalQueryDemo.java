package eu.heliovo.clientapi.query.local;

import java.util.Collections;
import java.util.List;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Demo class for {@link LocalQueryServiceImpl}
 * 
 * @author junia schoch at fhnw ch
 *
 */
public class LocalQueryDemo {
	private static final String START_TIME = "2003-08-03 08:00:00";
	private static final String END_TIME = "2003-08-05 08:00:00";
	private static final String FROM_ICS = "instrument_pat";
	private static final String FROM_HEC = "rhessi_hxr_flare";
	private static final Integer MAXRECORDS = 5;
	private static final Integer STARTINDEX = 0;
	
	public static void main(String[] args) throws Exception {
		HelioQueryResult result = getQueryResult(HelioServiceName.ICS);
		System.out.println("xml saved in " + result.asURL().getPath());
	}
	
	private static HelioQueryResult getQueryResult(HelioServiceName serviceName) {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
		LocalQueryServiceImpl service = getQueryServiceBean(context, serviceName);
		service = setServiceProperties(service);
		service = setServiceQueryTerm(service);
		
		HelioQueryResult result = service.execute();
		return result;
	}
	
	private static LocalQueryServiceImpl getQueryServiceBean(GenericXmlApplicationContext context, HelioServiceName serviceName) {
		String beanName = "";
		switch(serviceName.getServiceName()) {
			case "HEC":
				beanName = "localHecQueryService";
				break;
			case "ICS":
				beanName = "localIcsQueryService";
				break;
		}
		
		LocalQueryServiceImpl service = (LocalQueryServiceImpl) context.getBean(beanName);
		return service;
	}
	
	private static LocalQueryServiceImpl setServiceProperties(LocalQueryServiceImpl service) {
		switch (service.getServiceName().getServiceName()) {
			case "HEC":
				service.setFrom(Collections.singletonList(FROM_HEC));
				break;
			case "ICS":
				service.setFrom(Collections.singletonList(FROM_ICS));
			break;
		}
		service.setStartTime(Collections.singletonList(START_TIME));
		service.setEndTime(Collections.singletonList(END_TIME));
		service.setMaxRecords(MAXRECORDS);
		service.setStartIndex(STARTINDEX);
		service.setJoin(null);
		return service;
	}
	
	private static LocalQueryServiceImpl setServiceQueryTerm(LocalQueryServiceImpl service) {
		List<WhereClause> whereClauses = service.getWhereClauses();
		WhereClause clause = whereClauses.get(0);
		
		switch(service.getServiceName().getServiceName()) {
			case "HEC":
				HelioFieldDescriptor<Long> totalCount = (HelioFieldDescriptor<Long>) clause.findFieldDescriptorById("total_count");
				clause.setQueryTerm(totalCount, new HelioFieldQueryTerm<Long>(totalCount, Operator.LARGER_EQUAL_THAN, 100l));
				break;
			case "ICS":
				HelioFieldDescriptor<String> instType = (HelioFieldDescriptor<String>) clause.findFieldDescriptorById("name");
				clause.setQueryTerm(instType, new HelioFieldQueryTerm<String>(instType, Operator.EQUALS, "CIS"));
				break;
		}
		
		return service;
	}
}
