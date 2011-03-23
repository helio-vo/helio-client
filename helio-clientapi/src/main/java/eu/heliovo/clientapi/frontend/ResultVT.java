package eu.heliovo.clientapi.frontend;

import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import net.ivoa.xml.votable.v1.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class ResultVT
{
  private VOTABLE res;
  private List<List<String>> stack;
  private List<String> headers;
  public int startTime;
  public int endTime;
  public List<TableVT> tables;
  public String queryInfo;
  
  /**
   * Method added temporarily to edit directly the rows to remove when a selected result download is activated.
   */
  public VOTABLE getVOTABLE(){
	return this.res;
  }
  public int getTotalSize()
  {
    int count=0;
    for(TableVT table:tables)
    {
      count+=table.getData().size();
    }

    return count;
  }

  public class TableVT
  {
    private String tableName;
    private List<String> headers;
    private List<List<String>> data;

    public TableVT()
    {
      tableName="";
      headers=new ArrayList<String>();
      data=new ArrayList<List<String>>();

    }

    private void setName(String name)
    {
      this.tableName=name;
    }

    private void setHeaders(List<String> headersVT)
    {
      this.headers=headersVT;
    }

    private void setData(List<List<String>> dataVT)
    {
      this.data=dataVT;
    }

    public String getName()
    {
      return tableName;
    }

    public List<String> getHeaders()
    {
      return headers;
    }

    public List<List<String>> getData()
    {
      return data;
    }
  }

  public ResultVT()
  {
  }

  public ResultVT(VOTABLE res)
  {

    tables=new LinkedList<TableVT>();
    this.res=res;
    parse(res);
  }

  public List<TableVT> getTables()
  {
    return tables;
  }
/*
  public void setStringTable()
  {

  }
*/
  public String getStringTable()
  {
    try
    {
      StringWriter retBuf=new StringWriter();
      JAXBContext context=JAXBContext.newInstance(res.getClass());
      Marshaller m=context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FRAGMENT,true);
      m.marshal(res,retBuf);
      String retVal=retBuf.toString();

      return retVal;
    }
    catch(JAXBException ex)
    {
      Logger.getLogger(ResultVT.class.getName()).log(Level.SEVERE,null,ex);
      return "";
    }
  }
/*
  public void setStack(List<List<String>> stack)
  {
    this.stack=stack;
  }
  */

  public void parse(VOTABLE res)
  {
    // System.out.println("printing VOTABLE");
    headers=new ArrayList<String>();

    stack=new ArrayList<List<String>>();
    for(RESOURCE resource:res.getRESOURCE())
    {
      // System.out.println("resource " +resource.getDESCRIPTION());
      queryInfo="";
      for(Object o:resource.getINFOOrCOOSYSOrPARAM())
      {
        INFO info=(INFO)o;
        queryInfo+=info.getName()+" "+info.getValue();
      }
      
      for(TABLE table:resource.getTABLE())
      {
        // System.out.println("TABLE " +table.getName());
        TableVT tableVT=new TableVT();
        tableVT.setName(table.getName());
        LinkedList<String> headersVT=new LinkedList<String>();
        for(Object o:table.getFIELDOrPARAMOrGROUP())
        {
          FIELD f=(FIELD)o;
          headersVT.add(f.getName());

          if(!headers.contains(f.getName()))
          {
            headers.add(f.getName());
          }

        }
        tableVT.setHeaders(headersVT);
        ArrayList<List<String>> dataVT=new ArrayList<List<String>>();
        for(TR tr:table.getDATA().getTABLEDATA().getTR())
        {
          Stack<String> row=new Stack<String>();
          ArrayList<String> rowVT=new ArrayList<String>();
          for(TD td:tr.getTD())
          {
            row.add(td.getValue());
            rowVT.add(td.getValue());
          }
          stack.add(row);
          dataVT.add(rowVT);
        }
        tableVT.setData(dataVT);
        tables.add(tableVT);
      }

    }

  }
}
