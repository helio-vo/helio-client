package eu.heliovo.mockclient.query.longrunningquery;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a ILS service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
public interface IlsLongRunningQueryService extends LongRunningQueryService {
	
}
 
/**
 * Service client to access the ILS Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class IlsLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements IlsLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	IlsLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "ils-longrunningquery", "Instrument Location Service - Long Running Query Interface");
	}
}
