package eu.heliovo.clientapi.model.catalog;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.HelioFieldDescriptor;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Configuration of the fields of a catalogues. This consists of a list of links to the parameters.
 * @author marco soldati at fhnw ch
 * @deprecated to be removed
 */
@Deprecated public class HelioCatalog implements DomainValueDescriptor<String> {
	/**
	 * Name of the field that describes the catalog
	 */
	public static final String CATALOG_FIELD = "catalog";

	/**
	 * Linked hash set of fields in this catalog.
	 */
	private final Set<HelioFieldDescriptor<?>> fields = new LinkedHashSet<HelioFieldDescriptor<?>>();

	/**
	 * name and identifier of this catalog.
	 */
	private final String catalogName;

	/**
	 * This catalog's label to be displayed to the user.
	 */
	private final String catalogLabel;

	/**
	 * A short description of this catalog.
	 */
	private final String catalogDescription;
	
	/**
	 * Create a new Helio catalog
	 * @param catalogName name of the catalog.
	 * @param catalogLabel label of the catalog. Defaults to name if null or empty
	 * @param catalogDescription description for the catalog. May be null.
	 */
	public HelioCatalog(String catalogName, String catalogLabel, String catalogDescription) {
		AssertUtil.assertArgumentHasText(catalogName, "catalogName");
		this.catalogName = catalogName;
		this.catalogLabel = catalogLabel == null || catalogLabel.isEmpty() ? catalogName : catalogLabel;
		this.catalogDescription = catalogDescription;
	}
	
	/**
	 * Add a field to this catalog. The catalog will preserve the order in which the fields have been added.
	 * @param field the field to add
	 * @return true if the field has been added, false if the field already existed in this catalog and thus will be ignored.
	 */
	public boolean addField(HelioFieldDescriptor<?> field) {
		return fields.add(field);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof HelioCatalog)) {
			return false;
		}
		return catalogName.equals(((HelioCatalog)obj).catalogName);	
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HelioCatalog {");
		sb.append("name=").append(catalogName);
		sb.append(", label=").append(catalogLabel);
		sb.append(", description=").append(catalogDescription);
		sb.append(", fields=[");
		boolean first = true;
		for (HelioFieldDescriptor<?> field : fields) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(field.getName());
		}
		sb.append("]}");
		return sb.toString();
	}

	@Override
	public String getValue() {
		return catalogName;
	}

	@Override
	public String getLabel() {
		return catalogLabel;
	}

	@Override
	public String getDescription() {
		return catalogDescription;
	}

	/**
	 * Return the name of the catalog (same as {@link #getValue()}).
	 * @return the name of the catalog
	 */
	public String getCatalogName() {
		return catalogName;
	}
	
	/**
	 * Get the fields registered with this catalog
	 * @return the fields of this catalog.
	 */
	public HelioFieldDescriptor<?>[] getFields() {
		return fields.toArray(new HelioFieldDescriptor[fields.size()]);
	}
	
	public HelioFieldDescriptor<?> getFieldById(String id) {
	    AssertUtil.assertArgumentHasText(id, "id");
	    for (HelioFieldDescriptor<?> current : fields) {
            if (id.equals(current.getId())) {
                return current;
            }
        }
	    return null;
	}
}
