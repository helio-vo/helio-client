package eu.heliovo.clientapi.processing.context;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;

/**
 * Context service to access the parker model.
 * @author MarcoSoldati
 *
 */
public interface SimpleParkerModelService extends ContextService {

    /**
     * Get the start date to use
     * @return the start date
     */
    public Date getStartDate(); 

    /**
     * Set the start date.
     * @param startDate the date
     */
    public void setStartDate(Date startDate);
    
    /**
     * Convenience method to create a flare plot. This method can be used instead of calling setter methods and then execute.
     * @param date the date of the flare.
     * @return an object to access the result.
     */
    public ProcessingResult parkerModel(Date startDate);

}