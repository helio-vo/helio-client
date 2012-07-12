package eu.heliovo.clientapi.query.syncquery.impl;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.heliovo.registryclient.AccessInterface;

public class MockSyncQueryDelegate extends SyncQueryDelegate {

    /**
     * The port used by the mock delegate
     */
    private final HelioQueryService port;
    private final String methodName;

    public MockSyncQueryDelegate(HelioQueryService port, String methodName) {
        this.port = port;
        this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }
    
    @Override
    protected HelioQueryService getPort(AccessInterface accessInterface) {
        return this.port;
    }

}
