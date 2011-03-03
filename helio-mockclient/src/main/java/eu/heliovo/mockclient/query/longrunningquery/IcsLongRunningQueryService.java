package eu.heliovo.mockclient.query.longrunningquery;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a ICS service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
public interface IcsLongRunningQueryService extends LongRunningQueryService {
	
}
 
/**
 * Service client to access the Ics Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class IcsLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements IcsLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	IcsLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "ics-longrunningquery", "Instrument Capabilites Serivce - Long Running Query Interface");
	}
}
