package eu.heliovo.clientapi.query;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.registryclient.HelioServiceName;


public class HelioQueryFactoryImpl implements HelioQueryFactory {

	private WhereClauseFactoryBean whereClauseFactoryBean;

	/**
	 * Create a new query object for the given service and the given catalog.
	 * @param serviceName the service to query.
	 * @param from the catalog to query for.
	 * @return a query builder that can be configured for a query. Call {@link #buildQuery(HelioServiceName, String)} in the end.
	 */
	@Override
	public QueryBuilder buildQuery(HelioServiceName serviceName, String from) {
		return new QueryBuilder(serviceName, from);
	}

	public class QueryBuilder {
		private final HelioServiceName serviceName;
		private final String from;

		private String startTime;
		private String endTime;
		private int startIndex;
		private int maxRecords;
		private WhereClause whereClause;

		/**
		 * Create the query builder for a specific catalog.
		 * 
		 * @param serviceName
		 *            name of the service to send the query to.
		 * @param from
		 *            the catalog to query from.
		 */
		QueryBuilder(HelioServiceName serviceName, String from) {
			this.serviceName = serviceName;
			this.from = from;
			updateWhereClauses();
		}

		/**
		 * the start and end date and time of the query range. Expected format
		 * is ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
		 * 
		 * @param startTime
		 *            start time
		 * @param endTime
		 *            end time
		 * @return this
		 */
		public QueryBuilder timeRange(String startTime, String endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			return this;
		}

		/**
		 * the start date and time of the query range. Expected format is
		 * ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
		 * 
		 * @param startTime
		 *            the start time.
		 * @return this
		 */
		public QueryBuilder startTime(String startTime) {
			this.startTime = startTime;
			return this;
		}

		/**
		 * the end date and time of the query range. Expected format is
		 * ISO8601 (YYYY-MM-dd['T'HH:mm:ss[SSS]]). Must not be null.
		 * 
		 * @param endTime
		 *            the end time.
		 * @return this
		 */
		public QueryBuilder endTime(String endTime) {
			this.endTime = endTime;
			return this;
		}

		/**
		 * Set the start index of the query result to be returned. This ca be
		 * used for paging.
		 * 
		 * @param startIndex
		 *            the start index.
		 * @return this
		 */
		public QueryBuilder startIndex(int startIndex) {
			this.startIndex = startIndex;
			return this;
		}

		/**
		 * Set the number or items to be returned by the query. Defaults to
		 * 5000.
		 * 
		 * @param maxRecords
		 *            . The number of max records.
		 * @return this
		 */
		public QueryBuilder maxRecords(int maxRecords) {
			this.maxRecords = maxRecords;
			return this;
		}

		public QueryBuilder queryTerm(String fieldName, Operator operator, Object... arguments) {
			HelioFieldDescriptor<?> fieldDescriptor = whereClause.findFieldDescriptorById(fieldName);
			if (fieldDescriptor == null) {
				throw new IllegalArgumentException("Unable to find field with name " + fieldName + " in catalog "
								+ from);
			}
			@SuppressWarnings({ "rawtypes", "unchecked" })
			HelioFieldQueryTerm<?> queryTerms = new HelioFieldQueryTerm(fieldDescriptor, operator, arguments);
			whereClause.setQueryTerm(fieldDescriptor, queryTerms);
			return this;
		}

		public HelioQuery build() {
			HelioQuery helioQuery = new HelioQuery(serviceName, from);
			helioQuery.setStartTime(startTime);
			helioQuery.setEndTime(endTime);
			helioQuery.setWhereClause(whereClause);
			helioQuery.setStartIndex(startIndex);
			helioQuery.setMaxRecords(maxRecords);
			return helioQuery;
		}

		/**
		 * Get the where clause for the current catalog. If a catalog does not
		 * support where clauses the where clause will have an empty
		 * helioFieldDescriptor field.
		 * 
		 * @return the where clause for the 'from' list.
		 */
		public WhereClause getWhereClause() {
			return whereClause;
		}

		/**
		 * Update the where clauses, depending on the from property.
		 */
		private void updateWhereClauses() {
			this.whereClause = whereClauseFactoryBean.createWhereClause(serviceName, from);
		}
	}

	public void setWhereClauseFactoryBean(WhereClauseFactoryBean whereClauseFactoryBean) {
		this.whereClauseFactoryBean = whereClauseFactoryBean;
	}

}