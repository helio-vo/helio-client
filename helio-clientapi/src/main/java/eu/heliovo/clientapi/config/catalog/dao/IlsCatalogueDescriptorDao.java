package eu.heliovo.clientapi.config.catalog.dao;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.catalog.descriptor.IlsCatalogueDescriptor;

/**
 * Registry that holds the configuration of the ILS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class IlsCatalogueDescriptorDao extends AbstractCatalogueDescriptorDao {
	
    private final List<IlsCatalogueDescriptor> domainValues = new ArrayList<IlsCatalogueDescriptor>();
	
	/**
	 * Populate the registry
	 */
	public IlsCatalogueDescriptorDao() {
	}

	/**
	 * Init the daos content.
	 */
	public void init() {
        domainValues.add(new IlsCatalogueDescriptor("trajectories", "Trajectories", null));
        domainValues.add(new IlsCatalogueDescriptor("keyevents", "Key Events", null));
        domainValues.add(new IlsCatalogueDescriptor("obs_hbo", "Observatory HBO", null));
	}

	/**
	 * Get the domain values for the allowed instruments.
	 * @return the instrument domain values.
	 */
	@Override
	public List<IlsCatalogueDescriptor> getDomainValues() {
        return domainValues;
    }
}
