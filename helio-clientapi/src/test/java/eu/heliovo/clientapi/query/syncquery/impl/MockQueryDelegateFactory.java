package eu.heliovo.clientapi.query.syncquery.impl;

import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.clientapi.query.QueryMethodType;
import eu.heliovo.clientapi.query.QueryType;
import eu.heliovo.clientapi.query.asyncquery.impl.MockAsyncQueryDelegate;
import eu.heliovo.clientapi.query.asyncquery.impl.MockQueryService.MockPort;
import eu.heliovo.clientapi.query.delegate.QueryDelegateFactory;
import eu.heliovo.clientapi.query.syncquery.impl.MockSyncQueryService.MockQueryServicePort;
import eu.heliovo.shared.props.HelioFileUtil;

public class MockQueryDelegateFactory implements QueryDelegateFactory {

    private MockQueryServicePort syncPort;
    private MockPort asyncPort;

    public MockQueryDelegateFactory(MockQueryServicePort syncPort) {
        this.syncPort = syncPort;
    }

    public MockQueryDelegateFactory(MockPort asyncPort) {
        this.asyncPort = asyncPort;
    }

    /**
     * Get the delegate depending on the query type
     * @param queryType the query type, must not be null.
     * @return the corresponding query delegate.
     */
    public QueryDelegate getDelegate(QueryType queryType, QueryMethodType queryMethodType) {
        QueryDelegate delegate = null;
        switch (queryType) {
        case ASYNC_QUERY:
            MockAsyncQueryDelegate asyncDelegate = new MockAsyncQueryDelegate(asyncPort, queryMethodType.toString());
            return asyncDelegate;
        case SYNC_QUERY:
            MockSyncQueryDelegate syncDelegate = new MockSyncQueryDelegate(syncPort, queryMethodType.toString());
            syncDelegate.setHelioFileUtil(new HelioFileUtil("test"));
            delegate = syncDelegate;
        }
        return delegate;
    }

}
