package eu.heliovo.clientapi.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import uk.ac.starlink.table.ArrayColumn;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnPermutedStarTable;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.QueueTableSequence;
import uk.ac.starlink.table.RowPermutedStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.ByteArrayDataSource;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.FileDataSource;
import uk.ac.starlink.util.URLDataSource;
import uk.ac.starlink.votable.DataFormat;
import uk.ac.starlink.votable.VOTableBuilder;
import uk.ac.starlink.votable.VOTableWriter;
import eu.heliovo.shared.util.FileUtil;

/**
 * Facade class of the STIL library. The library provides facilities to - load
 * VOTables from a variety of sources - persist VOTables - create views/subsets
 * of VOTables - concatenate multiple tables
 * 
 * @author SimonFelix
 */
public class STILUtils {
    /**
     * The logger for this registry impl
     */
    private static final Logger LOGGER = Logger.getLogger(STILUtils.class);

    /**
     * The hibernate session factory
     */
    private static final SessionFactory sessionFactory = new AnnotationConfiguration().configure(STILUtils.class.getResource("/clientapi-hibernate.cfg.xml")).buildSessionFactory();

    static {
        // make sure the path to persist files exists
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating persisted file path: " + PersistedFile.PERSISTED_FILES_PATH);
        }
        new File(PersistedFile.PERSISTED_FILES_PATH).mkdirs();

