package eu.heliovo.clientapi.config.catalog.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.heliovo.clientapi.model.catalog.descriptor.IcsCatalogueDescriptor;

/**
 * Registry that holds the configuration of the ICS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class IcsCatalogueDescriptorDao extends AbstractCatalogueDescriptorDao<IcsCatalogueDescriptor> {
    private static final String CATALOG_ID = "ics";
    
	/**
     * Cache the created domain values
     */
    private List<IcsCatalogueDescriptor> domainValues;

    /**
     * Populate the registry
     */
    public IcsCatalogueDescriptorDao() {
    }
        
    // init the HEC configuration
    public void init() {
    	List<IcsCatalogueDescriptor> domainValues = new ArrayList<IcsCatalogueDescriptor>();
        domainValues.add(newIcsCatalogueDescriptor("instrument", "Instrument", null));
        //domainValues.add(initList("observatory", "Observatory", null));
        //domainValues.add(initList("flybys", "Flybys", null));
        
        this.domainValues = Collections.unmodifiableList(domainValues);
    }
    
    private IcsCatalogueDescriptor newIcsCatalogueDescriptor(String listId, String listLabel, String listDesc) {
    	IcsCatalogueDescriptor icsCatalogueDescriptor = new IcsCatalogueDescriptor(listId, listLabel, listDesc);
    	initList(CATALOG_ID, listId, icsCatalogueDescriptor);
    	return icsCatalogueDescriptor;
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
