package eu.heliovo.clientapi.processing.context;

import java.util.Date;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.ProcessingService;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;

/**
 * Context service to access the parker model.
 * @author MarcoSoldati
 *
 */
public interface SimpleParkerModelService extends ProcessingService<UrlProcessingResultObject> {

    /**
     * Get the start date to use
     * @return the start date
     */
    public Date getStartTime(); 

    /**
     * Set the start date.
     * @param startTime the date
     */
    public void setStartTime(Date startTime);
    
    /**
     * Convenience method to create a flare plot. This method can be used instead of calling setter methods and then execute.
     * @param date the date of the flare.
     * @return an object to access the result.
     */
    public ProcessingResult<UrlProcessingResultObject> parkerModel(Date startTime);

    /**
     * Set the plot type
     * @param plotType the plot type.
     */
    public void setPlotType(PlotType plotType);
    
    /**
     * Get the plot type.
     * @return the plot type.
     */
    public PlotType getPlotType();
    
    /**
     * Set the velocity to use
     * @param velocity the velocity
     */
    public void setVelocity(int velocity);

    /**
     * Get the velocity to use
     * @return the velocity
     */
    public int getVelocity();
    
    /**
     * Type of the plot
     * @author MarcoSoldati
     *
     */
    public static enum PlotType {
        INNER ("Inner"),
        OUTER ("Outer");
        
        /**
         * The label of the plot type
         */
        private final String label;

        private PlotType(String label) {
            this.label = label;
        }
        
        /**
         * Get the label of this type.
         * @return the plot type label.
         */
        public String getLabel() {
            return label;
        }
    }

}