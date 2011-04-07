package eu.heliovo.clientapi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * A set of utility methods to deal with VOTables.
 * @author MarcoSoldati
 *
 */
public class VOTableUtils {
	/**
	 * A logger to use
	 */
	private static final Logger _LOGGER = Logger.getLogger(VOTableUtils.class);
	
	/**
	 * Hold the singleton instance
	 */
	private static final VOTableUtils instance = new VOTableUtils();
	
	/**
	 * Get the singleton instance of the vo tables
	 * @return the utils
	 */
	public static VOTableUtils getInstance() {
		return instance;
	}
	
	/**
	 * The context to use
	 */
	private final JAXBContext jaxbContext;
	
	/**
	 * The docbuilder factor to use for DOM creation
	 */
    private final DocumentBuilderFactory docBuilderFactory;
    
	/**
	 * Create the VOTableUtils bean.
	 */
	private VOTableUtils() {
		String packageName = VOTABLE.class.getPackage().getName();
		try {
			jaxbContext = JAXBContext.newInstance(packageName);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to setup JAXBContext: " + e.getMessage(), e);
		}
		docBuilderFactory = DocumentBuilderFactory.newInstance();
	}
	
	/**
	 * Convert a VOTable object to a string
	 * @param voTable the voTable to convert
	 * @param formatted should the output be formatted, i.e. intended?
	 * @return the voTable as String
	 * @throws JobExecutionException
	 */
	public String voTable2String(VOTABLE voTable, boolean formatted) throws JobExecutionException {
		StringWriter sw = new StringWriter();
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(voTable, sw);
			return sw.toString();
		} catch (JAXBException e) {
			throw new RuntimeException("JAXBException while converting to String: " + e.getMessage(), e);
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				_LOGGER.warn("Unable to close string writer: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Marshal a VOTable to a writer. The writer will be closed by this method.
	 * @param voTable the voTable to synchronize.
	 * @param writer the writer to write to.
	 * @param formatted should the output be formatted, i.e. intended?
	 */
	public void voTable2Writer(VOTABLE voTable, Writer writer, boolean formatted) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(voTable, writer);
		} catch (JAXBException e) {
			throw new RuntimeException("JAXBException while converting to String: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	/**
	 * Convert an input stream to a vo table
	 * @param stream the input stream
	 * @return the marshalled voTable
	 * @throws JAXBException if the table cannot be created.
	 */
	public VOTABLE stream2VoTable(InputStream stream) throws JAXBException {
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<VOTABLE> doc = (JAXBElement<VOTABLE>)unmarshaller.unmarshal(new StreamSource(stream), VOTABLE.class);
			return doc.getValue();
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	/**
	 * Convert a reader to a votable by using JAXB
	 * @param reader the input reader
	 * @return the marshalled voTable
	 * @throws JAXBException if the table cannot be created.
	 */
	public VOTABLE reader2VoTable(Reader reader) throws JAXBException {
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<VOTABLE> doc = (JAXBElement<VOTABLE>)unmarshaller.unmarshal(new StreamSource(reader), VOTABLE.class);
			return doc.getValue();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	/**
	 * Convert a JAXB object to a DOM document.
	 * @param pObject
	 * @return
	 * @throws JAXBException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
    public Document asDOMDocument(Object pObject)
                throws JAXBException, ParserConfigurationException {
    	DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    	Document result = docBuilder.newDocument();

    	Marshaller marshaller = jaxbContext.createMarshaller();
    	marshaller.marshal(pObject, result);

    	return result;
    }
}
