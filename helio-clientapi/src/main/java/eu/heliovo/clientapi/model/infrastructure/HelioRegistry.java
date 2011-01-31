package eu.heliovo.clientapi.model.infrastructure;

import eu.heliovo.clientapi.model.HelioResource;

/**
 * Describe a helio registry.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioRegistry extends HelioResource {
	
	public String getRegistryId();
	
	public String getRegistryName();
}
