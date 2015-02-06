package eu.heliovo.clientapi.config.catalog.dao;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.config.CatalogueDescriptorDao;
import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.clientapi.model.catalog.descriptor.AbstractCatalogueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.FileUtil;

abstract class AbstractCatalogueDescriptorDao<T extends HelioCatalogueDescriptor> implements CatalogueDescriptorDao {
    /**
     * The logger
     */
    private static final Logger _LOGGER = Logger.getLogger(AbstractCatalogueDescriptorDao.class);

	/**
	 * Base url of the server that contains the HEC configuration.
	 */
	private static final String HELIO_CONFIG_SERVER = "http://helio.mssl.ucl.ac.uk";
	
	/**
	 * The URL to get the VOTable from.
	 */
	private static final String CONFIG_URL_TEMPLATE = 
						HELIO_CONFIG_SERVER + "/helio-%1$s/HelioQueryService?" +
									"STARTTIME=1900-01-01T00:00:00Z&" +
									"ENDTIME=3000-12-31T00:00:00Z&" +
									"FROM=%2$s&LIMIT=1";
    
	private static final String TEMP_FILE_TEMPLATE = "%1$s_%2$s.xml";
	
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

    
    public AbstractCatalogueDescriptorDao() {
        super();
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
     * Set the value of one field in the descriptor
     * @param descriptor the descriptor to modify.
     * @param colInfo the header of the current cell.
     * @param cell the cell's value to add to the descriptor.
     */
    protected void setCellInDescriptor(Object descriptor, ColumnInfo colInfo, Object cell) {
        String setterName = "set" + WordUtils.capitalize(colInfo.getName(), new char[] {'_'}).replaceAll("_", "");
        Method writer;
        try {
            writer = descriptor.getClass().getMethod(setterName, String.class);
        } catch (Exception e) {
            _LOGGER.warn("Failed to find  writer method '" + setterName + "': " + e.getMessage(), e);
            return;
        }
        if (writer != null) {
            try {
                if (cell != null) {
                    writer.invoke(descriptor, cell);
                }
            } catch (Exception e) {
                _LOGGER.warn("Failed to fully initialize catalogue descriptor for " + colInfo.getName() + ": " + e.getMessage(), e);
            }
        } else {
            _LOGGER.warn("Unable to find  writer method  " + setterName);
        }
    }
    
	protected void initList(String catalogId, String listId, T catalogueDescriptor) {
		URL configUrl = FileUtil.asURL(getConfigUrlTemplate(catalogId, listId)); 
		try {
			String tempFile = getCacheFileNameTemplate(catalogId, listId);
			StarTable table = loadStarTable(listId, configUrl, tempFile);
			
			_LOGGER.info("Loading configuration of '" + listId +"' catalogue.");
			populateFieldDescriptors(table, catalogueDescriptor);
		} catch (Exception e) {
			_LOGGER.warn("Failed to parse " + configUrl + ": " + e.getMessage());
		}
	}

	protected StarTable loadStarTable(String listId, URL configUrl, String tempFileName) {
		URL catalogUrl = getHelioFileUtil().getFileFromRemoteOrCache(listId, tempFileName, configUrl);
        StarTable table = readIntoStarTableModel(catalogUrl);
		return table;
	}
	
    /**
     * Read a VOTable URL into a start table and return the first table. 
     * @param url the  URL.
     * @return the catalog or field list as star table model.
     */
    protected StarTable readIntoStarTableModel(URL url) {
        StarTable[] tables;
        try {
            tables = getStilUtils().read(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse the configuration " + url + ": " + e.getMessage(), e);
        }
        
        if (tables.length != 1) {
            throw new RuntimeException("Failed to parse the configuration " + url + ". tables.length=" + tables.length);
        }
        
        StarTable table = tables[0];
        return table;
    }
    

	protected String getConfigUrlTemplate(String catalogId, String listId) {
		return String.format(CONFIG_URL_TEMPLATE, catalogId, listId);
	}
	
	
	protected String getCacheFileNameTemplate(String catalogId, String listId) {
		return String.format(TEMP_FILE_TEMPLATE, catalogId, listId);
	}
	
    private T populateFieldDescriptors(StarTable table, T catalogueDescriptor) {
    	if (catalogueDescriptor instanceof AbstractCatalogueDescriptor) {
    		AbstractCatalogueDescriptor abstractCatDescriptor = (AbstractCatalogueDescriptor) catalogueDescriptor;
    		List<HelioFieldDescriptor<?>> fieldDescriptors = extractFieldDescriptors(table);
    		abstractCatDescriptor.setFieldDescriptors(fieldDescriptors);
    	}
    	return catalogueDescriptor;
    }

	protected List<HelioFieldDescriptor<?>> extractFieldDescriptors(StarTable table) {
		List<HelioFieldDescriptor<?>> fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
		for (int i = 0; i < table.getColumnCount(); i++) {
			ColumnInfo colInfo = table.getColumnInfo(i);
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
		return fieldDescriptors;
	}

	public FieldTypeFactory getFieldTypeFactory() {
		return fieldTypeFactory;
	}

	public void setFieldTypeFactory(FieldTypeFactory fieldTypeFactory) {
		this.fieldTypeFactory = fieldTypeFactory;
	}

}