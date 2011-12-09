package eu.heliovo.clientapi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.Tables;
import uk.ac.starlink.votable.TableElement;
import uk.ac.starlink.votable.VOElement;
import uk.ac.starlink.votable.VOStarTable;

/**
 * Tests various functions of the STULUtils class
 * 
 * @author SimonFelix
 */
public class STILUtilsTest
{
  /**
   * Tests whether tables can be read.
   */
  @Test
  public void testReadVOTables() throws Exception
  {
    //read test tables
    StarTable[] tables=STILUtils.read(getTestVOTable());
    
    //check if two tables are returned
    assertEquals(2,tables.length);
    
    //check size of first table
    assertEquals(3,tables[0].getRowCount());
    assertEquals(10,tables[0].getColumnCount());
    
    //check size of second table
    assertEquals(21,tables[1].getRowCount());
    assertEquals(13,tables[1].getColumnCount());
  }
  
  
  @Test public void testReadVOElement() throws Exception {
      VOElement top = STILUtils.readVOElement(getTestVOTable());
      
      // Find the first RESOURCE element using standard DOM methods. 
      NodeList resources = top.getElementsByTagName( "RESOURCE" );
      //check if two tables are returned
      assertEquals(2, resources.getLength());

      // get first table
      Element resource = (Element) resources.item(0);
      
      // table 1
      VOElement vResource = (VOElement) resource; 
      VOElement[] tables = vResource.getChildrenByName( "TABLE" );
      assertEquals(1, tables.length);
      
      TableElement tableEl = (TableElement) tables[0];
      // Turn it into a StarTable so we can access its data. 
      StarTable starTable = new VOStarTable( tableEl );
      // Write out the column name for each of its columns. 
      int nCol = starTable.getColumnCount();
      assertEquals(10, nCol);
      long nRow = starTable.getRowCount();
      assertEquals(3, nRow);

      // table 2
      resource = (Element) resources.item(1);
      vResource = (VOElement) resource; 
      tables = vResource.getChildrenByName( "TABLE" );
      assertEquals(1, tables.length);
      tableEl = (TableElement) tables[0];
      // Turn it into a StarTable so we can access its data. 
      starTable = new VOStarTable( tableEl );
      // Write out the column name for each of its columns. 
      nCol = starTable.getColumnCount();
      assertEquals(13, nCol);
      nRow = starTable.getRowCount();
      assertEquals(21, nRow);
      
      // Iterate through its data rows, printing out each element. 
      for ( int iCol = 0; iCol < nCol; iCol++ ) {
          String colName = starTable.getColumnInfo( iCol ).getName(); 
          System.out.print( colName + "\t" );
      } 
      System.out.println();
      
      // Iterate through its data rows, printing out each element. 
      for ( RowSequence rSeq = starTable.getRowSequence(); rSeq.next(); ) {
          Object[] row = rSeq.getRow(); for ( int iCol = 0; iCol < nCol; iCol++ ) {
              System.out.print( row[ iCol ] + "\t" ); 
          }
          System.out.println();
      }
  }
  
  
  /**
   * Tests whether concatenation/appending of tables works.
   */
  @Test
  public void testConcatenateTables() throws Exception
  {
    //read test tables
    StarTable[] tables=STILUtils.read(getTestVOTable());
    
    //check "inner join" of columns
    StarTable resultInner=STILUtils.concatenate(true,tables);
    assertEquals(tables[0].getRowCount() + tables[1].getRowCount(),resultInner.getRowCount());
    assertEquals(5,resultInner.getColumnCount());
    
    //check "outer join" of columns
    StarTable resultOuter=STILUtils.concatenate(false,tables);
    assertEquals(tables[0].getRowCount() + tables[1].getRowCount(),resultOuter.getRowCount());
    assertEquals(18,resultOuter.getColumnCount());
    
    //check concatenation of five tables
    StarTable resultFive=STILUtils.concatenate(true,tables[0],tables[0],tables[0],tables[1],tables[1]);
    assertEquals(tables[0].getRowCount()*3 + tables[1].getRowCount()*2,resultFive.getRowCount());
    assertEquals(resultInner.getColumnCount(),resultFive.getColumnCount());
  }
  
  /**
   * Tests whether creating a view with a row subset works.
   */
  @Test
  public void testRowSubsets() throws Exception
  {
    //read test tables
    StarTable[] tables=STILUtils.read(getTestVOTable());
    
    //build a subset
    StarTable rowSubset=STILUtils.rowSubset(tables[0],1,1,0);
    assertEquals(tables[0].getColumnCount(),rowSubset.getColumnCount());
    assertEquals(3,rowSubset.getRowCount());
    
    assertEquals(tables[0].getCell(1,0),rowSubset.getCell(0,0));
    assertEquals(tables[0].getCell(1,0),rowSubset.getCell(1,0));
    assertEquals(tables[0].getCell(0,0),rowSubset.getCell(2,0));
  }
  
