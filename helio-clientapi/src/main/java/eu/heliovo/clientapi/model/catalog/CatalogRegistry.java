package eu.heliovo.clientapi.model.catalog;

import eu.heliovo.clientapi.model.field.HelioField;

/**
 * Registry that holds the catalogs of one catalog service.
 * @author marco soldati at fhnw ch
 *
 */
public interface CatalogRegistry {
	
	/**
	 * Get the parent service of the registered catalogs. 
	 * @return the parent service.
	 */
	//public abstract HelioCatalogService getCatalogService();

	/**
	 * Get the Field that describes the available catalogs. The catalogs are available through {@link HelioField#getValueDomain()}.
	 * The order of the value domain is defined by the insertion time of catalogs into the registry.
	 * @return catalog field.
	 */
	public abstract HelioField<String> getCatalogField();

	/**
	 * Get the fields assigned to a given catalog.
	 * @param catalogId the catalog to query for
	 * @return the fields or null if the catalog does not exist.
	 */
	public abstract HelioField<?>[] getFields(String catalogId);

}