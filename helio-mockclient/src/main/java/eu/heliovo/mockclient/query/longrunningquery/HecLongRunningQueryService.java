package eu.heliovo.mockclient.query.longrunningquery;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a Hec service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
public interface HecLongRunningQueryService extends LongRunningQueryService {
	
}
 
/**
 * Service client to access the Hec Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class HecLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements HecLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	HecLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "hec-longrunningquery", "Helio Event Catalogs - Long Running Query Interface");
	}
}
