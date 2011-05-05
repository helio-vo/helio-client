package eu.heliovo.clientapi.utils;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;

import uk.ac.starlink.table.*;
import uk.ac.starlink.util.*;
import uk.ac.starlink.votable.*;

/**
 * Facade class of the STIL library. The library provides facilities to
 * - load VOTables from a variety of sources
 * - persist VOTables
 * - create views/subsets of VOTables 
 * 
 * @author SimonFelix
 */
public class STILUtils
{
  /**
   * The logger for this registry impl
   */
  private static final Logger LOGGER=Logger.getLogger(STILUtils.class);
  
  
  /**
   * The hibernate session factory
   */
  private static final SessionFactory sessionFactory=new AnnotationConfiguration().configure().buildSessionFactory();
  
  
  static
  {
    //make sure the path to persist files exists
    LOGGER.info("Creating persisted file path: "+PersistedFile.PERSISTED_FILES_PATH);
    new File(PersistedFile.PERSISTED_FILES_PATH).mkdirs();
    
    //clean up files & database
    purgeOutdatedPersistedFiles();
    cleanupPersistedFilesDirectory();
    
    LOGGER.info("Initialization of STILUtils completed");
  }
  
  
  /**
   * Concatenates several tables into a single table. Identity of columns is determined by column name.
   * 
   * Please not that this method currently is not optimized and may exhibit very bad performance
   * when joining many or large tables.
   * 
   * @param _onlyColumnsInBoth If true only columns are included that exist in all supplied tables
   * @param _table Source tables
   * @throws IOException
   * @return A single table with all the records of the supplied tables
   */
  public static StarTable concatenate(boolean _onlyColumnsInBoth,StarTable... _table) throws IOException
  {
    if(_table.length==0)
      return null;
    
    StarTable res=_table[0];
    for(int i=1;i<_table.length;i++)
    {
      StarTable a=res;
      StarTable b=_table[i];
      long totalRows=a.getRowCount()+b.getRowCount();
      
      ColumnStarTable cst=ColumnStarTable.makeTableWithRows(totalRows);
      
      //PHASE 1: add the columns of a and b combined
      if(_onlyColumnsInBoth)
      {
        //add equal columns in o(n^2)
        for(int j=0;j<a.getColumnCount();j++)
        {
          ColumnInfo colJ=a.getColumnInfo(j);
          for(int k=0;k<b.getColumnCount();k++)
          {
            ColumnInfo colK=b.getColumnInfo(k);
            
            if(colJ.getName().equals(colK.getName()))
              cst.addColumn(ArrayColumn.makeColumn(colJ,totalRows));
          }
        }
      }
      else
      {
        Set<String> columns=new HashSet<String>();
        
        //add all columns of table a
        for(int j=0;j<a.getColumnCount();j++)
        {
          ColumnInfo colJ=a.getColumnInfo(j);
          cst.addColumn(ArrayColumn.makeColumn(colJ,totalRows));
          columns.add(colJ.getName());
        }
        
        //add not yet included columns of table b
        for(int j=0;j<b.getColumnCount();j++)
        {
          ColumnInfo colJ=b.getColumnInfo(j);
          if(!columns.contains(colJ.getName()))
          {
            cst.addColumn(ArrayColumn.makeColumn(colJ,totalRows));
            columns.add(colJ.getName());
          }
        }
      }
      
      //PHASE 2: add all data to this new table
      for(int j=0;j<cst.getColumnCount();j++)
      {
        ColumnInfo colJ=cst.getColumnInfo(j);
        for(int row=0;row<totalRows;row++)
          cst.setCell(row,j,null);
        
        for(int k=0;k<a.getColumnCount();k++)
          if(a.getColumnInfo(k).getName().equals(colJ.getName()))
          {
            for(int row=0;row<a.getRowCount();row++)
              cst.setCell(row,j,a.getCell(row,k));
            break;
          }
        
        for(int k=0;k<b.getColumnCount();k++)
          if(b.getColumnInfo(k).getName().equals(colJ.getName()))
          {
            long rowOffset=a.getRowCount();
            for(int row=0;row<b.getRowCount();row++)
              cst.setCell(row+rowOffset,j,b.getCell(row,k));
            break;
          }
      }
      
      res=cst;
    }
    
    return res;
  }
  
  
  /**
   * Reads VOTables from the given data source.
   * 
   * @param _src The datasource to read the VOT from
   * @return An array containing all tables
   * @throws IOException
   */
  public static StarTable[] read(DataSource _src) throws IOException
  {
    TableSequence tables=new VOTableBuilder(false).makeStarTables(
        _src,
        StoragePolicy.getDefaultPolicy());
    
    ArrayList<StarTable> res=new ArrayList<StarTable>();
    for(StarTable table;(table=tables.nextTable())!=null;)
      res.add(table);
    
    return res.toArray(new StarTable[0]);
  }
  
  
  /**
   * Reads VOTables from a URL
   * 
   * @param _src The URL to read the VOT from
   * @return An array containing all tables
   * @throws IOException
   */
  public static StarTable[] read(URL _src) throws IOException
  {
    return read(new URLDataSource(_src));
  }
  
  
  /**
   * Reads VOTables from an input stream.
   * 
   * @param _src The stream to read the VOT from
   * @return An array containing all tables
   * @throws IOException
   */
  public static StarTable[] read(InputStream _src) throws IOException
  {
    ByteArrayOutputStream baos=new ByteArrayOutputStream(65536);
    byte[] buffer=new byte[8192];
    for(;;)
    {
      int read=_src.read(buffer);
      if(read==-1)
        break;
      
      baos.write(buffer,0,read);
    }
    return read(new ByteArrayDataSource("",baos.toByteArray()));
  }
  
  
  /**
   * Reads VOTables from the given file.
   * 
   * @param _src The file to read the VOT from
   * @return An array containing all tables
   * @throws IOException
   */
  public static StarTable[] read(File _src) throws IOException
  {
    return read(new FileDataSource(_src));
  }
  
  
  /**
   * Creates a view of a table with a subset of the columns. If you
   * pass {0,7,1,1} you'll get a view that contains the first, eigth
   * and two times second column of the source table.
   * 
   * @param _src The source table to create the view from
   * @param _columns The columns the view should contain
   * @return The view
   */
  public static StarTable columnSubset(StarTable _src,int... _columns)
  {
    return new ColumnPermutedStarTable(_src,_columns);
  }
  
  
  /**
   * Creates a view of a table with a subset of the rows. If you
   * pass {0,7,1,1} you'll get a view that contains the first, eigth
   * and two times second row of the source table.
   * 
   * @param _src The source table to create the view from
   * @param _columns The rows the view should contain
   * @return The view
   */
  public static StarTable rowSubset(StarTable _src,long... _rows)
  {
    return new RowPermutedStarTable(_src,_rows);
  }
  
  
  /**
   * Stores VOTables in persistent storage. The method
   * returns a handle to the persisted tables. This handle
   * allows you to read it at a later time.
   * 
   * The tables will be stored for at least 180 days.
   * 
   * @see #persist(StarTable[], Date)
   * @param _src The VOTables to store
   * @return Returns a handle to the persisted data.
   * @throws IOException
   */
  public static String persist(StarTable[] _src) throws IOException
  {
    Calendar expiration=Calendar.getInstance();
    expiration.add(Calendar.DATE,180);
    
    return persist(_src,expiration.getTime());
  }
  
  
  private static void cleanupPersistedFilesDirectory()
  {
    LOGGER.info("Syncing persisted files in filesystem with those in database");
    
    Session s=sessionFactory.openSession();
    
    List<?> db=s.createQuery("FROM PersistedFile").list();
    File[] fs=new File(PersistedFile.PERSISTED_FILES_PATH).listFiles();
    
    //there are more files in the filesystem than in the database --> fix the filesystem
    if(fs.length>db.size())
    {
      LOGGER.info("  There are more files in the filesystem");
      
      for(File f:fs)
      {
        PersistedFile pf=(PersistedFile)s.createQuery("FROM PersistedFile WHERE Id=?").setString(0,f.getName()).uniqueResult();
        if(pf==null)
        {
          LOGGER.info("    Deleting "+f.getAbsolutePath());
          f.delete();
        }
      }
      fs=new File(PersistedFile.PERSISTED_FILES_PATH).listFiles();
    }
    
    //if there are more files in the database than in the filesystem --> fix the db
    if(db.size()>fs.length)
    {
      LOGGER.info("  There are more files in the database");
      
      Transaction tx=s.beginTransaction();
      Arrays.sort(fs);
      for(Object o:db)
      {
        PersistedFile pf=(PersistedFile)o;
        if(Arrays.binarySearch(fs,new File(pf.getPersistedFilePath()))<0)
        {
          LOGGER.info("    Deleting "+pf.id+" from the database");
          s.delete(pf);
        }
      }
      tx.commit();
    }
    
    s.close();
  }
  
  
  private static void purgeOutdatedPersistedFiles()
  {
    Session s=sessionFactory.openSession();
    
    Transaction tx=s.beginTransaction();
    for(Object o:s.createQuery("FROM PersistedFile WHERE expires<current_timestamp").list())
    {
      PersistedFile pf=(PersistedFile)o;
      
      LOGGER.info("Purging outdated file "+pf.getPersistedFilePath());
      
      new File(pf.getPersistedFilePath()).delete();
      s.delete(pf);
    }
    
    tx.commit();
    s.close();
  }
  
  
  /**
   * Stores VOTables in persistent storage. The method
   * returns a handle to the persisted tables. This handle
   * allows you to read it at a later time.
   * 
   * The tables will be stored at least until the supplied
   * expiration date.
   * 
   * @see #persist(StarTable[], Date)
   * @param _src The VOTables to store
   * @param _expiration The expiration date
   * @return Returns a handle to the persisted data.
   * @throws IOException
   */
  public static String persist(StarTable[] _src,Date _expiration) throws IOException
  {
    purgeOutdatedPersistedFiles();
    
    QueueTableSequence tables=new QueueTableSequence();
    for(StarTable t:_src)
      tables.addTable(t);
    tables.endSequence();
    
    PersistedFile pf=PersistedFile.createNewPersistedFile(_expiration);
    new VOTableWriter(DataFormat.BINARY,true).writeStarTables(tables,new FileOutputStream(pf.getPersistedFilePath()));
    
    
    Session s=sessionFactory.openSession();
    
    Transaction tx=s.beginTransaction();
    s.save(pf);
    tx.commit();
    s.close();
    
    return pf.id;
  }
  
  
  /**
   * This method reads persisted tables. You have to
   * pass the id you got when calling {@link #persist(StarTable[], Date)}.
   * 
   * @param _id The id of persisted tables
   * @return The VOTables
   * @throws IOException
   */
  public static StarTable[] read(String _id) throws IOException
  {
    return read(new FileDataSource(new PersistedFile(_id).getPersistedFilePath()));
  }
}
