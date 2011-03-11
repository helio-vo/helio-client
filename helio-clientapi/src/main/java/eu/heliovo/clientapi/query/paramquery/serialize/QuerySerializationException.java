package eu.heliovo.clientapi.query.paramquery.serialize;

import eu.heliovo.clientapi.HelioClientException;

/**
 * RuntimeException that is thrown in case the query cannot be serialized. 
 * @author marco soldati at fhnw ch
 *
 */
public class QuerySerializationException extends HelioClientException {

	/**
	 * the version uid.
	 */
	private static final long serialVersionUID = -1009642941657172103L;

	/**
	 * Create an expetion with a message and a wrapped cause.
	 * @param message the message
	 * @param cause the cause
	 */
	public QuerySerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create an exception with the message
	 * @param message the message.
	 */
	public QuerySerializationException(String message) {
		super(message);
	}
	
	/**
	 * Create an exception with a wrapped cause
	 * @param cause the cause
	 */
	public QuerySerializationException(Throwable cause) {
		super(cause);
	}

}
