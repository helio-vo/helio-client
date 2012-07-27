package eu.heliovo.clientapi.model.service.dao;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.model.field.FieldType;
import eu.heliovo.clientapi.model.field.FieldTypeFactory;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.field.descriptor.HecCatalogueDescriptor;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.FileUtil;

/**
 * DAO for the catalogue descriptors.
 * @author MarcoSoldati
 *
 */
public class HecCatalogueDescriptorDao {
    /**
     * The logger
     */
    private static final Logger _LOGGER = Logger.getLogger(HecCatalogueDescriptorDao.class);
      
    /**
     * The URL to get the VOTable from.
     */
    private static final URL CONFIG_URL = 
            FileUtil.asURL("http://festung1.oats.inaf.it:8080/helio-hec/HelioQueryService?" +
            		"STARTTIME=1900-01-01T00:00:00Z&" +
            		"ENDTIME=3000-12-31T00:00:00Z&" +
            		"FROM=hec_catalogue");
    
    /**
     * URL to access the field definitions
     */
    private static final String HEC_FIELD_URL_TEMPLATE = 
            "http://festung1.oats.inaf.it:8080/helio-hec/HelioQueryService?FROM=%1$s&LIMIT=1";

    private static final String HEC_FIELD_CACHE_TEMPLATE = "hec_fields_%1$s.xml";
//            FileUtil.asURL("http://festung3.oats.inaf.it:8080/helio-hec/VOSI/tables");
    
    /**
     * The file utils.
     */
    private transient HelioFileUtil helioFileUtil;
    
    /**
     * The stil utils
     */
    private transient STILUtils stilUtils;
    
    /**
     * Reference to the field type factory.
     */
    private transient FieldTypeFactory fieldTypeFactory;

    /**
     * Cache the created domain values
     */
    private Set<HecCatalogueDescriptor> domainValues;
    
    // init the HEC configuration
    public void init() {
        URL hecCatalogueUrl = helioFileUtil.getFileFromRemoteOrCache("hec", "hec_catalogues.xml", CONFIG_URL);
        StarTable table = readIntoStarTableModel(hecCatalogueUrl);
        
        _LOGGER.info("Loading configuration of " + table.getRowCount() + " event catalogues.");
        
        Set<HecCatalogueDescriptor> domainValues = new LinkedHashSet<HecCatalogueDescriptor>();
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
            HecCatalogueDescriptor hecCatalogueDescriptor = new HecCatalogueDescriptor();
            
            // loop over the columns
            String currentCatalogName = null;
            for (int col = 0; col < table.getColumnCount(); col++) {
                // and fill the current cell into the descriptor
                ColumnInfo colInfo = table.getColumnInfo(col);
                Object cell = row[col];
                setCellInDescriptor(hecCatalogueDescriptor, colInfo, cell);
                
                // we also need to find the column name for later.
                if ("name".equals(colInfo.getName())) {
                    currentCatalogName = cell.toString();
                }
            }
            
            // now try to load the field definitions of the current table.
            // We do so by sending a fake query to the HEC and reading the header
            if (currentCatalogName != null) {
                String cacheFileName = String.format(HEC_FIELD_CACHE_TEMPLATE, currentCatalogName);
                URL hecFieldUrlTemplate = FileUtil.asURL(String.format(HEC_FIELD_URL_TEMPLATE, currentCatalogName));

                URL hecFieldUrl = helioFileUtil.getFileFromRemoteOrCache("hec", cacheFileName, hecFieldUrlTemplate);
                try {
//                    VOElement votableModel = stilUtils.readVOElement(hecFieldUrl);
//                    NodeList resources = votableModel.getElementsByTagName("RESOURCE");
//                    VOElement resource = (VOElement)resources.item(0);
//                    TableElement[] tables = (TableElement[]) resource.getChildrenByName( "TABLE" );
//                    TableElement fieldTtable = tables[0];
                    
                    StarTable fieldTable = readIntoStarTableModel(hecFieldUrl);
                    for (int i = 0; i < fieldTable.getColumnCount(); i++) {
                        ColumnInfo colInfo = fieldTable.getColumnInfo(i);
                        HelioField<?> helioField = new HelioField<Object>();
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
                    }
                    
                } catch (Exception e) {
                    _LOGGER.warn("Failed to parse " + hecFieldUrlTemplate + ": " + e.getMessage(), e);
                }
                
            }
            
            domainValues.add(hecCatalogueDescriptor);
        }
        this.domainValues = Collections.unmodifiableSet(domainValues);
    }

    /**
     * Set the value of one field in the descriptor
     * @param hecCatalogueDescriptor the descriptor to modify.
     * @param colInfo the header of the current cell.
     * @param cell the cell's value to add to the descriptor.
     */
    private void setCellInDescriptor(HecCatalogueDescriptor hecCatalogueDescriptor, ColumnInfo colInfo, Object cell) {
        String setterName = "set" + colInfo.getName().substring(0,1).toUpperCase() + colInfo.getName().substring(1);
        Method writer;
        try {
            writer = HecCatalogueDescriptor.class.getMethod(setterName, String.class);
        } catch (Exception e) {
            _LOGGER.warn("Failed to find  writer method '" + setterName + "': " + e.getMessage(), e);
            return;
        }
        if (writer != null) {
            try {
                if (cell != null) {
                    writer.invoke(hecCatalogueDescriptor, cell);
                }
            } catch (Exception e) {
                _LOGGER.warn("Failed to fully initialize catalogue descriptor for " + colInfo.getName() + ": " + e.getMessage(), e);
            }
        } else {
            _LOGGER.warn("Unable to find  writer method  " + setterName);
        }
    }

    /**
     * Read the HEC catalog or field VOTable into an URL 
     * @param url the  URL.
     * @return the catalog or field list as star table model.
     */
    private StarTable readIntoStarTableModel(URL url) {
        StarTable[] tables;
        try {
            tables = stilUtils.read(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse the configuration " + url);
        }
        
        if (tables.length != 1) {
            throw new RuntimeException("Failed to parse the configuration " + url + ". tables.length=" + tables.length);
        }
        
        StarTable table = tables[0];
        return table;
    }

    /**
     * The domain values for the HEC catalogue.
     * @return the domain values
     */
    public Set<HecCatalogueDescriptor> getDomainValues() {
        return domainValues;
    }
    
    /**
     * @return the helioFileUtil
     */
    public HelioFileUtil getHelioFileUtil() {
        return helioFileUtil;
    }

    /**
     * @param helioFileUtil the helioFileUtil to set
     */
    public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
        this.helioFileUtil = helioFileUtil;
    }

    /**
     * @return the stilUtils
     */
    public STILUtils getStilUtils() {
        return stilUtils;
    }

    /**
     * @param stilUtils the stilUtils to set
     */
    public void setStilUtils(STILUtils stilUtils) {
        this.stilUtils = stilUtils;
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
