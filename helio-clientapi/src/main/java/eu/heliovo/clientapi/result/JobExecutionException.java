package eu.heliovo.clientapi.result;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Wrapper RuntimeException for any exception that occurs during execution of a 
 * long running job. The purpose of this object is to allow clients to decide if they 
 * just want to ignore the exception or if they want to handle it appropriately.
 * The wrapped exceptions can be of any type specified in enum {@link Type}.
 * @author marco soldati at fhnw ch
 *
 */
public class JobExecutionException extends RuntimeException {
	
	/**
	 * version id to serialize this exception. 
	 */
	private static final long serialVersionUID = -4106713218178729430L;

	/**
	 * Type of Job Execution Exceptions.
	 *
	 */
	public enum Type {
		/**
		 * Thrown if a long running task has been canceled on the client side.
		 * The task may still be running on the server side. Use {@link HelioJob#getPhase()}
		 * to check this.
		 */
		CANCELLATION_EXCEPTION(CancellationException.class), 
		
		/**
		 * Thrown if a long running task has been interrupted by an 
		 * {@link Thread#interrupt()} signal.
		 * The task may still be running on the server side. Use {@link HelioJob#getPhase()}
		 * to check this.
		 */
		INTERRUPTED_EXCEPTION(InterruptedException.class),
		
		/**
		 * Thrown if any exception occurs during task execution. 
		 * The {@link ExecutionException} wrapped by this exception contains
		 * the root cause of the exception. 
		 */
		EXECUTION_EXCEPTION(ExecutionException.class), 
		
		/**
		 * Thrown if a time out occurs. 
		 * The task may still be running on the server side. Use {@link HelioJob#getPhase()}
		 * to check this.
		 */
	    TIMEOUT_EXCEPTION(TimeoutException.class),
	    
	    /**
	     * Throw if any IO exception occurs during execution of the task.
	     */
	    IO_EXCEPTION(IOException.class),
	    
	    /**
	     * Thrown if any unknown exception occurs.
	     */
	    EXCEPTION(Exception.class)
	    ;
		
		/**
		 * Class assigned with the enum		
		 */
		private final Class<? extends Throwable> throwableClass;

		/**
		 * Construct the type of the wrapped exception
		 * @param throwableClass the class of the wrapped exception.
		 */
		Type(Class<? extends Throwable> throwableClass) {
			this.throwableClass = throwableClass;			
		}
		
		/**
		 * Get the class of the type.
		 * @return the class
		 */
		public Class<? extends Throwable> getThrowableClass() {
			return throwableClass;
		}
	}

	/**
	 * Keep the type of the exception.
	 */
	private final Type exceptionType;
	
	/**
	 * Create an exception message of type {@link Type#CANCELLATION_EXCEPTION}.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, CancellationException cause) {
		super(message, cause);
		this.exceptionType = Type.CANCELLATION_EXCEPTION;
	}
	
	/**
	 * Create an exception message of type {@link Type#CANCELLATION_EXCEPTION}.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(CancellationException cause) {
		super(cause);
		this.exceptionType = Type.CANCELLATION_EXCEPTION;
	}

	/**
	 * Create an exception message of type {@link Type#INTERRUPTED_EXCEPTION}.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, InterruptedException cause) {
		super(message, cause);
		this.exceptionType = Type.INTERRUPTED_EXCEPTION;
	}
	
	/**
	 * Create an exception message of type {@link Type#INTERRUPTED_EXCEPTION}.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(InterruptedException cause) {
		super(cause);
		this.exceptionType = Type.INTERRUPTED_EXCEPTION;
	}

	/**
	 * Create an exception message of type {@link Type#EXECUTION_EXCEPTION}.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, ExecutionException cause) {
		super(message, cause);
		this.exceptionType = Type.EXECUTION_EXCEPTION;
	}
	
	/**
	 * Create an exception message of type {@link Type#EXECUTION_EXCEPTION}.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(ExecutionException cause) {
		super(cause);
		this.exceptionType = Type.EXECUTION_EXCEPTION;
	}

	/**
	 * Create an exception message of type {@link Type#TIMEOUT_EXCEPTION}.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, TimeoutException cause) {
		super(message, cause);
		this.exceptionType = Type.TIMEOUT_EXCEPTION;
	}
	
	/**
	 * Create an exception message of type {@link Type#TIMEOUT_EXCEPTION}.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(TimeoutException cause) {
		super(cause);
		this.exceptionType = Type.TIMEOUT_EXCEPTION;
	}
	/**
	 * Create an exception message of type {@link Type#IO_EXCEPTION}.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, IOException cause) {
		super(message, cause);
		this.exceptionType = Type.IO_EXCEPTION;
	}
	
	/**
	 * Create an exception message of type {@link Type#IO_EXCEPTION}.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(IOException cause) {
		super(cause);
		this.exceptionType = Type.IO_EXCEPTION;
	}
	/**
	 * Create an exception message of type {@link Type#EXCEPTION}.
	 * @param message the message.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(String message, Exception cause) {
		super(message, cause);
		this.exceptionType = Type.EXCEPTION;
	}
	
	/**
	 * Create an exception message of type {@link Type#EXCEPTION}.
	 * @param cause the cause exception to be wrapped.
	 */
	public JobExecutionException(Exception cause) {
		super(cause);
		this.exceptionType = Type.EXCEPTION;
	}
	
	/**
	 * Return the type of the wrapped exception. Never null. 
	 * @return the type of the wrapped exception.
	 */
	public Type getExceptionType() {
		return exceptionType;
	}
}
