package eu.heliovo.clientapi.registry;

/**
 * Types of known HELIO Services
 * @author MarcoSoldati
 *
 */
public enum HelioServiceType {
	/**
	 * Services that implement the long running query service interface
	 */
	LONGRUNNING_QUERY_SERVICE,
	
	/**
	 * Synchronous helio query services
	 */
	SYNC_QUERY_SERVICE,
	
	/**
	 * Unknown service type
	 */
	UNKNOWN_SERVICE;
}
