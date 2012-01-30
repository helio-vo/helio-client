package eu.heliovo.clientapi.processing.hps;

import java.net.URL;
import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingResultObject;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.hps.CirPropagationModel.CirProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Marker interface for a propagation model processing service.
 * @author MarcoSoldati
 */
public interface CirPropagationModel extends ProcessingService<CirProcessingResultObject> {
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
     * Convenience method to call the propagation model in one single method. 
     * Alternatively you can call the setter-methods and then the generic {@link #execute()} method.
     * @param startTime the start time
     * @param longitude the longitude of the CME on the Sun.
     * @param speed the speed
     * @return an object to access the result of the call asynchronously.
     * @throws JobExecutionException if anything goes wrong the exception will be wrapped in a JobExecutionException.
     */
    public abstract ProcessingResult<CirProcessingResultObject> execute(Date startTime, Float longitude, Float speed) throws JobExecutionException;

    /**
     * The result object returned by this model
     * @author MarcoSoldati
     *
     */
    public interface CirProcessingResultObject extends ProcessingResultObject {
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
