package eu.heliovo.clientapi.processing.context.impl.des;

import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.impl.AbstractDesPlotterServiceImpl;

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
    public UlyssesPlotterServiceImpl() {
        super(MISSION);
    }
}
