package eu.heliovo.clientapi.processing.context.impl.des;

import eu.heliovo.clientapi.processing.context.DesPlotterService;
import eu.heliovo.clientapi.processing.context.impl.AbstractDesPlotterServiceImpl;

/**
 * Create a plotter service for mission "ACE".
 * @author MarcoSoldati
 *
 */
public class AcePlotterServiceImpl extends AbstractDesPlotterServiceImpl {
    
    /**
     * The name of the service variant
     */
    public static final String SERVICE_VARIANT =  DesPlotterService.SERVICE_VARIANT + "/ace";
    
    /**
     * the ACE mission
     */
    public static final String MISSION = "ACE";

    /**
     * Create the ACE plotter service
     */
    public AcePlotterServiceImpl() {
        super(MISSION);
    }
}
