package eu.heliovo.clientapi.config.catalog.dao;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.model.catalog.descriptor.EventListDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.shared.util.FileUtil;

/**
 * DAO for the catalogue descriptors.
 * @author MarcoSoldati
 *
 */
public class EventListDescriptorDao extends AbstractCatalogueDescriptorDao {
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
     * URL to access the field definitions
     */
    private static final String HEC_FIELD_URL_TEMPLATE = 
            HEC_CONFIG_SERVER + "/helio-hec/HelioQueryService?FROM=%1$s&LIMIT=1";

    private static final String HEC_FIELD_CACHE_TEMPLATE = "hec_fields_%1$s.xml";
    
    /**
     * Reference to the field type factory.
     */
    private transient FieldTypeFactory fieldTypeFactory;
    
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
        	EventListDescriptor eventListDescriptor = new EventListDescriptor();
        	
        	// add the content of the table row columns to the event list descriptor.
        	addColumnsToDescriptor(table, row, eventListDescriptor);
        	
        	String currentCatalogName = eventListDescriptor.getName();
        	try {

        		// now try to load the field definitions of the current table.
        		// We do so by sending a fake query to the HEC and reading the header
        		if (currentCatalogName != null) {
        			String cacheFileName = String.format(HEC_FIELD_CACHE_TEMPLATE, currentCatalogName);
        			URL hecFieldUrlTemplate = FileUtil.asURL(String.format(HEC_FIELD_URL_TEMPLATE, currentCatalogName));

        			URL hecFieldUrl = getHelioFileUtil().getFileFromRemoteOrCache("hec", cacheFileName, hecFieldUrlTemplate);
        			try {
        				//                    VOElement votableModel = stilUtils.readVOElement(hecFieldUrl);
        				//                    NodeList resources = votableModel.getElementsByTagName("RESOURCE");
        				//                    VOElement resource = (VOElement)resources.item(0);
        				//                    TableElement[] tables = (TableElement[]) resource.getChildrenByName( "TABLE" );
        				//                    TableElement fieldTtable = tables[0];

        				StarTable fieldTable = readIntoStarTableModel(hecFieldUrl);
        				List<HelioFieldDescriptor<?>> fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
        				for (int i = 0; i < fieldTable.getColumnCount(); i++) {
        					ColumnInfo colInfo = fieldTable.getColumnInfo(i);
        					HelioFieldDescriptor<?> helioField = new HelioFieldDescriptor<Object>();
        					helioField.setDescription(colInfo.getDescription());
        					helioField.setName(colInfo.getName());
        					helioField.setId(colInfo.getName());

        					FieldType type = fieldTypeFactory.getNewTypeByJavaClass(colInfo.getContentClass());
        					if (type != null) {
        						type.setUcd(colInfo.getUCD());
        						type.setUnit(colInfo.getUnitString());
        						type.setUtype(colInfo.getUtype());
        						helioField.setType(type);
        					} else {
        						_LOGGER.warn("Unable to find field type for class " + colInfo.getContentClass());
        					}
        					fieldDescriptors.add(helioField);
        				}
        				eventListDescriptor.setFieldDescriptors(fieldDescriptors);

        			} catch (Exception e) {
        				_LOGGER.warn("Failed to parse " + hecFieldUrlTemplate + ": " + e.getMessage());
        				continue;
        			}

        		}
        	} catch (Exception e) {
        		_LOGGER.warn("Failed to create configuration for catalogue " + currentCatalogName + ": " 
        				+ e.getMessage(), e);
        		continue;
        	}
            domainValues.add(eventListDescriptor);
        }
        this.domainValues = Collections.unmodifiableList(domainValues);
    }

	private void addColumnsToDescriptor(StarTable table, Object[] row,
			EventListDescriptor eventListDescriptor) {
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

    /**
     * @return the fieldTypeFactory
     */
    public FieldTypeFactory getFieldTypeFactory() {
        return fieldTypeFactory;
    }

    /**
     * @param fieldTypeFactory the fieldTypeFactory to set
     */
    public void setFieldTypeFactory(FieldTypeFactory fieldTypeFactory) {
        this.fieldTypeFactory = fieldTypeFactory;
    }
}
