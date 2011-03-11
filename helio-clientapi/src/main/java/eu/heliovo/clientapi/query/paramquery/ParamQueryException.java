package eu.heliovo.clientapi.query.paramquery;

import eu.heliovo.clientapi.HelioClientException;

/**
 * Exception that will be thrown if any problem in a param query occurs.
 * @author marco soldati at fhnw ch
 *
 */
public class ParamQueryException extends HelioClientException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8549106951419627320L;

	/**
	 * Create the param query exception with a message.
	 * @param message
	 */
	public ParamQueryException(String message) {
		super(message);
	}

	/**
	 * Create the exception with a wrapped cause
	 * @param cause the cause
	 */
	public ParamQueryException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create the exception with a wrapped cause and a message
	 * @param message the message.
	 * @param cause the cause.
	 */
	public ParamQueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
