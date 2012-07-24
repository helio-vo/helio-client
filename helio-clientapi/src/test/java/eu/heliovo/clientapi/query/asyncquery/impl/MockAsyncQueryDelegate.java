package eu.heliovo.clientapi.query.asyncquery.impl;

import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.heliovo.clientapi.query.delegate.AsyncQueryDelegate;
import eu.heliovo.registryclient.AccessInterface;

public class MockAsyncQueryDelegate extends AsyncQueryDelegate {

    /**
     * The port used by the mock delegate
     */
    private final LongHelioQueryService port;
    private final String methodName;

    public MockAsyncQueryDelegate(LongHelioQueryService port, String methodName) {
        this.port = port;
        this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }
    
    @Override
    protected LongHelioQueryService getPort(AccessInterface accessInterface) {
        return port;
    }

}
