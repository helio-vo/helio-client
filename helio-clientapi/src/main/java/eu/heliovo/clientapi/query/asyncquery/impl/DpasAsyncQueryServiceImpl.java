package eu.heliovo.clientapi.query.asyncquery.impl;

import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * DPAS needs an increase timeout for the first call.
 * @author MarcoSoldati
 *
 */
class DpasAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    
    /**
     * Create the DES query support.
     * @param serviceName name of the service. Must be equal to {@link HelioServiceName#DES}
     * @param serviceVariant of the service variant.
     * @param description a description of the servcie from the registry
     * @param accessInterfaces the interfaces to use for this service.
     */
    public DpasAsyncQueryServiceImpl(HelioServiceName serviceName, String serviceVariant, String description, AccessInterface ... accessInterfaces) {
        super(serviceName, null, description, accessInterfaces);
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