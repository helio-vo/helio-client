package eu.heliovo.clientapi.model.service;

import eu.heliovo.clientapi.model.catalog.HelioCatalog;

/**
 * Interface that marks HELIO services that return catalog based information. 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioCatalogService extends HelioService {

	/**
	 * Get all catalogs registered with this service.
	 * @return the catalogs registered with the service.
	 */
	public HelioCatalog getHelioCatalogs();
}
