package eu.heliovo.clientapi.monitor;

/**
 * Methods to query status information from the montor service.
 * This service is intended for administrators to check the system health.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioMonitor {
	
	/**
	 * Get the status of a specific service.
	 * @param serviceId the id of the service to get the status for. Must not be null or empty.
	 * @return the status or null if the status cannot be loaded.
	 * @throws IllegalArgumentException if the service with id 'serviceId' does not exist or if serviceId is null.
	 */
	public MonitoredServiceStatus getStatus(String serviceId) throws IllegalArgumentException; 

}
