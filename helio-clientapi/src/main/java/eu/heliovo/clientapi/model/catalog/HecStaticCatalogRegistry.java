package eu.heliovo.clientapi.model.catalog;

import java.math.BigInteger;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.w3c.dom.*;

import eu.heliovo.clientapi.frontend.SimpleInterface;
import eu.heliovo.clientapi.model.field.*;

/**
 * Registry that holds the configuration of the HEC catalogs.
 * The insert order of the catalogs is preserved. The registry guarantees that these catalogs are returned in the same
 * sort order.
 * @author marco soldati at fhnw ch
 *
 */
public class HecStaticCatalogRegistry implements CatalogRegistry {
	/**
	 * The registry instance
	 */
	private static CatalogRegistry instance;
	
	/**
	 * Get the singleton instance of the catalog registry
	 * @return
	 */
	public static synchronized CatalogRegistry getInstance() {
		if (instance == null) {
			instance = new HecStaticCatalogRegistry();
		}
		return instance;
	}
	
	/**
	 * The map of catalogs. Use method {@link #add(HelioCatalog)} to add new elements.
	 */
	private final Map<String, HelioCatalog> helioCatalogMap = new LinkedHashMap<String, HelioCatalog>();
	
	/**
	 * The field type registry to use
	 */
	private FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();
	
