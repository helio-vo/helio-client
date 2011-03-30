package eu.heliovo.clientapi.model.catalog.impl;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import eu.heliovo.clientapi.frontend.SimpleInterface;
import eu.heliovo.clientapi.model.catalog.CatalogRegistry;
import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.field.*;

/**
 * Registry that holds the configuration of the HEC catalogs. The insert order
 * of the catalogs is preserved. The registry guarantees that these catalogs are
 * returned in the same sort order.
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class HecStaticCatalogRegistry implements CatalogRegistry {
	/**
	 * Name of the default catalog
	 */
	private static final String DEFAULT_CATALOG_NAME = "goes_xray_flare"; 
	
	/**
	 * The registry instance
	 */
	private static CatalogRegistry instance;

	/**
	 * Get the singleton instance of the catalog registry
	 * 
	 * @return
	 */
	public static synchronized CatalogRegistry getInstance() {
		if (instance == null) {
			instance = new HecStaticCatalogRegistry();
		}
		return instance;
	}

	/**
	 * Location of the HEC lists.
	 */
	private URL catalogsLocation = SimpleInterface.class.getResource("/HEC_Lists.xml");
	
	/**
	 * Location of the HEC fields.
	 */
	private URL fieldsLocation = SimpleInterface.class.getResource("/HEC_Fields.xml");
	
	/**
	 * The map of catalogs. Use method {@link #add(HelioCatalog)} to add new
	 * elements.
	 */
	private final Map<String, HelioCatalog> helioCatalogMap = new LinkedHashMap<String, HelioCatalog>();

	/**
	 * store the default catalog for this list.
	 */
	private String defaultCatalog = null;
	
	/**
	 * The field type registry to use
	 */
	private FieldTypeRegistry fieldTypeRegistry = FieldTypeRegistry.getInstance();

	/**
	 * Populate the registry
	 */
	private HecStaticCatalogRegistry() {
		DocumentBuilder docBuilder;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Unable to create DOM document builder: " + e.getMessage(), e);
		}
		
		InputSource catalogsSource = getInputSource(catalogsLocation);
		InputSource fieldsSource = getInputSource(fieldsLocation);

		try {
			
			// parse catalog descriptions
			Document dLists = docBuilder.parse(catalogsSource);
			Document dFields = docBuilder.parse(fieldsSource);
			dLists.normalize();
			dFields.normalize();

			// create xpath
			XPath xpath = XPathFactory.newInstance().newXPath();

			NodeList lists = dLists.getElementsByTagName("HEC_Lists");
			for (int i = 0; i < lists.getLength(); i++) {
				Element listElement = (Element) lists.item(i);

				// create catalog
				int catInternalId = Integer.parseInt(listElement.getElementsByTagName("ListDBID").item(0).getTextContent());
				String catName = listElement.getElementsByTagName("ListID").item(0).getTextContent();

				String catLabel = getChildValue(listElement, "ListName");
				String catDescription = getChildValue(listElement, "ListDesc");

				HelioCatalog catalog = new HelioCatalog(catName, catLabel, catDescription);
				add(catalog);
				
				if (DEFAULT_CATALOG_NAME.equals(catName)) {
					this.defaultCatalog = catName;
				}

				// find associated fields
				NodeList fields = (NodeList) xpath.evaluate("//HEC_Fields[ListDBID=" + catInternalId + "]", dFields, XPathConstants.NODESET);
				for (int j = 0; j < fields.getLength(); j++) {
					Element fieldElement = (Element) fields.item(j);

					String fieldId = fieldElement.getElementsByTagName("OldFieldName").item(0).getTextContent();
					String fieldName = fieldElement.getElementsByTagName("FieldName").item(0).getTextContent();
					String fieldDescription = getChildValue(fieldElement, "FieldDesc");
					String fieldDataType = getChildValue(fieldElement, "FieldDataType");

					FieldType ft;
					if ("integer".equalsIgnoreCase(fieldDataType))
						ft = fieldTypeRegistry.getType("int");
					else if ("real".equalsIgnoreCase(fieldDataType))
						ft = fieldTypeRegistry.getType("double");
					else if ("text".equalsIgnoreCase(fieldDataType))
						ft = fieldTypeRegistry.getType("string");
					else if ("ISO8601 Time".equalsIgnoreCase(fieldDataType))
						ft = fieldTypeRegistry.getType("dateTime");
					else if ("Special - Xclass".equalsIgnoreCase(fieldDataType))
						ft = fieldTypeRegistry.getType("xclass");
					else if ("Special - Oclass".equalsIgnoreCase(fieldDataType))
						ft = fieldTypeRegistry.getType("oclass");
					else if (fieldDataType == null)
						ft = null;
					else
						ft = fieldTypeRegistry.getType(fieldDataType);

					if (ft == null)
						ft = fieldTypeRegistry.getType("unknown");

					HelioField<?> field = new HelioField<Object>(fieldId, fieldName, fieldDescription, ft);
					catalog.addField(field);
					
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize registry: " + e.getMessage(), e);
		}

//		HelioCatalog catalog = new HelioCatalog("test_catalog", "Test Catalog", "catalog for testing purposed only.");
//		catalog.addField(new HelioField<Integer>("delay", "delay", "Add an artifical delay in milliseconds to the service. For testing purposes only.", fieldTypeRegistry
//				.getType("integer"), new Integer(0)));
//		catalog.addField(new HelioField<Float>("exception-probability", "exception-probability", "Add a probability that the service throws an exception. "
//				+ "if <=0: never throw an exception, if >= 1.0: always throw an exception. " + "For testing purposes only.", fieldTypeRegistry.getType("float"), new Float(0)));
//		add(catalog);
	}

	/**
	 * Wrap a given URL into an input source. Set the public id to the location.
	 * @param location the location.
	 * @return a new einput source object. 
	 */
	private InputSource getInputSource(URL location) {
		InputSource source;
		try {
			source = new InputSource(location.openStream());
		} catch (IOException e) {
			throw new RuntimeException("Unable to open url '" + location + "'. Cause: " + e.getMessage(), e);
		}
		source.setPublicId(location.toExternalForm());
		return source;
	}

	private String getChildValue(Element _e, String _childTag) {
		NodeList nl = _e.getElementsByTagName(_childTag);
		if (nl.getLength() > 0)
			return nl.item(0).getTextContent();

		return null;
	}

	/**
	 * Add a catalog to the map.
	 * 
	 * @param catalog
	 *            the catalog to add.
	 * @throws IllegalArgumentException
	 *             if a catalog with the same name has been added before.
	 */
	private void add(HelioCatalog catalog) throws IllegalArgumentException {
		HelioCatalog oldCatalog = helioCatalogMap.put(catalog.getCatalogName(), catalog);
		if (oldCatalog != null) {
			throw new IllegalArgumentException("Catalog with name " + catalog.getCatalogName() + " has been previously registered. Old catalog: " + oldCatalog + ", new catalog: "
					+ catalog);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.heliovo.clientapi.model.catalog.CatalogRegistry#getCatalogField()
	 */
	@Override
	public HelioField<String> getCatalogField() {
		// we should probably cache thie field domain values as this method is
		// rather expensive.
		Collection<HelioCatalog> cat = helioCatalogMap.values();
		@SuppressWarnings("unchecked")
		DomainValueDescriptor<String>[] catValueDomain = (DomainValueDescriptor<String>[]) cat.toArray(new DomainValueDescriptor<?>[cat.size()]);
		HelioField<String> catalogField = new HelioField<String>("hec_catalog", "catalog", "catalog", "Generated field that defines the domain of allowed catalogs",
				fieldTypeRegistry.getType("string"), catValueDomain, defaultCatalog);
		return catalogField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.heliovo.clientapi.model.catalog.CatalogRegistry#getFields(java.lang
	 * .String)
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
