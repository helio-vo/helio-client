package eu.heliovo.clientapi.query.paramquery.serialize;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Wrapper exception for a set of query serialization exceptions.
 * @author marco soldati at fhnw ch
 *
 */
public class MultiQuerySerializationException extends QuerySerializationException {
	/**
	 * the serial version id.
	 */
	private static final long serialVersionUID = -7349617248557595553L;
	
	/**
	 * List of exception
	 */
	private final List<QuerySerializationException> exceptions;

	public MultiQuerySerializationException(List<QuerySerializationException> exceptions) {
		super("Multiple exception occured while processing the query");
		this.exceptions = exceptions;
	}


	@Override
	public void printStackTrace(PrintStream s) {
		for (QuerySerializationException exception : exceptions) {
			exception.printStackTrace(s);
		}
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		for (QuerySerializationException exception : exceptions) {
			exception.printStackTrace(s);
		}
	}
}
