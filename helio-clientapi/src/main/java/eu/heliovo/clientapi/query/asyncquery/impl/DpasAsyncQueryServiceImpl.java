package eu.heliovo.clientapi.query.asyncquery.impl;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * DPAS needs an increase timeout for the first call.
 * @author MarcoSoldati
 * TODO: Rather use a spring property for setting the timeout.
 */
public class DpasAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    
    /**
     * Create the DES query support.
     * @param serviceName name of the service. Must be equal to {@link HelioServiceName#DES}
     * @param serviceVariant name of the service variant. May be null.
     * @param description a description of the servcie from the registry
     * @param accessInterfaces the interfaces to use for this service.
     */
    public DpasAsyncQueryServiceImpl(HelioServiceName serviceName, String serviceVariant) {
        super(serviceName, serviceVariant);
        AssertUtil.assertArgumentEquals(HelioServiceName.DPAS, serviceName,  "serviceName");
    }
    
    /**
     * Increase call timeout for DPAS
     */
    @Override
    protected long getCallTimout() {
        return 60000;
    }
}