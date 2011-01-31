package eu.heliovo.clientapi.model.organisation;

/**
 * Description of an instrument
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioInstrument {
	/**
	 * Return the instrument id. Must not be null. The id must not contain spaces (???).
	 * @return the id of the instrument.
	 */
	public String getInstrumentId();
	
	/**
	 * Return the name of the instrument. The name may contain any valid string characters and is 
	 * meant for visual feedback in GUIs. 
	 * @return the name of the instrument.
	 */
	public String getInstrumentName();
	
	
	
}
