package eu.heliovo.mockclient.util;

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
	 * Default timeout in milliseconds to wait for a result from a call.
	 */
	private static final long DEFAULT_TIMEOUT = 2000;

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
	 * Exectue a callable in its own Thread and wait for a give time for the result.
	 * The method also handles exceptions and wraps them as JobExecutionExcepttions
	 * @param callable the callable to asynchronouly call.
	 * @param callId an identifier for  the call for user feedback.
	 * @param timeoutInMs number of milliseconds to wait for a result.
	 * @param <T> the return type of the submitted callable.
	 * @return the object returned by the callable.
	 */
	public static <T> T callAndWait(Callable<T> callable, final String callId, long timeoutInMs) throws JobExecutionException {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<T> callFuture = executor.submit(callable);
		
		T result;
		try {
			result = callFuture.get(timeoutInMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new JobExecutionException("Got interrupted while calling: " + callId + ".", e);
		} catch (ExecutionException e) {
			throw new JobExecutionException("Exception while calling: " + callId + ".", e);
		} catch (TimeoutException e) {
			throw new JobExecutionException("Unable to connect to: " + callId + ". The service may be down", e);			
		} finally {
			executor.shutdown();
		}
		return result;
	}
}
