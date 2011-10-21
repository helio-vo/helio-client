package eu.heliovo.clientapi.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Helper class for asynchronous execution of jobs.
 * @author marco soldati at fhnw ch
 *
 */
public class AsyncCallUtils {

	/**
	 * Default timeout in milliseconds to block a call.
	 */
	public static final long DEFAULT_TIMEOUT = 20000;

	/**
	 * Execute a callable in its own Thread and wait for a default time ({@value #DEFAULT_TIMEOUT}ms) for the result.
	 * @param callable the callable to call in its own Thread.
	 * @param callId an identifier for  the call for user feedback
	 * @param <T> the return type of the submitted callable.
	 * @return the object returned by the callable.
	 * @throws JobExecutionException any exception is wrapped in a JobExecutionExceptions.
	 */
	public static <T> T callAndWait(Callable<T> callable, final String callId) throws JobExecutionException {
		return callAndWait(callable, callId, DEFAULT_TIMEOUT);
	}
	
	/**
	 * Execute a callable in its own Thread and wait for a give time for the result.
	 * The method also handles exceptions and wraps them as JobExecutionExcepttions
	 * @param callable the callable to asynchronously call.
	 * @param callId an identifier for  the call for user feedback.
	 * @param timeoutInMs number of milliseconds to wait for a result.
	 * @param <T> the return type of the submitted callable.
	 * @return the object returned by the callable.
	 * @throws JobExecutionException any exception is wrapped in a JobExecutonException.
	 */
	public static <T> T callAndWait(Callable<T> callable, final String callId, long timeoutInMs) throws JobExecutionException {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<T> callFuture = executor.submit(callable);
		
		T result;
		try {
			result = callFuture.get(timeoutInMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new JobExecutionException("Got interrupted while calling '" + callId + "': " + e.getMessage(), e);
		} catch (ExecutionException e) {
			throw new JobExecutionException("Exception while calling '" + callId + "': " + e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new JobExecutionException("Unable to connect to '" + callId + "' within " + MessageUtils.formatSeconds(timeoutInMs) +". The service may be down. Cause: " + e.getMessage(), e);			
		} finally {
			executor.shutdown();
		}
		return result;
	}
		
	/**
	 * Execute a callable in its own Thread and return immediately.
	 * The method also handles exceptions and wraps them as JobExecutionExcepttions
	 * @param callable the callable to asynchronously call.
	 * @param callId an identifier for  the call for user feedback.
	 * @param <T> the return type of the submitted callable.
	 * @return a future object to access the result of the call.
	 * @throws JobExecutionException if anything goes wrong.
	 */
	public static <T> Future<T> callLater(Callable<T> callable, final String callId) throws JobExecutionException {
	    ExecutorService executor = Executors.newCachedThreadPool();
	    Future<T> callFuture = executor.submit(callable);
	    executor.shutdown(); // do not accept new jobs.
	    return callFuture;
	}	
}
