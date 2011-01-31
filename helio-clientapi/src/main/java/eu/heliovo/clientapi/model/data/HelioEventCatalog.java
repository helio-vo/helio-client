package eu.heliovo.clientapi.model.data;

import java.util.List;

import eu.heliovo.clientapi.model.HelioResource;

/**
 * Interface to describe an event list in helio.
 * 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioEventCatalog extends HelioResource, List<HelioEvent> {

	/**
	 * Get the name of the event list.
	 * @return the name of the event list.
	 */
	public String getListName();
	
}
