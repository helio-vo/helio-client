package eu.heliovo.mockclient.query.longrunningquery;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a DPAS service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
public interface DpasLongRunningQueryService extends LongRunningQueryService {
	
}
 
/**
 * Service client to access the DPAS Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class DpasLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements DpasLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	DpasLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "dpas-longrunningquery", "Data Provider Access Service - Long Running Query Interface");
	}
}
