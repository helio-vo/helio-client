package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a Mdes service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
public interface MdesLongRunningQueryService extends LongRunningQueryService {
	
}
 
/**
 * Service client to access the Mdes Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class MdesLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements MdesLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	MdesLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "mdes-longrunningquery", "Metadata Evaluation Service - Long Running Query Interface");
	}
}
