package eu.heliovo.clientapi.query.delegate;

import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.clientapi.query.QueryMethodType;
import eu.heliovo.clientapi.query.QueryType;

public interface QueryDelegateFactory {

    /**
     * Get the delegate depending on the query type
     * @param queryType the query type, must not be null.
     * @return the corresponding query delegate.
     */
    public QueryDelegate getDelegate(QueryType queryType,
            QueryMethodType queryMethodType);

}