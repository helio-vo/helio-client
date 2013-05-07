package eu.heliovo.clientapi.config.catalog.dao;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.config.CatalogueDescriptorDao;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.shared.props.HelioFileUtil;

abstract class AbstractCatalogueDescriptorDao implements CatalogueDescriptorDao {
    /**
     * The logger
     */
    private static final Logger _LOGGER = Logger.getLogger(AbstractCatalogueDescriptorDao.class);

    /**
     * The file utils.
     */
    private transient HelioFileUtil helioFileUtil;
    
    /**
     * The stil utils
     */
    private transient STILUtils stilUtils;
    
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

}