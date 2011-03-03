package eu.heliovo.mockclient.query.longrunningquery;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a HFC service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
public interface HfcLongRunningQueryService extends LongRunningQueryService {
	
}
 
/**
 * Service client to access the HFC Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class HfcLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements HfcLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	HfcLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "hfc-longrunningquery", "Helio Feature Catalog - Long Running Query Interface");
	}
}
