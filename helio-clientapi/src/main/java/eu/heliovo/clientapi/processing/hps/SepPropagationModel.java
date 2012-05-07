package eu.heliovo.clientapi.processing.hps;

import java.net.URL;
import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.HelioProcessingServiceResultObject;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.hps.SepPropagationModel.SepProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for a propagation model processing service.
 * @author MarcoSoldati
 */
public interface SepPropagationModel extends ProcessingService<SepProcessingResultObject> {

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
     * Set the longitude of the Sep.
     * @param longitude the longitude on the sun.
     */
    public void setLongitude(float longitude);
    
    /**
     * Get the longitude of the Sep.
     * @return the longitude
     */
    public float getLongitude();

    /**
     * Set the Sep speed
     * @param speed the speed
     */
    public void setSpeed(float speed);
    
    /**
     * Get the Sep speed.
     * @return the speed.
     */
    public float getSpeed();

    /**
     * Set the Sep speed error
     * @param speedError the speedError
     */
    public void setSpeedError(float speedError);
    
    /**
     * Get the speed error
     * @return the speed error.
     */
    public float getSpeedError();
    
    /**
     * Set the SEP beta param.
     * @param beta the beta param.
     */
    public void setBeta(float beta);
    
    /**
     * Get the beta param.
     * @return the beta param.
     */
    public float getBeta();
    
    /**
     * Convenience method to call the propagation model in one single method. 
     * Alternatively you can call the setter-methods and then the generic {@link #execute()} method.
     * @param startTime the start time
     * @param longitude the longitude of the Sep on the Sun.
     * @param speed the speed
     * @param speedError the possible error of the speed.
     * @param beta the beta param
     * @return an object to access the result of the call asynchronously.
     * @throws JobExecutionException if anything goes wrong the exception will be wrapped in a JobExecutionException.
     */
    public abstract ProcessingResult<SepProcessingResultObject> execute(Date startTime, Float longitude, Float speed, Float speedError, Float beta) throws JobExecutionException;

    /**
     * The result object returned by this model
     * @author MarcoSoldati
     *
     */
    public interface SepProcessingResultObject extends HelioProcessingServiceResultObject {
        /**
         * URL to the inner plot URL.
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
