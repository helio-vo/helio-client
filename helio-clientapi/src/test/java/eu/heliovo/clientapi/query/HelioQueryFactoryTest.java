package eu.heliovo.clientapi.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.query.HelioQueryFactoryImpl.QueryBuilder;
import eu.heliovo.registryclient.HelioServiceName;

public class HelioQueryFactoryTest {
	
	private HelioQueryFactoryImpl helioQueryFactory;

	@Before
	public void setup() {
		WhereClauseFactoryBean whereClauseFactoryBean = new MockWhereClauseFactoryBean();
		helioQueryFactory = new HelioQueryFactoryImpl();
		helioQueryFactory.setWhereClauseFactoryBean(whereClauseFactoryBean);
		
	}

	@Test
	public void simple_query() {
		HelioQuery query = 
				helioQueryFactory
					.buildQuery(HelioServiceName.ICS, "instrument")
					.startTime("2014-01-01")
					.endTime("2014-01-03")
					.startIndex(100)
					.maxRecords(40)
					.build();
		
		assertEquals(HelioServiceName.ICS, query.getServiceName());
		assertEquals("instrument", query.getFrom());
		assertEquals("2014-01-01", query.getStartTime());
		assertEquals("2014-01-03", query.getEndTime());
		assertEquals(100, query.getStartIndex());
		assertEquals(40, query.getMaxRecords());
		assertNotNull(query.getWhereClause());
	} 
	
	@Test
	public void timerange_query() {
		HelioQuery query = 
				helioQueryFactory
					.buildQuery(HelioServiceName.ICS, "instrument")
					.timeRange("2014-01-01","2014-01-03")
					.build();
		assertEquals("2014-01-01", query.getStartTime());
		assertEquals("2014-01-03", query.getEndTime());
	} 
	
	@Test
	public void whereclause_query() {
		HelioQuery query = 
						helioQueryFactory
						.buildQuery(HelioServiceName.ICS, "instrument")
						.queryTerm("testId", Operator.LIKE, "abc")
						.build();
		List<HelioFieldQueryTerm<?>> queryTerms = query.getWhereClause().getQueryTerms();
		assertEquals(1, queryTerms.size());
		assertEquals("testId", queryTerms.get(0).getHelioFieldDescriptor().getId());
	} 
	
	@Test
	public void advanced_query() {
		QueryBuilder queryBuilder = helioQueryFactory.buildQuery(HelioServiceName.ICS, "instrument");
		WhereClause whereClause = queryBuilder.getWhereClause();		
		
		System.out.print(whereClause.getFieldDescriptors());
		
		@SuppressWarnings("unchecked")
		HelioFieldDescriptor<String> fieldDescriptor = (HelioFieldDescriptor<String>) whereClause.findFieldDescriptorById("testId");
		assertNotNull(fieldDescriptor);
		
		HelioFieldQueryTerm<String> queryTerm = new HelioFieldQueryTerm<String>(fieldDescriptor, Operator.LIKE, "abc");
		whereClause.setQueryTerm(fieldDescriptor, queryTerm);
		
		HelioQuery query = queryBuilder.build();
		
		assertEquals(HelioServiceName.ICS, query.getServiceName());
		assertEquals("instrument", query.getFrom());
		assertNull(query.getStartTime());
		assertNull(query.getEndTime());
		assertEquals(0, query.getStartIndex());
		assertEquals(0, query.getMaxRecords());
		assertNotNull(query.getWhereClause());
	}
}