	/**
	 * Populate the registry
	 */
	private HecStaticCatalogRegistry() {
    try
    {
      DocumentBuilder docBuilder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
      
      //parse catalog descriptions
      Document dLists=docBuilder.parse(SimpleInterface.class.getResourceAsStream("/HEC_Lists.xml"));
      Document dFields=docBuilder.parse(SimpleInterface.class.getResourceAsStream("/HEC_Fields.xml"));
      dLists.normalize();
      dFields.normalize();
      
      //create xpath
      XPath xpath=XPathFactory.newInstance().newXPath();
      
      NodeList lists=dLists.getElementsByTagName("HEC_Lists");
      for(int i=0;i<lists.getLength();i++)
      {
        Element listElement=(Element)lists.item(i);
        
        //create catalog
        int catInternalId=Integer.parseInt(listElement.getElementsByTagName("ListDBID").item(0).getTextContent());
        String catName=listElement.getElementsByTagName("ListID").item(0).getTextContent();
        String catLabel=listElement.getElementsByTagName("ListName").item(0).getTextContent();
        String catDescription=listElement.getElementsByTagName("ListDesc").item(0).getTextContent();
        HelioCatalog catalog=new HelioCatalog(catName,catLabel,catDescription);
        
        System.out.println(catName);
        
        //find associated fields
        NodeList fields=(NodeList)xpath.evaluate("//HEC_Fields[ListDBID="+catInternalId+"]",dFields,XPathConstants.NODESET);
        for(int j=0;j<fields.getLength();j++)
        {
          Element fieldElement=(Element)fields.item(j);
          
          String fieldId=fieldElement.getElementsByTagName("OldFieldName").item(0).getTextContent();
          String fieldName=fieldElement.getElementsByTagName("FieldName").item(0).getTextContent();
          
          String fieldDescription=null;
          NodeList nl=fieldElement.getElementsByTagName("FieldDesc");
          if(nl.getLength()>0)
            fieldDescription=nl.item(0).getTextContent();
          
          String fieldDataType=fieldElement.getElementsByTagName("FieldDataType").item(0).getTextContent();
          
          
          FieldType ft=null;
          if("integer".equalsIgnoreCase(fieldDataType))
            ft=fieldTypeRegistry.getType("int");
          else if("real".equalsIgnoreCase(fieldDataType))
            ft=fieldTypeRegistry.getType("double");
          else if("text".equalsIgnoreCase(fieldDataType))
            ft=fieldTypeRegistry.getType("string");
          else if("ISO8601 Time".equalsIgnoreCase(fieldDataType))
            ft=fieldTypeRegistry.getType("dateTime");
          else if("Special - Xclass".equalsIgnoreCase(fieldDataType))
            ft=fieldTypeRegistry.getType("xclass");
          else if("Special - Oclass".equalsIgnoreCase(fieldDataType))
            ft=fieldTypeRegistry.getType("oclass");
          else
            ft=fieldTypeRegistry.getType(fieldDataType);
          
          /*
            <FieldDBID>5</FieldDBID>
            <OldFieldName>goes_id</OldFieldName>
            <FieldType>Private</FieldType>
            <FieldEntity>Index</FieldEntity>
            <FieldProperty>Catalogue</FieldProperty>
            <FieldUnits>HEC id</FieldUnits>
            <FieldSIConv>1&gt;Unitless</FieldSIConv>
            <FieldProdNotes>rename to standard id (e.g. hec_id)</FieldProdNotes>
           */
          
          System.out.println("      "+fieldName);
          
          catalog.addField(new HelioField<Object>(fieldId,fieldName,fieldDescription,ft));
        }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
	  
	  
		HelioCatalog catalog = new HelioCatalog("goes_xray_flare", "goes_xray_flare", "Goes list of xray flares");
		catalog.addField(new HelioField<Date>("ntime_start", "ntime_start", "selection start time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("time_start", "time_start", "event start time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("time_peak", "time_peak", "event peak time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("time_end", "time_end", "event end time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<Date>("ntime_end", "ntime_end", "selection end time", fieldTypeRegistry.getType("dateTime")));
		catalog.addField(new HelioField<BigInteger>("nar", "nar", "active region number", fieldTypeRegistry.getType("integer")));
		catalog.addField(new HelioField<Double>("latitude", "latitude", "heliographic latitude", fieldTypeRegistry.getType("decimal")));
		catalog.addField(new HelioField<Double>("longitude", "longitude", "heliographic longitude", fieldTypeRegistry.getType("decimal")));
		catalog.addField(new HelioField<Double>("long_carr", "long_carr", "Carrington longitude", fieldTypeRegistry.getType("decimal")));
		catalog.addField(new HelioField<String>("xray_class", "xray_class", "x-ray importance class", fieldTypeRegistry.getType("string")));
		catalog.addField(new HelioField<String>("optical_class", "optical_class", "optical importance class", fieldTypeRegistry.getType("string")));
		
		add(catalog);
		
		catalog = new HelioCatalog("test_catalog", "Test Catalog", "catalog for testing purposed only.");
		catalog.addField(new HelioField<Integer>("delay", "delay", "Add an artifical delay in milliseconds to the service. For testing purposes only.", fieldTypeRegistry.getType("integer"), new Integer(0)));
		catalog.addField(new HelioField<Float>("exception-probability", "exception-probability", "Add a probability that the service throws an exception. " +
				"if <=0: never throw an exception, if >= 1.0: always throw an exception. " +
				"For testing purposes only.", fieldTypeRegistry.getType("float"), new Float(0)));
		add(catalog);
	}

	/**
	 * Add a catalog to the map.
	 * @param catalog the catalog to add.
	 * @throws IllegalArgumentException if a catalog with the same name has been added before.
	 */
	private void add(HelioCatalog catalog) throws IllegalArgumentException {
		HelioCatalog oldCatalog = helioCatalogMap.put(catalog.getCatalogName(), catalog);
		if (oldCatalog != null) {
			throw new IllegalArgumentException("Catalog with name " + catalog.getCatalogName() + " has been previously registered. Old catalog: " + oldCatalog + ", new catalog: " + catalog);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.model.catalog.CatalogRegistry#getCatalogField()
	 */
	@Override
	public HelioField<String> getCatalogField() {
		// we should probably cache thie field domain values as this method is rather expensive.
		Collection<HelioCatalog> cat = helioCatalogMap.values();
		@SuppressWarnings("unchecked")
		DomainValueDescriptor<String>[] catValueDomain = (DomainValueDescriptor<String>[]) cat.toArray(new DomainValueDescriptor<?>[cat.size()]);
		HelioField<String> catalogField = new HelioField<String>(
				"hec_catalog", 
				"catalog",
				"catalog",
				"Generated field that defines the domain of allowed catalogs", 
				fieldTypeRegistry.getType("string"), 
				catValueDomain, 
				null);
		return catalogField;
	}

	/* (non-Javadoc)
	 * @see eu.heliovo.clientapi.model.catalog.CatalogRegistry#getFields(java.lang.String)
	 */
	@Override
	public HelioField<?>[] getFields(String catalogId) {
		HelioCatalog catalog = helioCatalogMap.get(catalogId);
		if (catalog == null) {
			return null;
		}
		return catalog.getFields();
	}
}
