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

public class SQLSerializerTest {

	/**
	 * the SQL Serializer
	 */
	private SQLSerializer sqlSerializer;

	// utility structure for the tests.
	private List<HelioFieldQueryTerm<?>> paramQueryTerms;

	@Before
	public void setUp() {
		sqlSerializer = new SQLSerializer();
		ConversionService service = new HelioConversionService();
		sqlSerializer.setConversionService(service);
		assertSame(service, sqlSerializer.getConversionService());
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
	}

	@After
	public void tearDown() {
		paramQueryTerms = null;
	}

	@Test public void getWhereClause_empty_query() {		
		String where = sqlSerializer.getWhereClause("cat", paramQueryTerms );
		assertEquals("", where);
	}
		
	@Test public void getWhereClause_string_equals() {
		HelioFieldDescriptor<String> field = getStringFieldDescriptor();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
		assertEquals("cat.astring='a value'", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_like() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.LIKE, "likeval"));
		assertEquals("cat.astring LIKE '%likeval%'", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_between() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.BETWEEN, "a", "b"));
		assertEquals("cat.astring BETWEEN 'a' AND 'b'", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_gt() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.LARGER_EQUAL_THAN, "a"));
		assertEquals("cat.astring >= 'a'", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_string_lt() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(getStringFieldDescriptor(), Operator.LESS_EQUAL_THAN, "a"));
		assertEquals("cat.astring <= 'a'", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_or_query() {
        HelioFieldDescriptor<String> field = getStringFieldDescriptor();
        paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
        paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "another value"));
        assertEquals("(cat.astring='a value' OR cat.astring='another value')", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_number_equals() {
		HelioFieldDescriptor<Double> field = getDoubleFieldDescriptor();
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(field, Operator.EQUALS, 123.456));
		assertEquals("cat.anumber=123.456", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_number_like() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(getDoubleFieldDescriptor(), Operator.LIKE, 123.456));
		assertEquals("cat.anumber LIKE '%123.456%'", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_number_between() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(getDoubleFieldDescriptor(), Operator.BETWEEN, 123.456, 234.567));
		assertEquals("cat.anumber BETWEEN 123.456 AND 234.567", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_number_gt() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(getDoubleFieldDescriptor(), Operator.LARGER_EQUAL_THAN, 123.456));
		assertEquals("cat.anumber >= 123.456", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_number_lt() {		
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(getDoubleFieldDescriptor(), Operator.LESS_EQUAL_THAN, 123.456));
		assertEquals("cat.anumber <= 123.456", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void getWhereClause_number_or_query() {
		HelioFieldDescriptor<Double> field = getDoubleFieldDescriptor();
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(field, Operator.EQUALS, 123.456));
		paramQueryTerms.add(new HelioFieldQueryTerm<Double>(field, Operator.EQUALS, 234.567));
		assertEquals("(cat.anumber=123.456 OR cat.anumber=234.567)", sqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void testComplexQueries() {	    
	    HelioFieldDescriptor<String> field = getStringFieldDescriptor();
	    paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
	    
	    HelioFieldDescriptor<Date> dateField = getDateFieldDescriptor();
	    paramQueryTerms.add(new HelioFieldQueryTerm<Date>(dateField, Operator.BETWEEN, new Date(100000000000l), new Date(100001000000l)));
	    paramQueryTerms.add(new HelioFieldQueryTerm<Date>(dateField, Operator.BETWEEN, new Date(100002000000l), new Date(100003000000l)));
	    
	    paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "another value"));
	    
	    assertEquals("(cat.astring='a value' OR cat.astring='another value') AND (cat.adate BETWEEN '1973-03-03T09:46:40' AND '1973-03-03T10:03:20' OR cat.adate BETWEEN '1973-03-03T10:20:00' AND '1973-03-03T10:36:40')", 
	            sqlSerializer.getWhereClause("cat", paramQueryTerms));
	    
	}

	private HelioFieldDescriptor<String> getStringFieldDescriptor() {
		HelioFieldDescriptor<String> field = new HelioFieldDescriptor<String>("string_test", "astring", "a description", FieldType.STRING);
		return field;
	}
	
	private HelioFieldDescriptor<Double> getDoubleFieldDescriptor() {
		HelioFieldDescriptor<Double> field = new HelioFieldDescriptor<Double>("double_test", "anumber", "a description", FieldType.DOUBLE);
		return field;
	}
	
	private HelioFieldDescriptor<Date> getDateFieldDescriptor() {
		HelioFieldDescriptor<Date> dateField = new HelioFieldDescriptor<Date>("date_test", "adate", "a description", FieldType.STRING);
		return dateField;
	}
}
