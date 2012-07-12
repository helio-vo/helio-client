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
    private static final Logger _LOGGER = Logger.getLogger(HecCatalogueDescriptorDaoTest.class);
      
    /**
     * The URL to get the VOTable from.
     */
    private static final URL CONFIG_URL = 
            FileUtil.asURL("http://festung1.oats.inaf.it:8080/helio-hec/HelioQueryService?" +
            		"STARTTIME=1900-01-01T00:00:00Z&" +
            		"ENDTIME=3000-12-31T00:00:00Z&" +
            		"FROM=hec_catalogue");
    
    /**
     * The file utils.
     */
    private transient HelioFileUtil helioFileUtil;
    
    /**
     * The stil utils
     */
    private transient STILUtils stilUtils;

    /**
     * Cache the created domain values
     */
    private Set<HecCatalogueDescriptor> domainValues;
    
    // init the HEC configuration
    public void init() {
        URL hecCatalogueUrl = helioFileUtil.getFileFromRemoteOrCache(helioFileUtil.getHelioTempDir("hec").getAbsolutePath(), "hec_catalogues.xml", CONFIG_URL);
        StarTable[] tables;
        try {
            tables = stilUtils.read(hecCatalogueUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse the HEC catalogues configuration");
        }
        
        if (tables.length != 1) {
            throw new RuntimeException("Failed to parse the HEC catalogues configuration. tables.length=" + tables.length);
        }
        
        StarTable table = tables[0];
        Set<HecCatalogueDescriptor> domainValues = new LinkedHashSet<HecCatalogueDescriptor>();
        for (int r = 0; r < table.getRowCount(); r++) {
            Object[] row;
            try {
                row = table.getRow(r);
            } catch (IOException e) {
                _LOGGER.warn("Failed to load row data from votable: " + e.getMessage(), e);
                continue;
            }
            HecCatalogueDescriptor hecCatalogueDescriptor = new HecCatalogueDescriptor();
            for (int col = 0; col < table.getColumnCount(); col++) {
                ColumnInfo colInfo = table.getColumnInfo(col);
                String setterName = "set" + colInfo.getName().substring(0,1).toUpperCase() + colInfo.getName().substring(1);
                Method writer;
                try {
                    writer = HecCatalogueDescriptor.class.getMethod(setterName, String.class);
                } catch (Exception e) {
                    _LOGGER.warn("Failed to find  writer method '" + setterName + "': " + e.getMessage(), e);
                    continue;
                }
                if (writer != null) {
                    try {
                        if (row[col] != null) {
                            writer.invoke(hecCatalogueDescriptor, row[col]);
                        }
                    } catch (Exception e) {
                        _LOGGER.warn("Failed to fully initialize catalogue descriptor for " + colInfo.getName() + ": " + e.getMessage(), e);
                    }
                } else {
                    _LOGGER.warn("Unable to find  writer method  " + setterName);
                }
            }
            domainValues.add(hecCatalogueDescriptor);
        }
        this.domainValues = Collections.unmodifiableSet(domainValues);
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
}
