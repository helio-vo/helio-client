package eu.heliovo.clientapi.query.asyncquery.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import org.apache.commons.io.IOUtils;

import uk.ac.starlink.table.ArrayColumn;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.votable.DataFormat;
import eu.helio_vo.xml.longqueryservice.v0.LongHelioQueryService;
import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.catalog.impl.HelioCatalogDaoFactory;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Variant of the ICS that merges the ICS result with the PAT table from the HEC.
 * @author MarcoSoldati
 *
 */
class IcsPatAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    /**
     * Identifier of the ICS variant
     */
    static String SERVICE_VARIANT = "ivo://helio-vo.eu/ics/ics_pat"; 
    
    /**
     * Create the DES query support. The constructor is public for the factory implementation.
     * @param serviceName name of the service. Must be equal to {@link HelioServiceName#DES}
     * @param serviceVariant the variant. Must equal to  {@value #SERVICE_VARIANT}.
     * @param description a description of the service from the registry
     * @param accessInterfaces the interfaces to use for this service.
     */
    public IcsPatAsyncQueryServiceImpl(HelioServiceName serviceName, String serviceVariant, String description, AccessInterface ... accessInterfaces) {
        super(serviceName, serviceVariant, description, accessInterfaces);
        AssertUtil.assertArgumentEquals(HelioServiceName.ICS, serviceName,  "serviceName");
        AssertUtil.assertArgumentEquals(SERVICE_VARIANT, serviceVariant,  "serviceVariant");
    }
    
    
    @Override
    protected HelioQueryResult createQueryResult(String resultId, LongHelioQueryService currentPort, String callId, long jobStartTime, List<LogRecord> logRecords) {
        return new IcsPatQueryResult(resultId, currentPort, callId, jobStartTime, logRecords);
    }
    
    /**
     * A query result implementation to enhance results from the ICS with information from the PAT.
     * @author MarcoSoldati
     *
     */
    static class IcsPatQueryResult extends AsyncQueryResultImpl {

        /**
         * Instrument column
         */
        private static final String INSTR_COL_NAME = "obsinst_key";
        
        /**
         * Retrieve the pat.
         */
        private final HelioCatalog pat;
        
        /**
         * A query result that enhances the ICS result with the pat table.
         * @param id the id of the query
         * @param port the port to use to retrieve the result
         * @param callId identifier for the called method (for logging) 
         * @param jobStartTime start time of the job
         * @param logRecords mutable list of log records.
         */
        IcsPatQueryResult(String id, LongHelioQueryService port, String callId, long jobStartTime, List<LogRecord> logRecords) {
            super(id, port, callId, jobStartTime, logRecords);
            pat = getPat();
        }
        
        /**
         * Get the provider access table.
         * @return the provider access table.
         */
        protected HelioCatalog getPat() {
            HelioCatalogDao dpasDao = HelioCatalogDaoFactory.getInstance().getHelioCatalogDao(HelioServiceName.DPAS);
            //System.out.println(Arrays.toString(dpasDao.getCatalogField().getValueDomain()));
            HelioCatalog pat = dpasDao.getCatalogById("dpas");
            if (pat == null) {
                throw new RuntimeException("Internal Error: unable to find Provider Access Table (PAT).");
            }
            return pat;
        }
        
        @Override
        public URL asURL(long timeout, TimeUnit unit) throws JobExecutionException {
            // get the ics table
            URL url = super.asURL(timeout, unit);
            //System.out.println(url);

            // convert to a star table
            StarTable[] tables;
            try {
                tables = STILUtils.read(url);
            } catch (IOException e) {
                throw new JobExecutionException("Unable to read result from ICS: " + e.getMessage(), e);
            }
            
            if (tables == null || tables.length == 0) {
                throw new JobExecutionException("Unable to read result from ICS: tables are emtpy.");
            }
            final StarTable ics = tables[0];
            
            // find obsInstKey
            int obsInstKey = findObsInstKeyColumn(ics);
            
            // get the instruments in the pat.
            HelioField<?> patInstr = pat.getFieldById("instrument");
            if (patInstr == null) {
                throw new JobExecutionException("Internal error: unable to find instrument field in PAT");
            }
            
            @SuppressWarnings("unchecked")
            DomainValueDescriptor<String>[] values = (DomainValueDescriptor<String>[]) patInstr.getValueDomain();
            
            // optimize the list for faster comparison
            SortedSet<String> patInstruments = new TreeSet<String>(); 
            for (DomainValueDescriptor<String> instrument : values) {
                patInstruments.add(instrument.getValue());
            }
                        
            // now compare ics and pat and fill in boolean []
            boolean [] isInPat = new boolean[(int)ics.getRowCount()]; // the cast should be safe as ics is rather small
            
            for (int i = 0; i < ics.getRowCount(); i++) {
                try {
                    if (patInstruments.contains(ics.getCell(i, obsInstKey))) {
                        isInPat[i] = true;
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
            
            // write the modified ics_pat table and return a URL pointing to it.
            String id;
            try {
                Calendar expiration = Calendar.getInstance();
                expiration.add(Calendar.DATE, 20);
                id = STILUtils.persist(expiration.getTime(), DataFormat.TABLEDATA, icsPatTable);
            } catch (IOException e) {
                throw new JobExecutionException("Internal Error: Unable to write merged ICS PAT table to disk: " + e.getMessage(), e);
            }
            File file = STILUtils.getFilePath(id);
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
    }
}