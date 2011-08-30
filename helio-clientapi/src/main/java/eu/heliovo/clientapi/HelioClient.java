package eu.heliovo.clientapi;

import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Main object to access the HELIO API. This is implemented as facade to the underlying system.
 * Clients can either user these convenience methods or directly access the available services
 * @author marco soldati at fhnw ch
 *
 */
public class HelioClient {

	/**
	 * Get all services registered with HelioClient.
	 * @return all services registered with HelioClient or empty array if none are found.
	 */
	public HelioServiceName[] getServiceNames() {
	    return null;
	}
	
	/**
	 * Get all services that implement a specific interface or are derived from a given class.
	 * @param <T> The type of the implemented interface or the derived class.
	 * @param clazz The implemented interface or the derived class. Must not be null. 
	 * @return all {@link HelioService}s that are of the given type or an empty array, if no service matches the criterion.
	 * @throws IllegalArgumentException if 'clazz' is null.  
	 */
	public <T extends Object> T[] getServices(Class<T> clazz) throws IllegalArgumentException {
	    return null;
	};
	
	/**
	 * Get a service by its name.
	 * @param serviceName the name of the service. Must not be null.
	 * @return the service or null if not found. Must not be null.
	 * @throws IllegalArgumentException if 'serviceName' is null.  
	 */
	public HelioServiceName getServiceName(String serviceName) {
	    return HelioServiceName.valueOf(serviceName);
	}
	
	/**
	 * Get a service by its name, but only if it is of a given type.
	 * @param serviceName the name of the service. Must not be null.
	 * @param clazz the class or interface the service should implement. Must not be null.
	 * @return the {@link HelioService} or null if it cannot be found.
	 * @throws IllegalArgumentException if 'serviceName' or 'clazz' is null.
	 */
	public <T extends Object> T getService(String serviceName, Class<T> clazz) {
	    return null;
	};	
}
