package eu.heliovo.clientapi.query.paramquery.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import eu.heliovo.clientapi.model.field.FieldTypeFactory;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;

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
	    ConversionService service = new GenericConversionService();
	    pqlSerializer.setConversionService(service);
	    assertSame(service, pqlSerializer.getConversionService());
	}
	
	/**
	 * Test {@link PQLSerializer#getWhereClause(java.util.List)}
	 */
	@Test public void testSimpleQueries() {		
		List<ParamQueryTerm<?>> paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		String where = pqlSerializer.getWhereClause(paramQueryTerms );
		assertEquals("", where);
		
		HelioField<String> field = new HelioField<String>("string_test", "astring", "a description", fieldTypeFactory.getTypeByName("string"));
		paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.EQUALS, "a value"));
		assertEquals("astring=a%20value", pqlSerializer.getWhereClause(paramQueryTerms));
		
		paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.LIKE, "likeval"));
		assertEquals("astring=*likeval*", pqlSerializer.getWhereClause(paramQueryTerms));
		
		paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.BETWEEN, "a", "b"));
		assertEquals("astring=a/b", pqlSerializer.getWhereClause(paramQueryTerms));
		
		paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.LARGER_EQUAL_THAN, "a"));
		assertEquals("astring=a/", pqlSerializer.getWhereClause(paramQueryTerms));

		paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.LESS_EQUAL_THAN, "a"));
		assertEquals("astring=/a", pqlSerializer.getWhereClause(paramQueryTerms));
	}
	
	@Test public void testOrQueries() {
	    List<ParamQueryTerm<?>> paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
        
        HelioField<String> field = new HelioField<String>("string_test", "astring", "a description", fieldTypeFactory.getTypeByName("string"));
        paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.EQUALS, (Object[])new String[][] {new String[] {"a value", "another value"}}));
        assertEquals("astring=a%20value,another%20value", pqlSerializer.getWhereClause(paramQueryTerms));
        
	}
	
}
