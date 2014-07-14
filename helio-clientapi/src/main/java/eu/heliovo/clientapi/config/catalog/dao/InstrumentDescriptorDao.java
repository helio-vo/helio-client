package eu.heliovo.clientapi.config.catalog.dao;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.model.catalog.descriptor.InstrumentDescriptor;
import eu.heliovo.shared.util.FileUtil;

/**
 * Registry that holds the configuration of the DPAS fields. 
 * 
 * @author marco soldati at fhnw ch
 * 
 */
public class InstrumentDescriptorDao extends AbstractCatalogueDescriptorDao {
    /**
     * where to cache the file
     */
    private static final String CACHE_LOCATION = "dpas_cache";

    /**
     * Column name of the "obs inst key" in the ICS table
     */
    private static final String OBSINST_KEY = "obsinst_key";
    
    /**
     * Column name of the "longname" in the ICS table.
     */
    private static final String LONGNAME = "longname";
    
    /**
	 * The logger to use.
	 */
	private static final Logger _LOGGER = Logger.getLogger(InstrumentDescriptorDao.class);

	
	/**
	 * Location of the instruments list.
	 */
	private URL instrumentsXsdUrl = FileUtil.asURL("http://www.helio-vo.eu/services/xml/instruments.xsd");

	/**
	 * Location of the ICS table.
	 */
	private URL icsTableUrl = FileUtil.asURL("http://helio.mssl.ucl.ac.uk/helio-ics/HelioQueryService?STARTTIME=1900-01-01T00:00:00Z&ENDTIME=3000-12-31T00:00:00Z&FROM=instrument");
	
	/**
	 * The pat table url
	 */
	private URL patTableUrl = FileUtil.asURL("http://helio.mssl.ucl.ac.uk/helio-dpas/HelioPatServlet");

	/**
	 * Our list of instrument Descriptors that should be cached here.
	 */
    private List<InstrumentDescriptor> domainValues;
	
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
			URL instrumentsXsd = getHelioFileUtil().getFileFromRemoteOrCache(CACHE_LOCATION, "instruments.xsd", instrumentsXsdUrl);
			assertNutNull(instrumentsXsd, "instruments");
			URL icsVoTable = getHelioFileUtil().getFileFromRemoteOrCache(CACHE_LOCATION, "full_ics_instrument.xml", icsTableUrl);
			assertNutNull(icsVoTable, "icsVoTable");
			URL patTable = getHelioFileUtil().getFileFromRemoteOrCache(CACHE_LOCATION, "patTable.csv", patTableUrl);
			assertNutNull(patTable, "patTable");
			
			// create the map of instruments
	        Map<String, InstrumentDescriptor> instrumentsMap = new LinkedHashMap<String, InstrumentDescriptor>();
			
			// next parse the ics table and enhance instrument descriptors
			enhanceInstrumentDescriptorsFromICS(instrumentsMap, icsVoTable);
			
			// now parse the pat table and add to the instrument descriptors.
			enhanceProviderInformationFromPat(instrumentsMap, patTable);
			
			// ...  and extract instruments into a map
			enhanceInstrumentDescriptorsFromInstrumentsXsd(instrumentsMap, instrumentsXsd);
			
