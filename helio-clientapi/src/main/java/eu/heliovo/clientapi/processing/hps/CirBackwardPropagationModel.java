package eu.heliovo.clientapi.processing.hps;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.hps.CirPropagationModel.CirProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for a propagation model processing service.
 * @author MarcoSoldati
 */
public interface CirBackwardPropagationModel extends ProcessingService<CirProcessingResultObject> {
    /**
     * Set the start time of the propagation
     * @param startTime the start time
     */
    public void setStartTime(Date startTime);
    
    /**
     * Get the start time of the propagation
     * @return the start time
     */
    public Date getStartTime();
    
    /**
     * Get the name of the object that has been hit.
     * @return the object that has been hit.
     */
    public String getHitObject();
    
    /**
     * Set the object that has been hit by an event.
     */
    public void setHitObject(String hitObject);
    
    /**
     * Set the CME speed
     * @param speed the speed
     */
    public void setSpeed(float speed);
    
    /**
     * Get the CME speed.
     * @return the speed.
     */
    public float getSpeed();

    /**
     * Set the CME speed error
     * @param speedError the speedError
     */
    public void setSpeedError(float speedError);
    
    /**
     * Get the speed error
     * @return the speed error.
     */
    public float getSpeedError();

    /**
     * Convenience method to call the propagation model in one single method. 
     * Alternatively you can call the setter-methods and then the generic {@link #execute()} method.
     * @param startTime the start time
     * @param the object hit by the solar wind.
     * @param speed the speed
     * @param speedError the speedError
     * @return an object to access the result of the call asynchronously.
     * @throws JobExecutionException if anything goes wrong the exception will be wrapped in a JobExecutionException.
     */
    public abstract ProcessingResult<CirProcessingResultObject> execute(Date startTime, String hitObject, Float speed, Float speedError) throws JobExecutionException;
}
