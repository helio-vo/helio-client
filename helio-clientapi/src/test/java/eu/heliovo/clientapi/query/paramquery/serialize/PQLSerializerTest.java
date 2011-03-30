package eu.heliovo.clientapi.query.paramquery.serialize;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;

public class PQLSerializerTest {

	private final FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();

	/**
	 * Test {@link PQLSerializer#getWhereClause(java.util.List)}
	 */
	@Test public void testPQLSerializer() {
		PQLSerializer serializer = new PQLSerializer();
		
		List<ParamQueryTerm<?>> paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		String where = serializer.getWhereClause(paramQueryTerms );
		assertEquals("", where);
		
		HelioField<String> field = new HelioField<String>("string_test", "astring", "a description", fieldTypeRegistry.getType("string"));
		paramQueryTerms.add(new ParamQueryTerm<String>(field, Operator.EQUALS, "a value"));
		assertEquals("astring=a%20value", serializer.getWhereClause(paramQueryTerms));
		
		
	}
}
