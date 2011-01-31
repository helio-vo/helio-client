package eu.heliovo.mockclient.query;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.infrastructure.HelioService;
import eu.heliovo.clientapi.query.HelioParamQuery;
import eu.heliovo.clientapi.result.HelioQueryResult;
import eu.heliovo.clientapi.result.JobExecutionException;
import eu.heliovo.clientapi.result.ResultId;
import eu.heliovo.clientapi.result.ResultIdFactory;
/**
 * Abstract base class for param query mock ups. 
 * @author marco soldati at fhnw ch 
 */
abstract class AbstractMockParamQueryImpl implements HelioParamQuery, HelioService {
	
	/**
	 * Classpath resource path to the VoTable.
	 */
	private final String resourcePath;
	
	/**
	 * Hold the conversion service for data type conversion.
	 */
	private ConversionService conversionService;
	
	/**
	 * Single thread executor for the query service 
	 */
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	/**
	 * Create the Mockclient.
	 * @param resourcePath
	 */
	public AbstractMockParamQueryImpl(String resourcePath) {
		this.resourcePath = resourcePath;		
	}

	@Override
	public HelioQueryResult query(final Map<String, ? extends Object> params) throws JobExecutionException {
		if (params == null) {
			throw new IllegalArgumentException("Argument 'params' must not be null. Please specify a map.");
		}

		final long delay = getAs(params, "delay", Long.class, 0l);
		final float exceptionProbability = getAs(params, "exception-probability", Float.class, 0.0f);
		
		Future<InputStream> remoteFuture = executor.submit(new Callable<InputStream>() {
			@Override
			public InputStream call() throws Exception {					
				try {
					return performQuery(params);
				} catch (Exception e) {
					throw new JobExecutionException(e);
				} 
			}
		});		
		
		URL votable = getClass().getResource(resourcePath);
		if (votable == null) {
			throw new RuntimeException("Unable to find resource '" + resourcePath + "'");
		}
		HelioQueryResultImpl queryResultImpl;
		try {
			queryResultImpl = new HelioQueryResultImpl(
				ResultIdFactory.createResultId("marco", ResultIdFactory.newTransactionId()), 
				remoteFuture,
				votable.toURI(), 
				delay,
				exceptionProbability);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Cannot convert URL to URI: " + e.getMessage(), e);
		}
		return queryResultImpl;
	}

	/**
	 * Get object from map and convert to an appropriate type if possible.
	 * @param params the map to check
	 * @param key the key to search for
	 * @param type the excepted type
	 * @param defaultValue the defaultValue if the value is null or if it cannot be converted.
	 * @return the converted value. 
	 */
	protected <T> T getAs(Map<String, ? extends Object> params, String key, Class<T> type, T defaultValue) {
		final Object value = params.get(key);		
		if (value == null) {
			return defaultValue;
		}		
		return conversionService.convert(value, type);
	}
		

	
	/**
	 * Execute a query on the Helio system. 
	 * @param parameters the parameters for the query.
	 * @return the result a input stream to read from. 
	 * @throws Exception if anything goes wrong.
	 */
	protected abstract InputStream performQuery(Map<String, ? extends Object> parameters)
			throws Exception;

	/**
	 * Create a LogRecord for a user message at a given time.
	 * @param level the log level. May be null.
	 * @param message the message. May be null.
	 * @param e any throwable. May be null.
	 * @return
	 */
	protected static LogRecord getMessage(Level level, String message,
			Throwable e) {
		LogRecord msg = new LogRecord(level, message);
		msg.setThrown(e);
		return msg;
	}

	/**
	 * Get the conversion service
	 * @return the conversion service
	 */
	public ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Set the conversion service.
	 * @param conversionService
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	/**
	 * Mock implementation of a Helio Query Result. 
	 * @author marco soldati at fhnw ch
	 *
	 */
	 static class HelioQueryResultImpl implements HelioQueryResult {
		/**
		 * The id of the result
		 */
		private final ResultId id;
		
		/**
		 * URI pointing to the VOTable
		 */
		private final URI voTable;
		
		/**
		 * Default timeout is 10 minutes
		 */
		private final static long DEFAULT_TIMEOUT = 10 * 60000;
		
