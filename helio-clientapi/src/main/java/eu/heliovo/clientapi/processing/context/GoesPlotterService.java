package eu.heliovo.clientapi.processing.context;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;

/**
 * Context service to access goes plots
 * @author MarcoSoldati
 *
 */
public interface GoesPlotterService extends ContextService {

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
     * Convenience method to create a goes plot. This method can be used instead of calling setter methods and then execute.
     * @param startDate the start date.
     * @param endDate the end date.
     * @return an object to access the result.
     */
    public ProcessingResult goesPlot(Date startDate, Date endDate);

}