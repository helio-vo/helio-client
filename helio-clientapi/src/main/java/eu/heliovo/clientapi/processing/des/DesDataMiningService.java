package eu.heliovo.clientapi.processing.des;

import eu.heliovo.clientapi.config.des.DesFunction;
import eu.heliovo.clientapi.config.des.DesMission;
import eu.heliovo.clientapi.config.des.DesParam;
import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

public class DesDataMiningService <T extends DesResultObject> implements ProcessingService<T>  {
    
    /**
     * Store the des mission 
     */
    private DesMission mission;
    
    /**
     * Store the des function
     */
    private DesFunction function;

    /**
     * The current parameter
     */
    private DesParam param;
    
    /**
     * Dynamically add new properties to this bean.
     */
    private void updateBeanConfig() {
        
    }
    
    
    /**
     * Execute the process
     */
    public ProcessingResult<T> execute() {
        return null;
    }

    @Override
    public HelioServiceName getServiceName() {
        return HelioServiceName.DES;
    }

    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return ServiceCapability.ASYNC_QUERY_SERVICE.equals(capability);
    }

    @Override
    public String getServiceVariant() {
        return null;
    }

    /**
     * @return the mission
     */
    public DesMission getMission() {
        return mission;
    }

    /**
     * @param mission the mission to set
     */
    public void setMission(DesMission mission) {
        this.mission = mission;
        updateBeanConfig();
    }

    /**
     * @return the function
     */
    public DesFunction getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(DesFunction function) {
        this.function = function;
        updateBeanConfig();
    }
    
    
    
}
