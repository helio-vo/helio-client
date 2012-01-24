package eu.heliovo.clientapi.processing.hps;

import java.net.URL;
import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingResultObject;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.hps.SolarWindPropagationModel.SolarWindProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for a propagation model processing service.
 * @author MarcoSoldati
 */
public interface SolarWindPropagationModel extends ProcessingService<SolarWindProcessingResultObject> {
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
     * Set the longitude of the CME.
     * @param longitude the longitude on the sun.
     */
    public void setLongitude(float longitude);
    
    /**
     * Get the longitude of the CME.
     * @return the longitude
     */
    public float getLongitude();
    
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
     * @param longitude the longitude of the CME on the Sun.
     * @param width the size of the event
     * @param speed the speed
     * @param speedError the possible error of the speed.
     * @return an object to access the result of the call asynchronously.
     * @throws JobExecutionException if anything goes wrong the exception will be wrapped in a JobExecutionException.
     */
    public abstract ProcessingResult<SolarWindProcessingResultObject> execute(Date startTime, Float longitude, Float width, Float speed, Float speedError) throws JobExecutionException;

    /**
     * The result object retruned by this model
     * @author MarcoSoldati
     *
     */
    public interface SolarWindProcessingResultObject extends ProcessingResultObject {
        /**
         * URL to the inner plot URL.
         * @return the inner plot
         */
        public URL getInnerPlotUrl();
        
        /**
         * URL to the outer plot URL
         * @return the outer plot URL
         */
        public URL getOuterPlotUrl();

        /**
         * URL to the VOTable
         * @return the voTable.
         */
        public URL getVoTableUrl();
    }
}
