package eu.heliovo.clientapi.query.paramquery.serialize;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;

public class PQLSerializerTest {

	/**
	 * Test {@link PQLSerializer#getWhereClause(java.util.List)}
	 */
	@Test public void testPQLSerializer() {
		PQLSerializer serializer = new PQLSerializer();
		
		List<ParamQueryTerm<?>> paramQueryTerms = new ArrayList<ParamQueryTerm<?>>();
		serializer.getWhereClause(paramQueryTerms );
	}
	
}
