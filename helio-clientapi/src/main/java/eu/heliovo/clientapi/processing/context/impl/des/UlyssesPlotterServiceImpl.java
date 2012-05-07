package eu.heliovo.clientapi.processing.context.impl.des;

import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.impl.AbstractDesPlotterServiceImpl;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Create a plotter service for mission "ULYSSES".
 * @author MarcoSoldati
 *
 */
public class UlyssesPlotterServiceImpl extends AbstractDesPlotterServiceImpl {
    
    /**
     * The name of the service variant
     */
    public static final String SERVICE_VARIANT =  DesPlotterService.SERVICE_VARIANT + "/ulysses";
    
    /**
     * the wind mission
     */
    public static final String MISSION = "Ulysses";

    /**
     * Create the ULYSSES plotter service
     * @param accessInterfaces the interfaces to use.
     */
    public UlyssesPlotterServiceImpl(HelioServiceName serviceName, String serviceVariant) {
        super(HelioServiceName.DES, SERVICE_VARIANT, MISSION);
        AssertUtil.assertArgumentEquals(HelioServiceName.DES, serviceName, "serviceName");
        AssertUtil.assertArgumentEquals(serviceVariant, SERVICE_VARIANT, "serviceVariant");
    }
}
