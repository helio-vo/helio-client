package eu.heliovo.clientapi;

/**
 * Basic super exception of all exceptions that can be thrown from the HELIO client API. 
 * @author marco soldati at fhnw ch
 *
 */
public class HelioClientException extends RuntimeException {
	/**
	 * the serial id.
	 */
	private static final long serialVersionUID = 6332155368357412380L;

	/**
	 * Create the exception with a message.
	 * @param message the message
	 */
	public HelioClientException(String message) {
		super(message);
	}

	/**
	 * Create the exception and wrap the cause
	 * @param cause the cause
	 */
	public HelioClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create the exception with a message and a cause
	 * @param message the message
	 * @param cause the cause
	 */
	public HelioClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
