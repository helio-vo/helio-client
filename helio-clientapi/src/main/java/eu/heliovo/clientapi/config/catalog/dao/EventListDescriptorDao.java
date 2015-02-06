package eu.heliovo.clientapi.config.catalog.dao;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.model.catalog.descriptor.AbstractCatalogueDescriptor;
import eu.heliovo.clientapi.model.catalog.descriptor.EventListDescriptor;
import eu.heliovo.shared.util.FileUtil;

/**
 * DAO for the catalogue descriptors.
 * @author MarcoSoldati
 *
 */
public class EventListDescriptorDao extends AbstractCatalogueDescriptorDao<EventListDescriptor> {
    /**
     * The logger
     */
    private static final Logger _LOGGER = Logger.getLogger(EventListDescriptorDao.class);
      
    /**
     * Base url of the server that contains the HEC configuration.
     */
    private static final String HEC_CONFIG_SERVER = "http://helio.mssl.ucl.ac.uk";
    
    /**
     * The URL to get the VOTable from.
     */
    private static final URL CONFIG_URL = 
            FileUtil.asURL(HEC_CONFIG_SERVER + "/helio-hec/HelioQueryService?" +
            		"STARTTIME=1900-01-01T00:00:00Z&" +
            		"ENDTIME=3000-12-31T00:00:00Z&" +
            		"FROM=hec_catalogue");
    
    /**
     * Cache the created domain values
     */
    private List<EventListDescriptor> domainValues;

        
    // init the HEC configuration
    public void init() {
    	
        URL hecCatalogueUrl = getHelioFileUtil().getFileFromRemoteOrCache("hec", "hec_catalogues.xml", CONFIG_URL);
        StarTable table = readIntoStarTableModel(hecCatalogueUrl);
        
        _LOGGER.info("Loading configuration of " + table.getRowCount() + " event catalogues.");
        
        List<EventListDescriptor> domainValues = new ArrayList<EventListDescriptor>();
        for (int r = 0; r < table.getRowCount(); r++) {
        	// get the current data row
        	Object[] row;
        	try {
        		row = table.getRow(r);
        	} catch (IOException e) {
        		_LOGGER.warn("Failed to load row data from votable: " + e.getMessage(), e);
        		continue;
        	}
        	// create the descriptor
        	EventListDescriptor eventListDescriptor = newEventListDescriptor(table, row);

    		// now try to load the field definitions of the current table.
    		// We do so by sending a fake query to the HEC and reading the header
        	String currentListName = eventListDescriptor.getName();
    		if (currentListName != null) {
    			initList("hec", currentListName, eventListDescriptor);
    		}
            domainValues.add(eventListDescriptor);
        }
        this.domainValues = Collections.unmodifiableList(domainValues);
    }

	private EventListDescriptor newEventListDescriptor(StarTable table, Object[] row) {
		EventListDescriptor eventListDescriptor = new EventListDescriptor();
		// add the content of the table row columns to the event list descriptor.
		addColumnsToDescriptor(table, row, eventListDescriptor);
		return eventListDescriptor;
	}

	private void addColumnsToDescriptor(StarTable table, Object[] row,
			AbstractCatalogueDescriptor eventListDescriptor) {
		for (int col = 0; col < table.getColumnCount(); col++) {
		    // and fill the current cell into the descriptor
		    ColumnInfo colInfo = table.getColumnInfo(col);
		    Object cell = row[col];
		    setCellInDescriptor(eventListDescriptor, colInfo, cell);
		}
	}
    
    /**
     * The domain values for the HEC catalogue.
     * @return the domain values
     */
    public List<EventListDescriptor> getDomainValues() {
        return domainValues;
    }
}
