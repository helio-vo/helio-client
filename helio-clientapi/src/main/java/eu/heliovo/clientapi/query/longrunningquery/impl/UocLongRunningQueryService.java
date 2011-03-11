package eu.heliovo.clientapi.query.longrunningquery.impl;

import java.net.URL;

import eu.heliovo.clientapi.query.longrunningquery.LongRunningQueryService;

/**
 * Marker interface for a Uoc service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 */
public interface UocLongRunningQueryService extends LongRunningQueryService {
	
}

/**
 * Service client to access the Uoc Service.
 * Use a {@link LongRunningQueryServiceFactory} to get an instance of this service.
 * @author marco soldati at fhnw ch
 *
 */
class UocLongRunningQueryServiceImpl extends AbstractLongRunningQueryService implements UocLongRunningQueryService {
	/**
	 * Create the service instance.
	 * @param wsdlLocation location of the service's WSDL file.
	 */
	UocLongRunningQueryServiceImpl(URL wsdlLocation) {
		super(wsdlLocation, "uoc-longrunningquery", "Unified Observing Catalogue - Long Running Query Interface");
	}
}
