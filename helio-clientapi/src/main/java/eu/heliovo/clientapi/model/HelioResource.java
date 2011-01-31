package eu.heliovo.clientapi.model;

import eu.heliovo.clientapi.help.annotation.Description;
import eu.heliovo.clientapi.help.annotation.MethodHelp;
import eu.heliovo.clientapi.help.annotation.Synopsis;


/**
 * Describe any Helio object as a resrouce. All objects are meant to implement this interface. 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioResource {
	/**
	 * Get a qualified identifier for the resource. 
	 * This identifier is unique within the whole Helio system.
	 * @return the resource id.
	 */
	@MethodHelp(
		help = @Description("Returns the Id of a resource."),
		synopsis = @Synopsis()
	)
	public String getResourceId();
}
