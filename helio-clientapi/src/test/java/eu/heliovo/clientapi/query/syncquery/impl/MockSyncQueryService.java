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
import eu.heliovo.clientapi.loadbalancing.impl.RandomLoadBalancer;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.registryclient.impl.AccessInterfaceImpl;
import eu.heliovo.shared.util.FileUtil;

/**
 * A local mock implementation of a HELIO query service. For testing purposes only
 * @author marco soldati at fhnw ch
 *
 */
class MockSyncQueryService extends SyncQueryServiceImpl {
	
	private static final Logger _LOGGER = Logger.getLogger(MockSyncQueryService.class);

	private static final URL wsdlLocation = FileUtil.asURL("http://localhost/test/HelioQuery.wsdl");
    private static final HelioServiceName name = HelioServiceName.register("test", "ivo://test");
//	private static final String description = "a dummy test service";
    private static final AccessInterface defaultInterface = new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.SYNC_QUERY_SERVICE, wsdlLocation);
    private MockQueryServicePort port;

	public MockSyncQueryService(MockQueryServicePort port) {	
		this.setAccessInterfaces(new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.SYNC_QUERY_SERVICE, wsdlLocation));
		this.setLoadBalancer(new RandomLoadBalancer());
		this.setServiceName(name);
		this.port = port;
	}
	
	@Override
    protected AccessInterface getBestAccessInterface() {
        return defaultInterface;
    }

	@Override
	protected HelioQueryService getPort(AccessInterface accessInterface) {
	    return port;
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
