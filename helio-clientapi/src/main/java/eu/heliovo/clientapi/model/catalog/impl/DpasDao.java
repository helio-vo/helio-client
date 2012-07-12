package eu.heliovo.clientapi.model.catalog.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.DomainValueDescriptorUtil;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.FileUtil;

/**
 * Registry that holds the configuration of the DPAS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class DpasDao extends AbstractDao implements HelioCatalogDao {
	/**
	 * The logger to use.
	 */
	private static final Logger _LOGGER = Logger.getLogger(DpasDao.class);
	
	/**
	 * Location of the instruments list.
	 */
	private URL instrumentsLocation = FileUtil.asURL("http://www.helio-vo.eu/services/xml/instruments.xsd");
	
	/**
	 * The pat table url
	 */
	private URL patTable = FileUtil.asURL("http://msslkz.mssl.ucl.ac.uk/helio-dpas/HelioPatServlet");
	    
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
     * The HELIO file utils
     */
    private HelioFileUtil helioFileUtil;
	
	/**
	 * Populate the registry
	 */
	public DpasDao() {
	}

	/**
	 * Init the daos content.
	 */
	public void init() {
		try {
			
			HelioCatalog dpas = new HelioCatalog("dpas", "Data Provider Access Service", "Catalog to access data form various sources in an unified way.");
			dpas.addField(new HelioField<Object>("startTime", "startTime", "start value of the time range to query", getFieldTypeRegistry().getType("string")));
			dpas.addField(new HelioField<Object>("endTime", "endTime", "end value of the time range to query", getFieldTypeRegistry().getType("string")));
			
			// create the instruments field
			// get the instruments.xsd ...
			URL instruments = helioFileUtil.getFileFromRemoteOrCache("dpas_cache", "instruments.xsd", instrumentsLocation);
			InputSource instrumentsSource = getInputSource(instruments);
			Document dInstruments = parseInputSourceToDom(instrumentsSource);
			
			// ...  and extract instruments into a map
			Map<String, String> instrumentsMap = createMapOfInstruments(dInstruments);
			
			// now parse the pat table and create the instrument domain descriptor.
			Set<DomainValueDescriptor<String>> instrumentDomain = createInstrumentDomainValueDescriptors(instrumentsMap);
			
			@SuppressWarnings("unchecked")
			HelioField<String> instrumentsField  = new HelioField<String>("instrument", "Instrument", "Instruments supported by HELIO", getFieldTypeRegistry().getType("string"), instrumentDomain.toArray(new DomainValueDescriptor[instrumentDomain.size()]));
			
			dpas.addField(instrumentsField);
			
			add(dpas);
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize registry: " + e.getMessage(), e);
		}
	}

	/**
	 * Parse the input source into a DOM tree and normalize the DOM tree.
	 * @param instrumentsSource
	 * @return a normalized DOM node
	 * @throws SAXException
	 * @throws IOException
	 * @see Node#normalize()
	 */
    private Document parseInputSourceToDom(InputSource instrumentsSource) throws SAXException, IOException {
        DocumentBuilder docBuilder;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            docBuilder = dbFactory.newDocumentBuilder();
            
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to create DOM document builder: " + e.getMessage(), e);
        }

        Document dInstruments = docBuilder.parse(instrumentsSource);
        dInstruments.normalize();
        return dInstruments;
    }
    
    /**
     * Extract the instruments from the instruments DOM. 
     * @param dInstruments the instruments DOM
     * @return a map with all instruments, key is the instrument id, value the instrument label.
     */
    private Map<String, String> createMapOfInstruments(Document dInstruments) {
        Map<String, String> instrumentsMap = new HashMap<String, String>();
        //NodeList lists = dInstruments.getChildNodes();
        NodeList lists = dInstruments.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "enumeration");
        for (int i = 0; i < lists.getLength(); i++) {
            Element listElement = (Element) lists.item(i);
            String instrumentName = listElement.getAttribute("value");
            //System.out.println(instrumentName);
            String instrumentLabel = getTextContent(listElement, "documentation");
            instrumentsMap.put(instrumentName, instrumentLabel);
        }
        return instrumentsMap;
    }


	/**
	 * Extract the text content from the first matching tag with a given name.
	 * @param fieldElement the parent element to analyze.
	 * @param tagName the name of the tag.
	 * @return the extracted text.
	 */
	private String getTextContent(Element fieldElement, String tagName) {
		if (fieldElement == null) {
			return null;
		}
		NodeList tag = fieldElement.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", tagName);
		if (tag == null || tag.getLength() == 0) {
			return null;
		}
		return tag.item(0).getTextContent().trim();
	}
	
	/**
	 * Create a set of instrument domain values containing the instrument id and the label.
	 * @param instrumentsMap the instrument map used to lookup the labels
	 * @return set of instrument descriptors.
	 * @throws IOException 
	 * @throws URISyntaxException
	 */
    private Set<DomainValueDescriptor<String>> createInstrumentDomainValueDescriptors(
            Map<String, String> instrumentsMap) throws IOException,
            URISyntaxException {
        Set<DomainValueDescriptor<String>> instrumentDomain = new LinkedHashSet<DomainValueDescriptor<String>>();
        URL pat = helioFileUtil.getFileFromRemoteOrCache("dpas_cache", "patTable.csv", patTable);
        if (pat != null) {
            LineIterator it = FileUtils.lineIterator(new File(pat.toURI()), "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    String[] entries = line.split(",");
                    if (entries.length >= 3) {
                        String instrumentName = entries[0].trim();
                        String instrumentLabel = instrumentsMap.get(instrumentName);
                        instrumentLabel = instrumentLabel == null ? instrumentName : instrumentLabel;
                        DomainValueDescriptor<String> instrumentDesc = DomainValueDescriptorUtil.asDomainValue(instrumentName, instrumentLabel, null);
                        instrumentDomain.add(instrumentDesc);
                    }
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } else {                
            _LOGGER.warn("Unable to load PAT table from remote or local: " + patTable);
        }
        return instrumentDomain;
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
		        getFieldTypeRegistry().getType("string"), catValueDomain, defaultCatalog);
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
	
	@Override
	public HelioServiceName getServiceName() {
	    return HelioServiceName.DPAS;
	}

    /**
     * @return the helioFileUtil
     */
    @Required
    public HelioFileUtil getHelioFileUtil() {
        return helioFileUtil;
    }

    /**
     * @param helioFileUtil the helioFileUtil to set
     */
    public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
        this.helioFileUtil = helioFileUtil;
    }
}
