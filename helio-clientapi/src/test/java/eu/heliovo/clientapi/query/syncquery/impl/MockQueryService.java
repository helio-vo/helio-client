package eu.heliovo.clientapi.query.syncquery.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.log4j.Logger;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.heliovo.clientapi.registry.AccessInterfaceType;
import eu.heliovo.clientapi.registry.impl.AccessInterfaceImpl;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * A local mock implementation of a HELIO query service. For testing purposes only
 * @author marco soldati at fhnw ch
 *
 */
class MockQueryService extends SyncQueryServiceImpl {
	
	private static final Logger _LOGGER = Logger.getLogger(MockQueryService.class);

	private static final URL wsdlLocation = HelioFileUtil.asURL("http://localhost/test/HelioQuery.wsdl");
	private static final String name = "test_service";
	private static final String description = "a dummy test service";

	public MockQueryService(MockQueryServicePort port) {	
		super(port, new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, wsdlLocation), name, description);
	}
	
	/**
	 * Mock implementation of a helio query service. For testing purposes.
	 * @author marco soldati at fhnw ch
	 *
	 */
	public static class MockQueryServicePort implements HelioQueryService {
		/**
		 * URL pointing to the result file.
		 */
		private final URL resultFile;

		/**
		 * Delay for the query methods before returning
		 */
		private final long queryDelay;

		
		public MockQueryServicePort(URL resultFile, long queryDelay) {
			this.resultFile = resultFile;
			this.queryDelay = queryDelay;
		}
		
		@Override
		public VOTABLE query(List<String> starttime, List<String> endtime,
				List<String> from, String where, String instrument,
				Integer maxrecords, Integer startindex, String join) {
			delay(queryDelay);
			return asVOTable(resultFile);
		}

		@Override
		public VOTABLE timeQuery(List<String> starttime, List<String> endtime,
				List<String> from, Integer maxrecords, Integer startindex) {
			delay(queryDelay);
			return asVOTable(resultFile);
			
		}
		
		private VOTABLE asVOTable(URL url) throws JobExecutionException {
			if (_LOGGER.isTraceEnabled()) {
				_LOGGER.trace("Convert URL to VOTABLE");
			}

			String packageName = VOTABLE.class.getPackage().getName();
			InputStream is;
			try {
				is = url.openStream();
			} catch (IOException e) {
				throw new RuntimeException("IOException while opening '" + url + "': " + e.getMessage(), e);
			}
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance( packageName );
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				JAXBElement<VOTABLE> doc = (JAXBElement<VOTABLE>)unmarshaller.unmarshal(new StreamSource(is), VOTABLE.class);
				return doc.getValue();
			} catch (JAXBException e) {
				throw new RuntimeException("JAXBException while reading " + url + ": " + e.getMessage(), e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					_LOGGER.warn("Unable to close input stream: " + e.getMessage(), e);
				}
			}
		}
		
		/**
		 * Sleep for delay milliseconds
		 * @param delay the delay in ms. Will be ignored if &lt;=0.
		 */
		private void delay(long delay) {
			try {
				if (delay > 0)
					Thread.sleep(delay);
			} catch (InterruptedException e) {
				// ignore
			}
		}



		
	}
}
