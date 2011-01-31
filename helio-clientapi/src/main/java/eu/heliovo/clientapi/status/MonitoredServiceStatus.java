package eu.heliovo.clientapi.status;

/**
 * Data container that holds status information from the monitor.
 * @author marco soldati at fhnw ch
 *
 */
public class MonitoredServiceStatus {
	/**
	 * Status of the service.
	 * @author marco soldati at fhnw ch
	 *
	 */
	public enum Status {
		/**
		 * Status is running under normal conditions
		 */
		RUNNING,
		
		/**
		 * Service is running but not stable. E.g.&nbsp;it does not return the expected result.
		 */
		INSTABLE,
		
		/**
		 * Service status is not known.
		 */
		UNKNOWN
	}
	
	private final Status status;
	
	
	private final long averageResonseTime;
	
	/**
	 * the service id
	 */
	private final String serviceId; 
	
	/**
	 * Create the status object.
	 * @param serviceId the id of the service being monitored. Must not be null.
	 * @param status the current status of the service being monitored. Must not be null.
	 * @param averageResponseTime average response time as collected by the monitor.
	 */
	public MonitoredServiceStatus(String serviceId, Status status, long averageResponseTime) {
		this.serviceId = serviceId;
		this.status = status;
		averageResonseTime = averageResponseTime;		
	}
	
	/**
	 * Return the id of the monitored service.
	 * @return the service id. Must not be null.
	 */
	public String getServiceId() {
		return serviceId;
	}
	
	/**
	 * The current status of the service.
	 * @return the status. Must not be null.
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * The average response time as collected by the monitor service. 
	 * @return the average response time.
	 */
	public long getAverageResonseTime() {
		return averageResonseTime;
	}
}
