package eu.heliovo.clientapi.processing.context;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;

/**
 * Context service to access Date evaluation service plots.
 * @author MarcoSoldati
 *
 */
public interface DesPlotterService extends ProcessingService<UrlProcessingResultObject> {
    
    /**
     * Name of the service variant for plotting. 
     */
    public static final String SERVICE_VARIANT = "ivo://helio-vo.eu/des/plot";
    
    /**
     * Get the start date to use
     * @return the start date
     */
    public Date getStartTime(); 

    /**
     * Set the start date.
     * @param startTime the start date
     */
    public void setStartTime(Date startTime);

    /**
     * Get the end date
     * @return the end date
     */
    public Date getEndTime();

    /**
     * Set the end date
     * @param endTime the end date.
     */
    public void setEndTime(Date endTime);
    
    /**
     * Convenience method to create a DES plot. This method can be used instead of calling setter methods and then execute.
     * @param startTime the start date.
     * @param endTime the end date.
     * @return an object to access the result.
     */
    public ProcessingResult<UrlProcessingResultObject> desPlot(Date startTime, Date endTime);

}