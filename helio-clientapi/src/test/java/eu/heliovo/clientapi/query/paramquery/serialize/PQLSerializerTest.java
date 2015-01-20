package eu.heliovo.clientapi.query.paramquery.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.clientapi.utils.convert.HelioConversionService;

public class PQLSerializerTest {

	/**
	 * the PQL Serializer
	 */
	private PQLSerializer pqlSerializer;

	// utility structure for the tests.
	private List<HelioFieldQueryTerm<?>> paramQueryTerms;
	
	@Before	public void setUp() {
	    pqlSerializer= new PQLSerializer();
	    ConversionService service = new HelioConversionService();
	    pqlSerializer.setConversionService(service);
	    assertSame(service, pqlSerializer.getConversionService());
	    paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
	}


	@After
	public void tearDown() {
		paramQueryTerms = null;
	}

	@Test public void getWhereClause_empty_query() {		
		String where = pqlSerializer.getWhereClause("cat", paramQueryTerms );
		assertEquals("", where);
	}
		
	@Test public void getWhereClause_string_equals() {
		HelioFieldDescriptor<String> field = getStringFieldDescriptor();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
		assertEquals("cat.astring,a%20value", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_like() {		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.LIKE, "likeval"));
		assertEquals("cat.astring,*likeval*", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_between() {		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.BETWEEN, "a", "b"));
		assertEquals("cat.astring,a/b", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_gt() {		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.LARGER_EQUAL_THAN, "a"));
		assertEquals("cat.astring,a/", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_lt() {		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.LESS_EQUAL_THAN, "a"));
		assertEquals("cat.astring,/a", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_or_query() {
        HelioFieldDescriptor<String> field = getStringFieldDescriptor();
        paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
        paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "another value"));
        assertEquals("cat.astring,a%20value,another%20value", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void testComplexQueries() {	    
	    HelioFieldDescriptor<String> field = getStringFieldDescriptor();
	    paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
	    
	    HelioFieldDescriptor<Date> dateField = getDateFieldDescriptor();
	    paramQueryTerms.add(new HelioFieldQueryTerm<Date>(dateField, Operator.BETWEEN, new Date(100000000000l), new Date(100001000000l)));
	    paramQueryTerms.add(new HelioFieldQueryTerm<Date>(dateField, Operator.BETWEEN, new Date(100002000000l), new Date(100003000000l)));
	    
	    paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "another value"));
	    
	    assertEquals("cat.astring,a%20value,another%20value;cat.adate,1973-03-03T09%3a46%3a40/1973-03-03T10%3a03%3a20,1973-03-03T10%3a20%3a00/1973-03-03T10%3a36%3a40", 
	            pqlSerializer.getWhereClause("cat", paramQueryTerms));
	    
	}

	private HelioFieldDescriptor<String> getStringFieldDescriptor() {
		HelioFieldDescriptor<String> field = new HelioFieldDescriptor<String>("string_test", "astring", "a description", FieldType.STRING);
		return field;
	}
	
	private HelioFieldDescriptor<Date> getDateFieldDescriptor() {
		HelioFieldDescriptor<Date> dateField = new HelioFieldDescriptor<Date>("date_test", "adate", "a description", FieldType.STRING);
		return dateField;
	}
}
