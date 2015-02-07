package eu.heliovo.clientapi.query;

import eu.heliovo.clientapi.query.HelioQueryFactoryImpl.QueryBuilder;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Factory to build up a HELIO query for a given service.
 * The factory returns a QueryBuilder object which can be used to create the actual query object.
 * There are two ways of using it: 
 * <ul>
 *   <li>
 *     <strong>Simple Query: </strong>
 *     <pre>
 *     HelioQuery query = helioQueryFactory.buildQuery(ICS, "instrument").timeRange("2011-01-01','2011-01-02).build();
 *     </pre>
 *   </li>
 *   <li>
 *     <strong>Advanced Query: </strong>
 *     QueryBuilder queryBuilder = helioQueryFactory.buildQuery(ICS, "instrument");
 *     WhereClause whereClause = queryBuilder.getWhereClause();
 *     FieldDescriptor nameDescriptor = whereClause.findFieldDescriptorById("name");
 *     HelioFieldQueryTerm<?> queryTerms = new HelioFieldQueryTerm(nameDescriptor, Operator.LIKE, "me");
 *     whereClause.setQueryTerm(nameDescriptor);
 *     HelioQuery query = queryBuilder.build();
 *     <pre>
 *     
 *     </pre>
 *   </li>
 * </ul>
 * 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioQueryFactory {

	/**
	 * Get a query builder for building a query.
	 * @param serviceName the name of the service to use.
	 * @param from the name of the catalog to query.
	 * @return the builder, use {@link QueryBuilder#build()} to get the query object.
	 */
	public abstract QueryBuilder buildQuery(HelioServiceName serviceName, String from);

}