			domainValues = createInstrumentDescriptorDomain(instrumentsMap);
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize registry: " + e.getMessage(), e);
		}
	}

	private void assertNutNull(URL url, String propertyName) {
       if (url == null) {
           throw new IllegalStateException("Failed to load '" + propertyName + "' from local cache or remote.");
       }
    }
    /**
     * Extract the instruments from the instruments DOM. 
     * @param dInstruments the instruments DOM
     * @return a map with all instruments, key is the instrument id, value the instrumentDescriptor.
     */
    private Map<String, InstrumentDescriptor> enhanceInstrumentDescriptorsFromInstrumentsXsd(Map<String, InstrumentDescriptor> instrumentsMap, URL instrumentsXsd) {
        InputSource instrumentsSource = getInputSource(instrumentsXsd);
        Document dInstruments = parseInputSourceToDom(instrumentsSource);
        NodeList lists = dInstruments.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "enumeration");
        for (int i = 0; i < lists.getLength(); i++) {
            Element listElement = (Element) lists.item(i);
            String instrumentName = listElement.getAttribute("value");
            String instrumentLabel = getTextContent(listElement, "documentation");
            
            if (!instrumentsMap.containsKey(instrumentName)) {
                InstrumentDescriptor instrumentDescriptor = new InstrumentDescriptor(instrumentName, instrumentLabel, null);
                instrumentDescriptor.setInInstrumentsXsd(true);
                instrumentsMap.put(instrumentName, instrumentDescriptor);
            }
        }
        return instrumentsMap;
    }

    /**
     * Parse the input source into a DOM tree and normalize the DOM tree.
     * @param instrumentsSource
     * @return a normalized DOM node
     * @throws SAXException
     * @throws IOException
     * @see Node#normalize()
     */
    private Document parseInputSourceToDom(InputSource instrumentsSource) {
        DocumentBuilder docBuilder;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            docBuilder = dbFactory.newDocumentBuilder();
            
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Unable to create DOM document builder: " + e.getMessage(), e);
        }

        Document dInstruments;
        try {
            dInstruments = docBuilder.parse(instrumentsSource);
        } catch (SAXException e) {
            throw new IllegalStateException("Unable to parse instruments.xsd: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse instruments.xsd: " + e.getMessage(), e);
        }
        dInstruments.normalize();
        return dInstruments;
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
    private void enhanceProviderInformationFromPat(
            Map<String, InstrumentDescriptor> instrumentDescriptorMap, URL pat) throws IOException,
            URISyntaxException {
        if (pat != null) {
            LineIterator it = FileUtils.lineIterator(new File(pat.toURI()), "UTF-8");
            try {
                while (it.hasNext()) {
                    // parse the current line
                    String line = it.nextLine();
                    String[] entries = line.split(",");
                    if (entries.length >= 3) {
                        String instrumentName = entries[0].trim();
                        if ("null".equals(instrumentName)) {
                            continue;
                        }
                        String provider = entries[1].trim();
                        String archive = entries[2].trim();
                        int priority = getInt(entries[3].trim());
                        
                        // get instrument descriptor ...
                        InstrumentDescriptor instrument = instrumentDescriptorMap.get(instrumentName);
                        if (instrument == null) {
                            instrument = new InstrumentDescriptor(instrumentName, instrumentName, null);
                            instrumentDescriptorMap.put(instrumentName, instrument);
                        }
                        instrument.setIsInPat(true);
                        
                        // ... and add provider from the PAT.
                        instrument.addProvider(provider, archive, priority);
                    }
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } else {                
            _LOGGER.warn("Unable to load PAT table from remote or local: " + pat);
        }
        
    }
    
    /**
     * Read the ICS and set the properties in the instrumentDescriptor
     * @param instrumentDescriptorMap the current instrumentDescriptors, new instruments may be added.
     * @param icsTable the ics table URL
     */
    private void enhanceInstrumentDescriptorsFromICS(Map<String, InstrumentDescriptor> instrumentDescriptorMap, URL icsTable) {
        StarTable table = readIntoStarTableModel(icsTable);

        int obsInstKey = findColumnPos(table, OBSINST_KEY);
        if (obsInstKey < 0) {
            throw new IllegalStateException("Unable to find column '" + OBSINST_KEY + "' in ICS table at " + icsTable);
        }
        
        int longnameCol = findColumnPos(table, LONGNAME);
        if (longnameCol < 0) {
            throw new IllegalStateException("Unable to find column '" + LONGNAME + "' in ICS table at " + icsTable);
        }
        
        for (int r = 0; r < table.getRowCount(); r++) {
            // get the current data row
            Object[] row;
            try {
                row = table.getRow(r);
            } catch (IOException e) {
                _LOGGER.warn("Failed to load row data from votable: " + e.getMessage(), e);
                continue;
            }
            
            // create or load descriptor
            String obsInstKeyName = row[obsInstKey].toString();
            InstrumentDescriptor instrumentDescriptor = instrumentDescriptorMap.get(obsInstKeyName);
            if (instrumentDescriptor == null) {
                String longname = row[longnameCol].toString();
                instrumentDescriptor = new InstrumentDescriptor(obsInstKeyName, longname, null);
                instrumentDescriptorMap.put(obsInstKeyName, instrumentDescriptor);
            }
            instrumentDescriptor.setInIcs(true);
            
            // loop over the columns ...
            for (int col = 0; col < table.getColumnCount(); col++) {
                ColumnInfo colInfo = table.getColumnInfo(col);
                Object cell = row[col];
                
                // ... and fill the current cell into the descriptor
                setCellInDescriptor(instrumentDescriptor, colInfo, cell);
            }
        }
    }

    /**
     * Find the position of a column with a given name
     * @param table the table to check
     * @param columnName the name of the column to look for
     * @return the position of the table or -1 if not found
     */
    private int findColumnPos(StarTable table, String columnName) {
        // loop over the columns
        for (int col = 0; col < table.getColumnCount(); col++) {
            // and fill the current cell into the descriptor
            ColumnInfo colInfo = table.getColumnInfo(col);
            
            // we also need to find the column name for later.
            if (columnName.equals(colInfo.getName())) {
                return col;
            }
        }
        return -1;
    }

    private List<InstrumentDescriptor> createInstrumentDescriptorDomain(Map<String, InstrumentDescriptor> instrumentDescriptorMap) {
        return new ArrayList<InstrumentDescriptor>(instrumentDescriptorMap.values());
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
	public List<InstrumentDescriptor> getDomainValues() {
        return domainValues;
    }
}
