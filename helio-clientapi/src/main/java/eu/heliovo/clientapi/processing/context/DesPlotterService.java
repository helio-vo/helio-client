package eu.heliovo.clientapi.processing.context;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;

/**
 * Context service to access Date evaluation service plots.
 * @author MarcoSoldati
 *
 */
public interface DesPlotterService extends ContextService {
    
    /**
     * Name of the service variant for plotting. 
     */
    public static final String SERVICE_VARIANT = "ivo://helio-vo.eu/des/plot";
    
    /**
     * Get the start date to use
     * @return the start date
     */
    public Date getStartDate(); 

    /**
     * Set the start date.
     * @param startDate the start date
     */
    public void setStartDate(Date startDate);

    /**
     * Get the end date
     * @return the end date
     */
    public Date getEndDate();

    /**
     * Set the end date
     * @param endDate the end date.
     */
    public void setEndDate(Date endDate);
    
    /**
     * Convenience method to create a DES plot. This method can be used instead of calling setter methods and then execute.
     * @param startDate the start date.
     * @param endDate the end date.
     * @return an object to access the result.
     */
    public ProcessingResult desPlot(Date startDate, Date endDate);

}