  /**
   * Tests whether creating a view with a column subset works.
   */
  @Test
  public void testColumnSubsets() throws Exception
  {
    //read test tables
    StarTable[] tables=STILUtils.read(getTestVOTable());
    
    //build a subset
    StarTable columnSubset=STILUtils.columnSubset(tables[0],1,1,0);
    assertEquals(tables[0].getRowCount(),columnSubset.getRowCount());
    assertEquals(3,columnSubset.getColumnCount(),3);
    
    assertEquals(tables[0].getCell(0,1),columnSubset.getCell(0,0));
    assertEquals(tables[0].getCell(0,1),columnSubset.getCell(0,1));
    assertEquals(tables[0].getCell(0,0),columnSubset.getCell(0,2));
  }
  
  /**
   * Tests whether tables can be persisted, retreived again and if cleanup
   * of expired tables works.
   */
  @Test
  public void testPersistence() throws Exception
  {
    //read test tables
    StarTable[] tables=STILUtils.read(getTestVOTable());
    
    
    //persist table for the duration of two seconds
    Calendar expiration=Calendar.getInstance();
    expiration.add(Calendar.SECOND,1);
    
    String persistId=STILUtils.persist(expiration.getTime(),tables);
    
    //read again from storage
    StarTable[] readAgain=STILUtils.read(persistId);
    
    //and check for equality
    assertEquals(tables.length,readAgain.length);
    for(int i=0;i<tables.length;i++)
    {
        
        tables[i].getParameters();
        DescribedValue val = null;
        val.getInfo().getUCD();
        tables[i].getRowSequence();
        
      assertEquals(tables[i].getRowCount(),readAgain[i].getRowCount());
      assertEquals(tables[i].getColumnCount(),readAgain[i].getColumnCount());
      
      for(int row=0;row<tables[i].getRowCount();row++)
        for(int col=0;col<tables[i].getColumnCount();col++)
          assertEquals(tables[i].getCell(row,col),readAgain[i].getCell(row,col));
    }
    
    
    //wait a bit to make sure that the persisted table will be
    //no longer there afterwards
    Thread.sleep(2500);
    
    STILUtils.purgeExpiredPersistedFiles();
    
    try
    {
      STILUtils.read(persistId);
      
      throw new AssertionError("Persisted file was not purged after it's expiration");
    }
    catch(FileNotFoundException _fnfe)
    {
      //this is expected. the previously persisted file should
      //have been removed by now
    }
  }
  
  /**
   * Tests whether table and file headers can be read.
   */
  @Test
  public void testHeaderReadout() throws Exception
  {
    //read test tables
    StarTable[] tables=STILUtils.read(getTestVOTable());
    
    //can read null value?
    assertEquals(-2147483648,tables[0].getColumnInfo(0).getAuxDatumValue(Tables.NULL_VALUE_INFO,Integer.class));
    
    //can read table header?
    assertEquals("2011-05-02 11:56:16",tables[0].getParameterByName("EXECUTED_AT").getValue());
    
    //can read column description?
    assertEquals("Event identification number (HEC internal number)",tables[0].getColumnInfo(0).getDescription());
    
    //can read table description?
    //TODO: reading DESCRIPTION elements does not yet work
    //assertEquals("Helio HEC time based query V1.16.25",tables[0].getParameterByName("Description").getValue());    
    
    //can read file header?
    String expectedQueryURL1="http://festung1.oats.inaf.it:8080/helio-hec/HelioQueryService?STARTTIME=2003-01-01T00:00:00&ENDTIME=2003-01-03T00:00:00&FROM=goes_sxr_flare";
    assertEquals(expectedQueryURL1,tables[0].getParameterByName("QUERY_URL").getValue());
    
    String expectedQueryURL2="http://festung1.oats.inaf.it:8080/helio-hec/HelioQueryService?STARTTIME=2003-01-01T00:00:00&ENDTIME=2003-01-03T00:00:00&FROM=rhessi_hxr_flare";
    assertEquals(expectedQueryURL2,tables[1].getParameterByName("QUERY_URL").getValue());
  }
  
  private URL getTestVOTable()
  {
    String resource = "/eu/heliovo/clientapi/utils/resource/testdata_2_tables.xml";
    URL resultFile = getClass().getResource(resource);
    assertNotNull("resource not found: " + resource, resultFile);
    return resultFile;
  }
}
