package eu.heliovo.clientapi.model.catalog;

import eu.heliovo.clientapi.model.field.HelioField;

/**
 * Registry that holds the catalogs of one catalog service.
 * @author marco soldati at fhnw ch
 *
 */
public interface CatalogRegistry {

	/**
	 * Get the Field that describes the available catalogs. The catalogs are available through {@link HelioField#getValueDomain()}.
	 * The order of the value domain is defined by the insertion time of catalogs into the registry.
	 * @return catalog field.
	 */
	public abstract HelioField<String> getCatalogField();

	/**
	 * Get the fields assigned to a given catalog.
	 * This is a convenience method for {@link #getCatalogById(String)}.{@link HelioCatalog#getFields() getFields()}.
	 * @param catalogId the catalog to query for
	 * @return the fields or null if the catalog does not exist.
	 */
	public abstract HelioField<?>[] getFields(String catalogId);
	
	/**
	 * Get the catalog with a given id.
	 * @param catalogId the id to look up. Must not be null.
	 * @return the catalog with the given name or null if not found.
	 */
	public abstract HelioCatalog getCatalogById(String catalogId);

}