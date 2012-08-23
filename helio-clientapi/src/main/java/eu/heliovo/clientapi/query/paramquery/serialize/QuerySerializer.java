package eu.heliovo.clientapi.query.paramquery.serialize;

import java.util.List;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;

/**
 * Serialiser for the WHERE part of a query.
 * This interface is considered project internal. It should not be used from outside.   
 * @author marco soldati at fhnw ch
 *
 */
public interface QuerySerializer {

	/**
	 * Serialize the query term into a string. This
	 * @param catalogueName name of the catalogue for which this where statement is valid.
	 * @param paramQueryTerms the terms to serialize
	 * @return the where clause in String form.
	 * @throws QuerySerializationException if anything goes wrong.
	 */
	public String getWhereClause(String catalogueName, List<HelioFieldQueryTerm<?>> paramQueryTerms) throws QuerySerializationException;
}
