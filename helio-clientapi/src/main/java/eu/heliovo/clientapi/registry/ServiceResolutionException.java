package eu.heliovo.clientapi.registry;

/**
 * Exception that is thrown if a service cannot be resolved.
 * @author marco soldati at fhnw ch
 *
 */
public class ServiceResolutionException extends RuntimeException {

	/**
	 * the serial id
	 */
	private static final long serialVersionUID = -689676258028693353L;

	/**
	 * Create a service resolution exception
	 * @param message the message
	 * @param cause the root cause
	 */
	public ServiceResolutionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a service resolution exception.
	 * @param message the message.
	 */
	public ServiceResolutionException(String message) {
		super(message);
	}	
}
