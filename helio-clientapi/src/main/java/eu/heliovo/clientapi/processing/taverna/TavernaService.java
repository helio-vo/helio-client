package eu.heliovo.clientapi.processing.taverna;

import java.util.Map;

import eu.heliovo.clientapi.processing.ProcessingResultObject;
import eu.heliovo.clientapi.processing.ProcessingService;

/**
 * Marker interface for context services.
 * @author MarcoSoldati
 * @param <T> the type of the result being returned by Taverna.
 */
public interface TavernaService <T extends ProcessingResultObject>  extends ProcessingService<T> {

    /**
     * Get the input names for a specific Taverna Service.
     * @return the input names.
     */
    //public List<String> getInputNames();
    
    /**
     * Set a map of inputs, the first input denotes the 
     * @param inputs
     */
    public void setInputs(Map<String, Object> inputs);
        
    /**
     * Get the service result
     * @return the service result.
     */
    //public Object getResult();
}
