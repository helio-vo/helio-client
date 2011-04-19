package eu.heliovo.clientapi.model.catalog.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.ivoa.xml.votable.v1.DATA;
import net.ivoa.xml.votable.v1.FIELD;
import net.ivoa.xml.votable.v1.RESOURCE;
import net.ivoa.xml.votable.v1.TABLE;
import net.ivoa.xml.votable.v1.TABLEDATA;
import net.ivoa.xml.votable.v1.TD;
import net.ivoa.xml.votable.v1.TR;
import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.heliovo.clientapi.frontend.SimpleInterface;
import eu.heliovo.clientapi.model.catalog.CatalogRegistry;
import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.FieldType;
import eu.heliovo.clientapi.model.field.FieldTypeRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.query.syncquery.impl.SyncQueryServiceFactory;
import eu.heliovo.clientapi.registry.impl.SyncServiceDescriptor;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

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
	private static final String DEFAULT_CATALOG_NAME = "goes_sxr_flare";

	/**
	 * The logger to use.
	 */
	private static final Logger _LOGGER = Logger.getLogger(HecStaticCatalogRegistry.class); 
	
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

					String fieldId = getTextContent(fieldElement, "OldFieldName"); 
					String fieldName = getTextContent(fieldElement, "FieldName");
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

					HelioField<?> field = new HelioField<Object>(fieldId == null ? fieldName: fieldId, fieldName, fieldDescription, ft);
					catalog.addField(field);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize registry: " + e.getMessage(), e);
		}
		
//		VOTABLE hecCatalogs = getHecCatalogs();
		VOTABLE hecCatalogs = null;
		if (hecCatalogs != null ) {
    		Set<String> catalogNames = new HashSet<String>();
    		
    		List<RESOURCE> resources = hecCatalogs.getRESOURCE();
    		for (RESOURCE resource : resources) {
                List<TABLE> tables = resource.getTABLE();
                for (TABLE table : tables) {
                    
                    // extract the position of hte table name field
                    List<Object> fields = table.getFIELDOrPARAMOrGROUP();
                    int rowpos = 0;
                    for (Object fieldOrGroup : fields) {
                        if (fieldOrGroup instanceof FIELD) {
                            FIELD field = (FIELD)(fieldOrGroup);
                            if ("table_name".equals(field.getName())) {
                                break;
                            }
                        }
                        rowpos++;
                    }
                    if (rowpos < fields.size()) {
                        DATA data = table.getDATA();
                        TABLEDATA tableData = data.getTABLEDATA();
                        List<TR> rows = tableData.getTR();
                        for (TR row : rows) {
                            TD cell = row.getTD().get(rowpos);
                            catalogNames.add(cell.getValue());
                        }
                    }
                }
            }
    		
    		//System.out.println(catalogNames);
    		//System.out.println(catalogNames.size() + ", " + helioCatalogMap.size());
    		
    		Set<String> toRemove = new HashSet<String>();
    		for (String catName : helioCatalogMap.keySet()) {
    		    if (catalogNames.contains(catName)) {
    		        catalogNames.remove(catName);
    		    } else {
    		        toRemove.add(catName);		        
    		    }
            }
    		//System.out.println("toRemove: " + toRemove);
    		//System.out.println("not defined: " + catalogNames);
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
	 * Get the hec_catalogs either from remote or from the local cache.
	 * @return the hec_catalogs.
	 */
	private VOTABLE getHecCatalogs() {
		// create the cache dir
		File cacheDir;
		try { 
			cacheDir = HelioFileUtil.getHelioTempDir("hec_cache");
		} catch (RuntimeException e) {
			cacheDir = null;
		}
		File cacheFile = cacheDir == null ? null : new File(cacheDir, "hec_catalogs.xml");
		
		VOTABLE votable;
		try {
			HelioQueryService hec = SyncQueryServiceFactory.getInstance().getSyncQueryService(SyncServiceDescriptor.SYNC_HEC);
			HelioQueryResult result = hec.timeQuery("1800-01-10T00:00:00", "2020-12-31T23:59:59", "catalogues", 0, 0);
			votable = result.asVOTable();
		} catch (Exception e) {
		    _LOGGER.warn("Unable to load hec catalogues from remote: " + e.getMessage(), e);
			votable = null;
		}
		
		if (cacheFile != null) {
			if (votable != null) {
				// cache VOTable
				FileWriter fileWriter = null;
				try {
					fileWriter = new FileWriter(cacheFile);
					VOTableUtils.getInstance().voTable2Writer(votable, fileWriter, false);
				} catch (IOException e) {
					_LOGGER.warn("Unable to cache '" + cacheFile + "': " + e.getMessage(), e); 
				} finally {
				    IOUtils.closeQuietly(fileWriter);
				}
			} else {
				// try to read from cache
			    FileReader  reader = null;
				try {
					reader = new FileReader(cacheFile);
					votable = VOTableUtils.getInstance().reader2VoTable(reader);
				} catch (Exception e) {
					_LOGGER.warn("Unable to load hec_catalogs.xml from cache: " + e.getMessage(), e);
				} finally {
				    IOUtils.closeQuietly(reader);
				}
			}
		}
		
		return votable;
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
