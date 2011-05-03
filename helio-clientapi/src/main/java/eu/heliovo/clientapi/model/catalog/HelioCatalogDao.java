package eu.heliovo.clientapi.model.catalog;

import eu.heliovo.clientapi.model.field.HelioField;

/**
 * A data access object to access the catalogs of one catalog service.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioCatalogDao {

	/**
	 * Get the Field that describes the available catalogs. The catalogs are available through {@link HelioField#getValueDomain()}.
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

	/**
	 * The name of the parent service that provides the catalogs.
	 * @return the name of the parent service.
	 */
	public abstract String getServiceName();
}