		/**
		 * Single thread executor for this result instance. 
		 */
		private final ExecutorService executor = Executors.newSingleThreadExecutor();

		/**
		 * sleep factor for long running task.
		 */
		private final long sleep;
		
		/**
		 * Probability that the current thread throws an exception.
		 */
		private final float exceptionProbability;
		
		/**
		 * the current execution phase.
		 */
		private Phase phase = Phase.PENDING;

		/**
		 * Future to access the result of a remote system call.
		 */
		private final Future<InputStream> remoteFuture;
		
		/**
		 * List that holds the user logs.
		 */
		private final List<LogRecord> userLogs = Collections.synchronizedList(new ArrayList<LogRecord>());
		
		/**
		 * Create a Helio query result
		 * @param id the assigned id
		 * @param remoteFuture the future that gives access to the remote call.
		 * @param voTable the assigned vo table.
		 * @param sleep time in milliseconds to delay the thread. Ignored if 0.
		 * @param exceptionProbability probability that an exception is thrown: &lt;0.0 for never, &gt;1.0 for always
		 */
		HelioQueryResultImpl(ResultId id, Future<InputStream> remoteFuture, URI voTable, long sleep, float exceptionProbability) {
			this.id = id;
			this.remoteFuture = remoteFuture;
			this.voTable = voTable;
			this.sleep = sleep;
			this.exceptionProbability = exceptionProbability;
		}

		@Override
		public Object asObjectModel() {
			throw new UnsupportedOperationException("Currently not supported.");
		}

		@Override
		public Object asObjectModel(long timeout, TimeUnit unit)
				throws CancellationException, InterruptedException,
				ExecutionException, TimeoutException {
			throw new UnsupportedOperationException("Currently not supported.");
		}

		@Override
		public InputStream asVOTable() throws JobExecutionException {
			return asVOTable(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
		}

		@Override
		public InputStream asVOTable(long timeout, TimeUnit unit)
				throws JobExecutionException {
			this.phase = Phase.QUEUED;
			if (remoteFuture != null) {
				try {
					InputStream result = remoteFuture.get(timeout, unit);
					phase = Phase.COMPLETED;
					return result;
				} catch (InterruptedException e) {
					userLogs.add(getMessage(Level.WARNING, "InterruptedException while loading remote data: " + e.getMessage() + ". Trying local dummy data.", e));
				} catch (ExecutionException e) {
					userLogs.add(getMessage(Level.WARNING, "ExecutionException while loading remote data: " + e.getMessage() + ". Trying local dummy data.", e));
				} catch (TimeoutException e) {
					userLogs.add(getMessage(Level.WARNING, "TimeoutException while loading remote data: " + e.getMessage() + ". Trying local dummy data.", e));
				}
			}
			
			Future<InputStream> future = executor.submit(new Callable<InputStream>() {
				@Override
				public InputStream call() throws Exception {					
					// wait a bit
					int repeat = (int)sleep / 100;
					boolean interrupted = false;
					for (int i = 0; i < repeat && !(interrupted = Thread.interrupted()); i++) {
						Thread.sleep(100);
					}
					if (interrupted) {
						throw new InterruptedException("Thread has been interrupted.");
					}
					if (Math.random() < exceptionProbability) {
						throw new RuntimeException("Random exeption occurred. This is used for testing purposes.");
					}
					
					return voTable.toURL().openStream();
				}
			});
			
			try {
				InputStream result = future.get(timeout, unit);
				phase = Phase.COMPLETED;
				return result;
			} catch (InterruptedException e) {
				phase = Phase.ERROR;
				throw new JobExecutionException(e);
			} catch (ExecutionException e) {
				phase = Phase.ERROR;
				throw new JobExecutionException(e);
			} catch (TimeoutException e) {
				phase = Phase.ERROR;
				throw new JobExecutionException(e);
			}
		}

		@Override
		public ResultId getResultId() {
			return id;
		}

		@Override
		public LogRecord[] getUserLogs() {
			return userLogs.toArray(new LogRecord[userLogs.size()]);
		}

		@Override
		public int getExecutionDuration() {
			return 0;
		}
		
		@Override
		public Date getDestructionTime() {
			return null;
		}
		
		@Override
		public Phase getPhase() {
			return phase;
		}
	}
}
