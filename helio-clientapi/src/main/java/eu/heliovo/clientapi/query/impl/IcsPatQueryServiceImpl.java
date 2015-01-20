package eu.heliovo.clientapi.query.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.IOUtils;

import uk.ac.starlink.table.ArrayColumn;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.votable.DataFormat;
import eu.heliovo.clientapi.config.catalog.dao.InstrumentDescriptorDao;
import eu.heliovo.clientapi.model.catalog.descriptor.InstrumentDescriptor;
import eu.heliovo.clientapi.query.BaseQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Variant of the ICS that merges the ICS result with the PAT table from the HEC.
 * @author MarcoSoldati
 *
 */
public class IcsPatQueryServiceImpl extends BaseQueryServiceImpl {
    /**
     * Identifier of the ICS variant
     */
    public static String SERVICE_VARIANT = "ivo://helio-vo.eu/ics/ics_pat";
  
    /**
     * Reference to the instrument descriptor dao
     */
    private InstrumentDescriptorDao instrumentDescriptorDao;
    
    /**
     * Reference to the stil utils to use.
     */
    private STILUtils stilUtils;
    
    /**
     * Create the DES query support. 
     */
    public IcsPatQueryServiceImpl() {
    }
    
    /**
     * Decorate the query result with additional information from the pat. 
     */
    @Override
    public HelioQueryResult execute() {
        HelioQueryResult result = super.execute();
        IcsPatQueryResultWrapper resultWrapper = new IcsPatQueryResultWrapper(result);
        resultWrapper.setStilUtils(stilUtils);
        resultWrapper.setInstrumentDescriptorDao(instrumentDescriptorDao);
        return resultWrapper;
    }
    
    /**
     * @return the instrumentDescriptorDao
     */
    public InstrumentDescriptorDao getInstrumentDescriptorDao() {
        return instrumentDescriptorDao;
    }

