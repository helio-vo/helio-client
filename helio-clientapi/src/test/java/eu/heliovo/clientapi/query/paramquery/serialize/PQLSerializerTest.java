package eu.heliovo.clientapi.query.paramquery.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.utils.convert.HelioConversionService;

public class PQLSerializerTest {

	private FieldTypeFactory fieldTypeFactory;

	/**
	 * the PQL Serializer
	 */
	private PQLSerializer pqlSerializer;
	
	@Before	public void setUp() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        fieldTypeFactory = (FieldTypeFactory) context.getBean("fieldTypeFactory");

	    pqlSerializer= new PQLSerializer();
	    ConversionService service = new HelioConversionService();
	    pqlSerializer.setConversionService(service);
	    assertSame(service, pqlSerializer.getConversionService());
	}
	
	/**
	 * Test {@link PQLSerializer#getWhereClause(String, java.util.List)}
	 */
	@Test public void testSimpleQueries() {		
		List<HelioFieldQueryTerm<?>> paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		String where = pqlSerializer.getWhereClause("cat", paramQueryTerms );
		assertEquals("", where);
		
		HelioFieldDescriptor<String> field = new HelioFieldDescriptor<String>("string_test", "astring", "a description", fieldTypeFactory.getTypeByName("string"));
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
		assertEquals("cat.astring,a%20value", pqlSerializer.getWhereClause("cat", paramQueryTerms));
		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.LIKE, "likeval"));
		assertEquals("cat.astring,*likeval*", pqlSerializer.getWhereClause("cat", paramQueryTerms));
		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.BETWEEN, "a", "b"));
		assertEquals("cat.astring,a/b", pqlSerializer.getWhereClause("cat", paramQueryTerms));
		
		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.LARGER_EQUAL_THAN, "a"));
		assertEquals("cat.astring,a/", pqlSerializer.getWhereClause("cat", paramQueryTerms));

		paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
		paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.LESS_EQUAL_THAN, "a"));
		assertEquals("cat.astring,/a", pqlSerializer.getWhereClause("cat", paramQueryTerms));
	}
	
	@Test public void testOrQueries() {
	    List<HelioFieldQueryTerm<?>> paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
        
        HelioFieldDescriptor<String> field = new HelioFieldDescriptor<String>("string_test", "astring", "a description", fieldTypeFactory.getTypeByName("string"));
        paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
        paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "another value"));
        assertEquals("cat.astring,a%20value,another%20value", pqlSerializer.getWhereClause("cat", paramQueryTerms));
        
	}
	
	@Test public void testComplexQueries() {
	    List<HelioFieldQueryTerm<?>> paramQueryTerms = new ArrayList<HelioFieldQueryTerm<?>>();
	    
	    HelioFieldDescriptor<String> field = new HelioFieldDescriptor<String>("string_test", "astring", "a description", fieldTypeFactory.getTypeByName("string"));
	    paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "a value"));
	    
	    HelioFieldDescriptor<Date> dateField = new HelioFieldDescriptor<Date>("date_test", "adate", "a description", fieldTypeFactory.getTypeByName("dateTime"));
	    paramQueryTerms.add(new HelioFieldQueryTerm<Date>(dateField, Operator.BETWEEN, new Date(100000000000l), new Date(100001000000l)));
	    paramQueryTerms.add(new HelioFieldQueryTerm<Date>(dateField, Operator.BETWEEN, new Date(100002000000l), new Date(100003000000l)));
	    
	    paramQueryTerms.add(new HelioFieldQueryTerm<String>(field, Operator.EQUALS, "another value"));
	    
	    assertEquals("cat.astring,a%20value,another%20value;cat.adate,1973-03-03T09%3a46%3a40/1973-03-03T10%3a03%3a20,1973-03-03T10%3a20%3a00/1973-03-03T10%3a36%3a40", 
	            pqlSerializer.getWhereClause("cat", paramQueryTerms));
	    
	}
	
}
