package eu.heliovo.clientapi.model.catalog.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.heliovo.clientapi.model.catalog.CatalogRegistry;
import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.DomainValueDescriptorUtil;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Registry that holds the configuration of the DPAS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class DpasStaticCatalogRegistry implements CatalogRegistry {
	/**
	 * The logger to use.
	 */
	@SuppressWarnings("unused")
	private static final Logger _LOGGER = Logger.getLogger(DpasStaticCatalogRegistry.class); 
	
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
			instance = new DpasStaticCatalogRegistry();
		}
		return instance;
	}

	/**
	 * Location of the instruments list.
	 */
	private URL instrumentsLocation = HelioFileUtil.asURL("http://www.helio-vo.eu/services/xml/instruments.xsd");
	
	/**
	 * The pat table url
	 */
	private URL patTable = HelioFileUtil.asURL("http://helio-vo.eu/services/other/pat_summary.csv");
	
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
	private DpasStaticCatalogRegistry() {
		DocumentBuilder docBuilder;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Unable to create DOM document builder: " + e.getMessage(), e);
		}
		
		File instruments = HelioFileUtil.getFileFromRemoteOrCache("dpas_cache", "instruments.xsd", instrumentsLocation);
		

		try {
			InputSource instrumentsSource = getInputSource(instruments.toURI().toURL());
			
			HelioCatalog dpas = new HelioCatalog("dpas", "Data Provider Access Service", "Catalog to access data form various sources in an unified way.");
			dpas.addField(new HelioField<Object>("startTime", "startTime", "start value of the time range to query", fieldTypeRegistry.getType("string")));
			dpas.addField(new HelioField<Object>("endTime", "endTime", "end value of the time range to query", fieldTypeRegistry.getType("string")));
			
			// create the instruments field

			// parse the instruments from the instruments.xsd
			Map<String, String> instrumentsMap = new HashMap<String, String>();

			// loop over the instruments.xsd
//			Document dInstruments = docBuilder.parse(instrumentsSource);
//			dInstruments.normalize();
//			
//			//NodeList lists = dInstruments.getChildNodes();
//			NodeList lists = dInstruments.getElementsByTagName("enumeration");
//			for (int i = 0; i < lists.getLength(); i++) {
//				Element listElement = (Element) lists.item(i);
//				String instrumentName = listElement.getAttribute("value");
//				System.out.println(instrumentName);
//				String instrumentLabel = getTextContent(listElement, "documentation");
//				instrumentsMap.put(instrumentName, instrumentLabel);
//			}
			
			// now parse the pat table and create the instrument domain descriptor.
			List<DomainValueDescriptor<String>> instrumentDomain = new ArrayList<DomainValueDescriptor<String>>();
			File pat = HelioFileUtil.getFileFromRemoteOrCache("dpas_cache", "pat_summary.csv", patTable);
			LineIterator it = FileUtils.lineIterator(pat, "UTF-8");
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] entries = line.split(",");
					if (entries.length >= 3) {
						String instrumentName = entries[2].trim();
						String instrumentLabel = instrumentsMap.get(instrumentName);
						instrumentLabel = instrumentLabel == null ? instrumentName : instrumentLabel;
						DomainValueDescriptor<String> instrumentDesc = DomainValueDescriptorUtil.asDomainValue(instrumentName, instrumentLabel, null);
						instrumentDomain.add(instrumentDesc);
					}
				}
			} finally {
				LineIterator.closeQuietly(it);
			}
			
			@SuppressWarnings("unchecked")
			HelioField<String> instrumentsField  = new HelioField<String>("instrument", "Instrument", "Instruments supported by HELIO", fieldTypeRegistry.getType("string"), instrumentDomain.toArray(new DomainValueDescriptor[instrumentDomain.size()]));
			
			dpas.addField(instrumentsField);
			
			add(dpas);
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize registry: " + e.getMessage(), e);
		}
	}

	private String getTextContent(Element fieldElement, String tagName) {
		if (fieldElement == null) {
			return null;
		}
		NodeList tag = fieldElement.getElementsByTagName(tagName);
		if (tag == null || tag.getLength() == 0) {
			return null;
		}
		return tag.item(0).getTextContent();
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
		// we should probably cache the field domain values as this method is
		// rather expensive.
		Collection<HelioCatalog> cat = helioCatalogMap.values();
		@SuppressWarnings("unchecked")
		DomainValueDescriptor<String>[] catValueDomain = (DomainValueDescriptor<String>[]) cat.toArray(new DomainValueDescriptor<?>[cat.size()]);
		HelioField<String> catalogField = new HelioField<String>("dpas_catalog", "catalog", "catalog", "Generated field that defines the domain of allowed catalogs",
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
		HelioCatalog catalog = getCatalogById(catalogId);
		if (catalog == null) {
			return null;
		}
		return catalog.getFields();
	}
	
	@Override
	public HelioCatalog getCatalogById(String catalogId) {
		AssertUtil.assertArgumentHasText(catalogId, "catalogId");
		return helioCatalogMap.get(catalogId);
	}
}
