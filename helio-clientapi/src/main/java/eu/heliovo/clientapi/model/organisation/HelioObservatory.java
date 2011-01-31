package eu.heliovo.clientapi.model.organisation;

import eu.heliovo.clientapi.model.HelioResource;

/**
 * Description of a Helio observatory.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioObservatory extends HelioResource {
	/**
	 * Get the id of the observatory. 
	 */
	public String getObservatoryId();
	
	/**
	 * Get the label of the observatory.
	 */
	public String getObservatoryLabel();
	
	/**
	 * The list of instruments operated by this observatory
	 * @return the instruments.
	 */
	public HelioInstrument[] getInstruments();
	
}
