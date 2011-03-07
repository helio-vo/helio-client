package eu.heliovo.clientapi.frontend;

import java.io.IOException;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.ws.BindingProvider;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import net.ivoa.xml.votable.v1.VOTABLE;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.helio_vo.xml.queryservice.v0.HelioQueryServiceService;

public class SimpleInterface
{
  static class Catalog
  {
    int dbid;
    String id;
    String name;
    String purpose;
    String description;
    String notes;
  }
  
  static Map<String,Catalog> catalogs;
  
  static
  {
    /*catalogs=new LinkedHashMap<String,SimpleInterface.Catalog>();
    
    try
    {
      DocumentBuilder docBuilder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
      
      //parse catalog descriptions
      Document dLists=docBuilder.parse(SimpleInterface.class.getResourceAsStream("/HEC_Fields.xml"));
      dLists.normalize();
      
      NodeList nl=dLists.getElementsByTagName("HEC_Lists");
      for(int i=0;i<nl.getLength();i++)
      {
        Element e=(Element)nl.item(i);
        
        Catalog c=new Catalog();
        System.out.println(e.getElementsByTagName("ListDBID").item(0).getNodeValue());
        //c.dbid=e.getElementsByTagName("ListDBID").item(0).getNodeValue();
        catalogs.put(c.id,c);
      }
      
      //parse field descriptions
      Document dFields=docBuilder.parse("/resources/HEC_Fields.xml");
      dFields.normalize();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }*/
  }
  
  public static ResultVT queryService(List<String> minDate,List<String> maxDate,List<String> from,String portAddress,String where)
  {
    int maxrecords=0;
    int startindex=0;
    
    if(minDate.size()==0)
      return null;
    
    if(maxDate.size()==0)
      return null;
    
    HelioQueryServiceService service=new HelioQueryServiceService();
    HelioQueryService port=service.getHelioQueryServicePort();
    int numberOfDatePairs=minDate.size();
    int numberOfFromSingles=from.size();
    
    if(portAddress.equals(PortDirectory.DPAS))
    {
      minDate = normalizeList(numberOfFromSingles,minDate);
      maxDate = normalizeList(numberOfFromSingles,maxDate);
      from = normalizeList(numberOfDatePairs,from);
    }
    
    BindingProvider bp = (BindingProvider) port;
    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, portAddress);
    VOTABLE result = port.query(minDate,maxDate, from,where,null, maxrecords, startindex, null);
    
    
    ResultVT resvt= new ResultVT(result);
    
    return resvt;
  }
  
  public static String test(List<String> minDate,List<String> maxDate,List<String> from,String portAddress,String where)
  {
    return "test: "+portAddress+" "+where;
  }
  
  //TODO: need to check if functionality will hold for propagation model usecase
  private static List<String> normalizeList(int max,List<String> list)
  {
      List<String> result=new ArrayList<String>();
      
      for(int i=0;i<max;i++)
       result.addAll(list);
      
      return result;
  }
}