        // clean up files & database
        purgeExpiredPersistedFiles();
        cleanupPersistedFilesDirectory();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialization of STILUtils completed");
        }
    }

    /**
     * Returns the index of a column for a given name.
     * 
     * @param _table
     *            The table that should be searched
     * @param _columnName
     *            The title of the column to look for
     * @return The index of the column or -1 if no column with a matching name
     *         can be found
     */
    private static int columnIndex(StarTable _table, String _columnName) {
        for (int i = 0; i < _table.getColumnCount(); i++)
            if (_columnName.equals(_table.getColumnInfo(i).getName()))
                return i;
        return -1;
    }

    /**
     * Concatenates several tables into a single table. Identity of columns is
     * determined by column name.
     * 
     * Please not that this method currently is not optimized and may exhibit
     * very bad performance when joining many or large tables.
     * 
     * @param _onlyColumnsInAll
     *            If true only columns are included that exist in all supplied
     *            tables
     * @param _tables
     *            Source tables
     * @throws IOException
     * @return A single table with all the records of the supplied tables
     */
    public static StarTable concatenate(boolean _onlyColumnsInAll, StarTable... _tables) throws IOException {
        if (_tables.length == 0)
            return null;
        if (_tables.length == 1)
            return _tables[0];

        long totalRows = 0;
        for (StarTable t : _tables)
            totalRows += t.getRowCount();

        ColumnStarTable res = ColumnStarTable.makeTableWithRows(totalRows);

        // PHASE 1: combine the columns of all tables
        if (_onlyColumnsInAll)
            /*
             * for each column of the first table check whether this column
             * appears in all other tables if it does --> add the column to the
             * result table
             */
            for (int i = 0; i < _tables[0].getColumnCount(); i++) {
                ColumnInfo curColumn = _tables[0].getColumnInfo(i);

                boolean containedInAllOtherTables = true;
                for (int j = 1; j < _tables.length; j++)
                    if (columnIndex(_tables[j], curColumn.getName()) == -1) {
                        containedInAllOtherTables = false;
                        break;
                    }

                if (containedInAllOtherTables)
                    res.addColumn(ArrayColumn.makeColumn(curColumn, totalRows));
            }
        else {
            // add all columns of each table if the column does not yet exist
            // in the results table
            for (StarTable t : _tables)
                for (int i = 0; i < t.getColumnCount(); i++) {
                    ColumnInfo colI = t.getColumnInfo(i);
                    if (columnIndex(res, colI.getName()) == -1)
                        res.addColumn(ArrayColumn.makeColumn(colI, totalRows));
                }
        }

        // PHASE 2: add all data to this new table
        long offset = 0;
        for (StarTable t : _tables) {
            for (int i = 0; i < res.getColumnCount(); i++) {
                int colSrc = columnIndex(t, res.getColumnInfo(i).getName());
                if (colSrc == -1)
                    /*
                     * if current table does not contain a column with the
                     * desired name, just set all cells to NULL instead
                     */
                    for (int row = 0; row < t.getRowCount(); row++)
                        res.setCell(offset + row, i, null);
                else
                    /*
                     * otherwise, copy all contents from the current table to
                     * the result-table
                     */
                    for (int row = 0; row < t.getRowCount(); row++)
                        res.setCell(offset + row, i, t.getCell(row, colSrc));
            }

            // move offset so that the next table will be written in
            // the rows below the current table
            offset += t.getRowCount();
        }

        return res;
    }

    /**
     * Reads VOTables from the given data source.
     * 
     * @param _src
     *            The datasource to read the VOT from
     * @return An array containing all tables
     * @throws IOException
     */
    public static StarTable[] read(DataSource _src) throws IOException {
        TableSequence tables = new VOTableBuilder(false).makeStarTables(_src, StoragePolicy.getDefaultPolicy());

        ArrayList<StarTable> res = new ArrayList<StarTable>();
        for (StarTable table; (table = tables.nextTable()) != null;)
            res.add(table);

        return res.toArray(new StarTable[0]);
    }

    /**
     * Reads VOTables from a URL
     * 
     * @param _src
     *            The URL to read the VOT from
     * @return An array containing all tables
     * @throws IOException
     */
    public static StarTable[] read(URL _src) throws IOException {
        return read(new URLDataSource(_src));
    }

    /**
     * Reads VOTables from an input stream.
     * 
     * @param _src
     *            The stream to read the VOT from
     * @return An array containing all tables
     * @throws IOException
     */
    public static StarTable[] read(InputStream _src) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
        byte[] buffer = new byte[8192];
        for (;;) {
            int read = _src.read(buffer);
            if (read == -1)
                break;

            baos.write(buffer, 0, read);
        }
        return read(new ByteArrayDataSource("", baos.toByteArray()));
    }

    /**
     * Reads VOTables from the given file.
     * 
     * @param _src
     *            The file to read the VOT from
     * @return An array containing all tables
     * @throws IOException
     */
    public static StarTable[] read(File _src) throws IOException {
        return read(new FileDataSource(_src));
    }

    /**
     * Creates a view of a table with a subset of the columns. If you pass
     * {0,7,1,1} you'll get a view that contains the first, eigth and two times
     * second column of the source table.
     * 
     * @param _src
     *            The source table to create the view from
     * @param _columns
     *            The columns the view should contain
     * @return The view
     */
    public static StarTable columnSubset(StarTable _src, int... _columns) {
        return new ColumnPermutedStarTable(_src, _columns);
    }

    /**
     * Creates a view of a table with a subset of the rows. If you pass
     * {0,7,1,1} you'll get a view that contains the first, eigth and two times
     * second row of the source table.
     * 
     * @param _src
     *            The source table to create the view from
     * @param _rows
     *            The rows the view should contain
     * @return The view
     */
    public static StarTable rowSubset(StarTable _src, long... _rows) {
        return new RowPermutedStarTable(_src, _rows);
    }

    /**
     * Stores VOTables in persistent storage. The method returns a handle to the
     * persisted tables. This handle allows you to read it at a later time.
     * 
     * The tables will be stored for at least 180 days.
     * 
     * @see #persist(Date, StarTable...)
     * @param _src
     *            The VOTables to store
     * @return Returns a handle to the persisted data.
     * @throws IOException
     */
    public static String persist(StarTable... _src) throws IOException {
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.DATE, 180);

        return persist(expiration.getTime(), _src);
    }

    /**
     * Sync persisted files in the files system with those in the database.
     */
    private static void cleanupPersistedFilesDirectory() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Syncing persisted files in filesystem with those in database");
        }
        Session s = sessionFactory.openSession();

        List<?> db = s.createQuery("FROM PersistedFile").list();
        File[] fs = new File(PersistedFile.PERSISTED_FILES_PATH).listFiles();

        // there are more files in the filesystem than in the database --> fix
        // the filesystem
        if (fs.length > db.size()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("  There are more files in the filesystem");
            }
            
            for (File f : fs) {
                PersistedFile pf = (PersistedFile) s.createQuery("FROM PersistedFile WHERE Id=?").setString(0, f.getName()).uniqueResult();
                if (pf == null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("    Deleting " + f.getAbsolutePath());
                    }
                    f.delete();
                }
            }
            fs = new File(PersistedFile.PERSISTED_FILES_PATH).listFiles();
        }

        // if there are more files in the database than in the filesystem -->
        // fix the db
        if (db.size() > fs.length) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("  There are more files in the database");
            }
            Transaction tx = s.beginTransaction();
            Arrays.sort(fs);
            for (Object o : db) {
                PersistedFile pf = (PersistedFile) o;
                if (Arrays.binarySearch(fs, new File(pf.getPersistedFilePath())) < 0) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("    Deleting " + pf.id + " from the database");
                    }
                    s.delete(pf);
                }
            }
            tx.commit();
        }

        s.close();
    }

    /**
     * Forces the deletion of expired persisted files.
     */
    public static void purgeExpiredPersistedFiles() {
        Session s = sessionFactory.openSession();

        Transaction tx = s.beginTransaction();
        for (Object o : s.createQuery("FROM PersistedFile WHERE expires<current_timestamp").list()) {
            PersistedFile pf = (PersistedFile) o;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Purging outdated file " + pf.getPersistedFilePath());
            }
            
            File fileToDelete = new File(pf.getPersistedFilePath());
            if (FileUtil.removeFile(fileToDelete)) {
                s.delete(pf);
            } else {
                    LOGGER.warn("Unable to remove expired file: " + pf.getPersistedFilePath() + ". Reason unknown.");
            }
        }

        tx.commit();
        s.close();
    }
    
    /**
     * Stores VOTables in persistent storage with data format defaulting to {@link DataFormat#BINARY}. The method returns a handle to the
     * persisted tables. This handle allows you to read it at a later time.
     * 
     * The tables will be stored at least until the supplied expiration date.
     * 
     * @param _expiration
     *            The expiration date
     * @param _src
     *            The VOTables to store
     * @return Returns a handle to the persisted data.
     * @throws IOException
     */
    public static String persist(Date _expiration, StarTable... _src) throws IOException {
        return persist(_expiration, DataFormat.BINARY, _src);
    }

    /**
     * Stores VOTables in persistent storage. The method returns a handle to the
     * persisted tables. This handle allows you to read it at a later time.
     * 
     * The tables will be stored at least until the supplied expiration date.
     * 
     * @param _expiration
     *            The expiration date
     * @param dataFormat
     *       the format in which to store the data.
     * @param _src
     *            The VOTables to store
     * @return Returns a handle to the persisted data.
     * @throws IOException
     */
    public static String persist(Date _expiration, DataFormat dataFormat, StarTable... _src) throws IOException {
        purgeExpiredPersistedFiles();

        QueueTableSequence tables = new QueueTableSequence();
        for (StarTable t : _src)
            tables.addTable(t);
        tables.endSequence();

        PersistedFile pf = PersistedFile.createNewPersistedFile(_expiration);
        FileOutputStream fos = new FileOutputStream(pf.getPersistedFilePath());
        new VOTableWriter(dataFormat, true).writeStarTables(tables, fos);

        IOUtils.closeQuietly(fos);
        
        Session s = sessionFactory.openSession();

        Transaction tx = s.beginTransaction();
        s.save(pf);
        tx.commit();
        s.close();
        return pf.id;
    }

    /**
     * This method reads persisted tables. You have to pass the id you got when
     * calling {@link #persist(Date, StarTable...)}.
     * 
     * @param _id
     *            The id of persisted tables
     * @return The VOTables
     * @throws IOException
     */
    public static StarTable[] read(String _id) throws IOException {
        return read(new FileDataSource(new PersistedFile(_id).getPersistedFilePath()));
    }

    /**
     * This method returns the file path of a persisted table. You have to pass
     * the id you got when calling {@link #persist(Date, StarTable...)}.
     * 
     * @param _id
     *            The id of persisted tables
     * @return The file object or null if the file does not exist.
     */
    public static File getFilePath(String _id) {
        File file = new File(new PersistedFile(_id).getPersistedFilePath());
        return file.exists() ? file : null;
    }
}
