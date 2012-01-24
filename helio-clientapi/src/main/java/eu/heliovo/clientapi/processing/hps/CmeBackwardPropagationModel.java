package eu.heliovo.clientapi.processing.hps;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.hps.CmePropagationModel.CmeProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for a propagation model processing service.
 * @author MarcoSoldati
 */
public interface CmeBackwardPropagationModel extends ProcessingService<CmeProcessingResultObject> {

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
     * Set the width 
     * @param width the width
     */
    public void setWidth(float width);
    /**
     * Get the width
     * @return the width
     */
    public float getWidth();

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
     * @param hitObject the object hit by the CME.
     * @param width the size of the event
     * @param speed the speed
     * @param speedError the possible error of the speed.
     * @return an object to access the result of the call asynchronously.
     * @throws JobExecutionException if anything goes wrong the exception will be wrapped in a JobExecutionException.
     */
    public abstract ProcessingResult<CmeProcessingResultObject> execute(Date startTime, String hitObject, Float width, Float speed, Float speedError) throws JobExecutionException;
}
