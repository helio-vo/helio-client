package eu.heliovo.clientapi.config.catalog.dao;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.catalog.descriptor.IcsCatalogueDescriptor;

/**
 * Registry that holds the configuration of the ICS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class IcsCatalogueDescriptorDao extends AbstractCatalogueDescriptorDao {
	
    private final List<IcsCatalogueDescriptor> domainValues = new ArrayList<IcsCatalogueDescriptor>();
	
	/**
	 * Populate the registry
	 */
	public IcsCatalogueDescriptorDao() {
	}

	/**
	 * Init the daos content.
	 */
	public void init() {
        domainValues.add(new IcsCatalogueDescriptor("instrument", "Instrument", null));
        domainValues.add(new IcsCatalogueDescriptor("observatory", "Observatory", null));
        domainValues.add(new IcsCatalogueDescriptor("flybys", "Flybys", null));
	}

	/**
	 * Get the domain values for the allowed instruments.
	 * @return the instrument domain values.
	 */
	@Override
	public List<IcsCatalogueDescriptor> getDomainValues() {
        return domainValues;
    }
}