    /**
     * @param instrumentDescriptorDao the instrumentDescriptorDao to set
     */
    public void setInstrumentDescriptorDao(
            InstrumentDescriptorDao instrumentDescriptorDao) {
        this.instrumentDescriptorDao = instrumentDescriptorDao;
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
     * A query result implementation to enhance results from the ICS with information from the PAT.
     * @author MarcoSoldati
     *
     */
    static class IcsPatQueryResultWrapper implements HelioQueryResult {

        /**
         * Instrument column
         */
        private static final String INSTR_COL_NAME = "obsinst_key";
        
        /**
         * Default timeout is 120 seconds
         */
        private final static long DEFAULT_TIMEOUT = 120000;
        
        /**
         * Reference to the instrument descriptor dao
         */
        private InstrumentDescriptorDao instrumentDescriptorDao;
        
        /**
         * Reference to the stil utils to use.
         */
        private STILUtils stilUtils;
        
        /**
         * hold the user logs of the wrapped result
         */
        private final List<LogRecord> userLogs;

        /**
         * The wrapped result
         */
        private final HelioQueryResult helioQueryResult;
        
        /**
         * A query result that enhances the ICS result with the pat table.
         * @param helioQueryResult the wrapped result.
         */
        IcsPatQueryResultWrapper(HelioQueryResult helioQueryResult) {
            this.helioQueryResult = helioQueryResult;
            this.userLogs = new ArrayList<LogRecord>(Arrays.asList(helioQueryResult.getUserLogs()));
        }
        
        @Override
        public Phase getPhase() {
            return helioQueryResult.getPhase();
        }
        
        @Override
        public URL asURL(long timeout, TimeUnit unit) throws JobExecutionException {
            // get the ics table
            URL icsResultUrl = helioQueryResult.asURL(timeout, unit);

            // convert to a star table
            StarTable[] tables;
            try {
                tables = stilUtils.read(icsResultUrl);
            } catch (IOException e) {
                throw new JobExecutionException("Unable to read result from ICS: " + e.getMessage(), e);
            }
            
            if (tables == null || tables.length == 0) {
                throw new JobExecutionException("Unable to read result from ICS: tables are emtpy.");
            }
            final StarTable ics = tables[0];
            
            // find obsInstKey
            int obsInstKey = findObsInstKeyColumn(ics);
            
            // optimize the list for faster comparison
            List<InstrumentDescriptor> instruments = instrumentDescriptorDao.getDomainValues();
            SortedSet<String> patInstruments = getSetOfPatInstrumentNames(instruments);

            // now compare ics and pat and fill in boolean []
            boolean [] isInPat = new boolean[(int)ics.getRowCount()]; // the cast should be safe as ics is rather small
            
            int isInPatCounter = 0;
            for (int i = 0; i < ics.getRowCount(); i++) {
                try {
                    if (patInstruments.contains(ics.getCell(i, obsInstKey))) {
                        isInPat[i] = true;
                        isInPatCounter++;
                    }
                } catch (IOException e) {
                    throw new JobExecutionException("Internal error: unable to read cell [" + i + ", " + obsInstKey + "]");                    
                }
            }

            // create a column star table to append the new column
            ColumnStarTable icsPatTable = new ColumnStarTable(ics) {
                @Override
                public long getRowCount() {
                    return ics.getRowCount();
                }
            };
            
            // copy data from ics to icsPat table (there is probably a better way to do this)
            try {
                for (int col = 0; col < ics.getColumnCount(); col++) {
                    ColumnInfo curColumn = ics.getColumnInfo(col);
                    icsPatTable.addColumn(ArrayColumn.makeColumn(curColumn, ics.getRowCount()));
                    // set the data
                    for (long row = 0; row < ics.getRowCount(); row++) {
                        Object val = ics.getCell(row, col);
                        icsPatTable.setCell(row, col, val);
                    }
                }
            } catch (IOException e) {
                throw new JobExecutionException("Internal error: unable to copy data from ics to ics_pat: " + e.getMessage(), e);                    
            }

            // create the column
            ColumnInfo isInPatColumnInfo = new ColumnInfo("isInPat", Boolean.class, "Boolean values to indicate if a field from the ICS is " +
            		"contained in the Provider Access Table, i.e. if the Data Access Provider Service " +
            		"is able to access the specific instrument.");
            
            ArrayColumn isInPatCol = ArrayColumn.makeColumn(isInPatColumnInfo, isInPat); 
            
            icsPatTable.addColumn(isInPatCol);
            
            userLogs.add(new LogRecord(Level.FINE, isInPatCounter + " instruments are registered with the DPAS (provider access table)"));
            
            // write the modified ics_pat table and return a URL pointing to it.
            String id;
            try {
                Calendar expiration = Calendar.getInstance();
                expiration.add(Calendar.DATE, 20);
                id = stilUtils.persist(expiration.getTime(), DataFormat.TABLEDATA, icsPatTable);
            } catch (IOException e) {
                throw new JobExecutionException("Internal Error: Unable to write merged ICS PAT table to disk: " + e.getMessage(), e);
            }
            File file = stilUtils.getFilePath(id);
            if (file == null) {
                throw new JobExecutionException("Internal Error: Unable to get handle to written file: " + id);
            }
            
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new JobExecutionException("Internal Error: Unable to convert file handle to URL: " + file);
            }
        }

        /**
         * Get a set of instrument names that are included in the PAT (i.e. that have a provider assigned)
         * @param instruments the instrument descriptors
         * @return set of names only.
         */
        private SortedSet<String> getSetOfPatInstrumentNames(
                List<InstrumentDescriptor> instruments) {
            SortedSet<String> patInstruments = new TreeSet<String>(); 
            for (InstrumentDescriptor instrument : instruments) {
                if (instrument.hasProviders()) {
                    patInstruments.add(instrument.getValue());
                }
            }
            return patInstruments;
        }

        /**
         * Find the obs inst key column
         * @param ics the ics
         * @return the position of the column.
         */
        private int findObsInstKeyColumn(StarTable ics) {
         // find the position of the obsinst_key column
            for (int i = 0; i < ics.getColumnCount(); i++) {
                ColumnInfo colInfo = ics.getColumnInfo(i);
                if (INSTR_COL_NAME.equals(colInfo.getName())) {
                    return i;
                }
            }
            
            throw new JobExecutionException("Internal Error: cannot find instrument column in ics table: '" + INSTR_COL_NAME +"'.");
        }

        @Override
        public int getExecutionDuration() {
            return helioQueryResult.getExecutionDuration();
        }

        @Override
        public Date getDestructionTime() {
            return helioQueryResult.getDestructionTime();
        }

        @Override
        public URL asURL() throws JobExecutionException {
            return asURL(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        public VOTABLE asVOTable() throws JobExecutionException {
            return asVOTable(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        public VOTABLE asVOTable(long timeout, TimeUnit unit) throws JobExecutionException {
            URL url = asURL(timeout, unit);
            return VOTableUtils.getInstance().url2VoTable(url);
        }
        
        @Override
        public String asString() throws JobExecutionException {
            return asString(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        public String asString(long timeout, TimeUnit unit) throws JobExecutionException {
            try {
                return IOUtils.toString(asURL().openStream());
            } catch (IOException e) {
                throw new JobExecutionException("Unable to convert result to String: " + e.getMessage(), e);
            }
        }
        @Override
        public LogRecord[] getUserLogs() {
            return userLogs.toArray(new LogRecord[userLogs.size()]);
        }
        
        /**
         * Set the dao for the instrument descriptor
         * @param instrumentDescriptorDao the instrument descriptor dao
         */
        public void setInstrumentDescriptorDao(
                InstrumentDescriptorDao instrumentDescriptorDao) {
            this.instrumentDescriptorDao = instrumentDescriptorDao;
        }
        
        /**
         * Set the stil utils bean to use
         * @param stilUtils the stil utils bean
         */
        public void setStilUtils(STILUtils stilUtils) {
            this.stilUtils = stilUtils;
        }
    }
}