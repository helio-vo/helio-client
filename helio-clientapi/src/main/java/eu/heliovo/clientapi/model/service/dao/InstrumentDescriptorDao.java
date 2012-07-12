package eu.heliovo.clientapi.model.service.dao;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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

import eu.heliovo.clientapi.model.field.descriptor.InstrumentDescriptor;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.FileUtil;

/**
 * Registry that holds the configuration of the DPAS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class InstrumentDescriptorDao {
	private static final String CACHE_FILE = "patTable.csv";

    private static final String CACHE_LOCATION = "dpas_cache";

    /**
	 * The logger to use.
	 */
	private static final Logger _LOGGER = Logger.getLogger(InstrumentDescriptorDao.class);
	
	/**
	 * Location of the instruments list.
	 */
	private URL instrumentsLocation = FileUtil.asURL("http://www.helio-vo.eu/services/xml/instruments.xsd");

	/**
	 * Location of the ICS table.
	 */
	//private URL icsTable = FileUtil.asURL("http://msslkz.mssl.ucl.ac.uk/helio-ics/HelioQueryService?STARTTIME=1900-01-01T00:00:00Z&ENDTIME=3000-12-31T00:00:00Z&FROM=instrument");
	
	/**
	 * The pat table url
	 */
	private URL patTable = FileUtil.asURL("http://msslkz.mssl.ucl.ac.uk/helio-dpas/HelioPatServlet");
	
    /**
     * The HELIO file utils
     */
    private HelioFileUtil helioFileUtil;

    private Set<InstrumentDescriptor> domainValues;
	
	/**
	 * Populate the registry
	 */
	public InstrumentDescriptorDao() {
	}

	/**
	 * Init the daos content.
	 */
	public void init() {
		try {
			// create the instruments field
			// get the instruments.xsd ...
			URL instruments = helioFileUtil.getFileFromRemoteOrCache("instrument_cache", "instruments.xsd", instrumentsLocation);
			InputSource instrumentsSource = getInputSource(instruments);
			Document dInstruments = parseInputSourceToDom(instrumentsSource);
			
			// ...  and extract instruments into a map
			Map<String, InstrumentDescriptor> instrumentsMap = createMapOfInstruments(dInstruments);
			
			// now parse the pat table and create the instrument domain descriptor.
			domainValues = createInstrumentDomainValueDescriptors(instrumentsMap);			
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
     * @return a map with all instruments, key is the instrument id, value the instrumentDescriptor.
     */
    private Map<String, InstrumentDescriptor> createMapOfInstruments(Document dInstruments) {
        Map<String, InstrumentDescriptor> instrumentsMap = new LinkedHashMap<String, InstrumentDescriptor>();
        //NodeList lists = dInstruments.getChildNodes();
        NodeList lists = dInstruments.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "enumeration");
        for (int i = 0; i < lists.getLength(); i++) {
            Element listElement = (Element) lists.item(i);
            String instrumentName = listElement.getAttribute("value");
            //System.out.println(instrumentName);
            String instrumentLabel = getTextContent(listElement, "documentation");
            InstrumentDescriptor instrumentDescriptor = new InstrumentDescriptor(instrumentName, instrumentLabel, null);
            instrumentsMap.put(instrumentName, instrumentDescriptor);
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
	 * Create a set of instrumentDescriptor domain values.
	 * @param instrumentDescriptorMap the instrument map to add the provider 
	 * @return set of instrument descriptors.
	 * @throws IOException 
	 * @throws URISyntaxException
	 */
    private Set<InstrumentDescriptor> createInstrumentDomainValueDescriptors(
            Map<String, InstrumentDescriptor> instrumentDescriptorMap) throws IOException,
            URISyntaxException {
        
        Set<InstrumentDescriptor> instrumentDomain = new LinkedHashSet<InstrumentDescriptor>();
        URL pat = helioFileUtil.getFileFromRemoteOrCache(CACHE_LOCATION, CACHE_FILE, patTable);
        if (pat != null) {
            LineIterator it = FileUtils.lineIterator(new File(pat.toURI()), "UTF-8");
            try {
                while (it.hasNext()) {
                    // parse the current line
                    String line = it.nextLine();
                    String[] entries = line.split(",");
                    if (entries.length >= 3) {
                        String instrumentName = entries[0].trim();
                        String provider = entries[1].trim();
                        String archive = entries[2].trim();
                        int priority = getInt(entries[3].trim());
                        
                        // get instrument descriptor ...
                        InstrumentDescriptor instrument = instrumentDescriptorMap.get(instrumentName);
                        if (instrument == null) {
                            instrument = new InstrumentDescriptor(instrumentName, instrumentName, null);
                            instrumentDescriptorMap.put(instrumentName, instrument);
                        }
                        
                        // ... and add provider from the PAT.
                        instrument.addProvider(provider, archive, priority);
                        instrumentDomain.add(instrument);
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
     * Parse string into integer. In case of an error return MAX_INT.
     * @param intString the string to parse
     * @return the parsed value.
     */
	private int getInt(String intString) {
	    try {
	    return Integer.parseInt(intString);
	    } catch (NumberFormatException e) {
	        return Integer.MAX_VALUE;
	    }
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
	 * Get the domain values for the allowed instruments.
	 * @return the instrument domain values.
	 */
	public Set<InstrumentDescriptor> getDomainValues() {
        return domainValues;
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
