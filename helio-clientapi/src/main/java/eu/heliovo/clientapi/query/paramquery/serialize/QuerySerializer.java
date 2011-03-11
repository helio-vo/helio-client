package eu.heliovo.clientapi.query.paramquery.serialize;

import java.util.List;

import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;

/**
 * Serializer for the WHERE part of a query.
 * This interface is considered project internal. It should not be used from outside.   
 * @author marco soldati at fhnw ch
 *
 */
public interface QuerySerializer {

	/**
	 * Serialize the query term into a string. This
	 * @param paramQueryTerms the terms to serialize
	 * @return the where clause in String form.
	 * @throws QuerySerializationException if anything goes wrong.
	 */
	public String getWhereClause(List<ParamQueryTerm<?>> paramQueryTerms) throws QuerySerializationException;
}
