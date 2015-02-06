package eu.heliovo.clientapi.config.catalog.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.heliovo.clientapi.model.catalog.descriptor.IlsCatalogueDescriptor;

/**
 * Registry that holds the configuration of the ILS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class IlsCatalogueDescriptorDao extends AbstractCatalogueDescriptorDao<IlsCatalogueDescriptor> {
    private static final String CATALOG_ID = "ils";

    /**
     * Cache the domain values
     */
    private List<IlsCatalogueDescriptor> domainValues;
	
	/**
	 * Populate the registry
	 */
	public IlsCatalogueDescriptorDao() {
	}

	/**
	 * Init the daos content.
	 */
	public void init() {
    	List<IlsCatalogueDescriptor> domainValues = new ArrayList<IlsCatalogueDescriptor>();
    	
        domainValues.add(newIlsCatalogueDescriptor("trajectories", "Trajectories", null));
        domainValues.add(newIlsCatalogueDescriptor("keyevents", "Key Events", null));
        domainValues.add(newIlsCatalogueDescriptor("obs_hbo", "Observatory HBO", null));
        
        this.domainValues = Collections.unmodifiableList(domainValues);
	}
	  
    private IlsCatalogueDescriptor newIlsCatalogueDescriptor(String listId, String listLabel, String listDesc) {
    	IlsCatalogueDescriptor ilsCatalogueDescriptor = new IlsCatalogueDescriptor(listId, listLabel, listDesc);
    	initList(CATALOG_ID, listId, ilsCatalogueDescriptor);
    	return ilsCatalogueDescriptor;
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
