package eu.heliovo.clientapi.workerservice;

import eu.heliovo.clientapi.HelioClientException;

/**
 * Wrapper RuntimeException for any exception that occurs during execution of a 
 * long running job. The purpose of this exception is to simplify exception handling.
 * Clients can decide if they want to handle certain wrapped exceptions
 * or if they just want to ignore them.
 * @author marco soldati at fhnw ch
 */
public class JobExecutionException extends HelioClientException {
	
	/**
	 * version id to serialize this exception. 
	 */
	private static final long serialVersionUID = -4106713218178729430L;

	/**
	 * Create a new exception without a cause.
	 * @param message the message for the exception
	 */
	public JobExecutionException(String message) {
		super(message);
	}
	
	/**
	 * Create an exception with a message and a cause.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Create an exception message with a cause.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(Throwable cause) {
		super(cause);
	}
}
