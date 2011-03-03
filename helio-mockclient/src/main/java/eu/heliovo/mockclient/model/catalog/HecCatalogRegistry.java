package eu.heliovo.mockclient.model.catalog;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;

/**
 * Registry that holds the configuration of the HEC catalogs.
 * The insert order of the catalogs is preserved. The registry guarantees that these catalogs are returned in the same
 * sort order.
 * @author marco soldati at fhnw ch
 *
 */
public class HecCatalogRegistry {
	/**
	 * The registry instance
	 */
	private static HecCatalogRegistry instance;
	
	/**
	 * Get the singleton instance of the catalog registry
	 * @return
	 */
	public static synchronized HecCatalogRegistry getInstance() {
		if (instance == null) {
			instance = new HecCatalogRegistry();
		}
		return instance;
	}
	
	/**
	 * The map of catalogs. Use method {@link #add(HelioCatalog)} to add new elements.
	 */
	private final Map<String, HelioCatalog> helioCatalogMap = new LinkedHashMap<String, HelioCatalog>();
	
	/**
	 * The field type registry to use
	 */
	private FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();
	
	/**
	 * Populate the registry
	 */
	private HecCatalogRegistry() {
		HelioCatalog catalog = new HelioCatalog("goes_xray_flare", "goes_xray_flare", "Goes list of xray flares");
		catalog.addField(new HelioField<Date>("ntime_start", "ntime_start", "selection start time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("time_start", "time_start", "event start time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("time_peak", "time_peak", "event peak time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("time_end", "time_end", "event end time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("ntime_end", "ntime_end", "selection end time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<BigInteger>("nar", "nar", "active region number", fieldTypeRegistry.getType("integer")));
		catalog.addField(new HelioField<Double>("latitude", "latitude", "heliographic latitude", fieldTypeRegistry.getType("decimal")));
		catalog.addField(new HelioField<Double>("longitude", "longitude", "heliographic longitude", fieldTypeRegistry.getType("decimal")));
		catalog.addField(new HelioField<Double>("long_carr", "long_carr", "Carrington longitude", fieldTypeRegistry.getType("decimal")));
		catalog.addField(new HelioField<String>("xray_class", "xray_class", "x-ray importance class", fieldTypeRegistry.getType("string")));
		catalog.addField(new HelioField<String>("optical_class", "optical_class", "optical importance class", fieldTypeRegistry.getType("string")));
		
		add(catalog);
		
		catalog = new HelioCatalog("test_catalog", "Test Catalog", "catalog for testing purposed only.");
		catalog.addField(new HelioField<Integer>("delay", "delay", "Add an artifical delay in milliseconds to the service. For testing purposes only.", fieldTypeRegistry.getType("integer"), new Integer(0)));
		catalog.addField(new HelioField<Float>("exception-probability", "exception-probability", "Add a probability that the service throws an exception. " +
				"if <=0: never throw an exception, if >= 1.0: always throw an exception. " +
				"For testing purposes only.", fieldTypeRegistry.getType("float"), new Float(0)));
		add(catalog);
	}

	/**
	 * Add a catalog to the map.
	 * @param catalog the catalog to add.
	 * @throws IllegalArgumentException if a catalog with the same name has been added before.
	 */
	private void add(HelioCatalog catalog) throws IllegalArgumentException {
		HelioCatalog oldCatalog = helioCatalogMap.put(catalog.getCatalogName(), catalog);
		if (oldCatalog != null) {
			throw new IllegalArgumentException("Catalog with name " + catalog.getCatalogName() + " has been previously registered. Old catalog: " + oldCatalog + ", new catalog: " + catalog);
		}
	}
	
	/**
	 * Get the Field that describes the available catalogs. The catalogs are available through {@link HelioField#getValueDomain()}.
	 * The order of the value domain is defined by the insertion time of catalogs into the registry.
	 * @return catalog field.
	 */
	public HelioField<String> getCatalogField() {
		// we should probably cache thie field domain values as this method is rather expensive.
		Collection<HelioCatalog> cat = helioCatalogMap.values();
		@SuppressWarnings("unchecked")
		DomainValueDescriptor<String>[] catValueDomain = (DomainValueDescriptor<String>[]) cat.toArray(new DomainValueDescriptor<?>[cat.size()]);
		HelioField<String> catalogField = new HelioField<String>(
				"hec_catalog", 
				"catalog", 
				"Generated field that defines the domain of allowed catalogs", 
				fieldTypeRegistry.getType("string"), 
				catValueDomain, 
				null);
		return catalogField;
	}

	/**
	 * Get the fields assigned to a given catalog.
	 * @param catalogId the catalog to query for
	 * @return the fields or null if the catalog does not exist.
	 */
	public HelioField<?>[] getFields(String catalogId) {
		HelioCatalog catalog = helioCatalogMap.get(catalogId);
		if (catalog == null) {
			return null;
		}
		return catalog.getFields();
	}
}
