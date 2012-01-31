package eu.heliovo.clientapi.query.syncquery.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.helio_vo.xml.queryservice.v0.HelioQueryServiceService;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.syncquery.SyncQueryService;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Abstract base implementation of the sync query service
 * @author MarcoSoldati
 *
 */
class SyncQueryServiceImpl extends AbstractServiceImpl implements SyncQueryService {

	/**
	 * The logger instance
	 */
	private static final Logger _LOGGER = Logger.getLogger(SyncQueryServiceImpl.class);
    
	/**
	 * Name of the query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/QueryService/v0.1", "HelioQueryServiceService");

	/**
	 * The default time in ms to wait for a result.
	 */
	private static final long DEFAULT_TIMEOUT = 600000;

	/**
	 * The timeout to wait for a result.
	 */
	private long timeout = DEFAULT_TIMEOUT;
	
	/**
	 * Create the connector and open the connection to the WSDL file.
	 * @param serviceName the name of the service
	 * @param description a short text to describe the service
	 * @param accessInterfaces the locations of the wsdl files. Must not be null.
	 */
	public SyncQueryServiceImpl(HelioServiceName serviceName, String description, AccessInterface ... accessInterfaces) {
	    super(serviceName, null, description, accessInterfaces);
	}
	
	/**
	 * Create the connector and open the connection to the WSDL file. This constructor can be used to submit the port from outside. This
	 * is of particular interest for testing purposes.
	 * @param port the port to be used by this query service
	 * @param accessInterface the location of the wsdl. Must not be null
	 * @param serviceName the name of the service
	 * @param description a short text to describe the service
	 */
	public SyncQueryServiceImpl(HelioServiceName serviceName, String description, HelioQueryService port, AccessInterface accessInterface) {
	    super(serviceName, null, description, new AccessInterface[] {accessInterface});
        
	    if (!ServiceCapability.SYNC_QUERY_SERVICE.equals(accessInterface.getCapability())) {
	        throw new IllegalArgumentException("AccessInterface.Capability must be " + ServiceCapability.SYNC_QUERY_SERVICE + ", but is " + accessInterface.getCapability());
	    }
	    if (!AccessInterfaceType.SOAP_SERVICE.equals(accessInterface.getInterfaceType())) {
	        throw new IllegalArgumentException("AccessInterfaceType must be " + AccessInterfaceType.SOAP_SERVICE + ", but is " + accessInterface.getInterfaceType());
	    }
	}
	
	/**
	 * Use JAXWS to create a new service port for a given WSDL location.
	 * @param accessInterface the service endpoint
	 * @return the port to access the service.
	 */
	protected HelioQueryService getPort(AccessInterface accessInterface) {
		AssertUtil.assertArgumentNotNull(accessInterface, "accessInterface");
		
		HelioQueryServiceService queryService = new HelioQueryServiceService(accessInterface.getUrl(), SERVICE_NAME);		
		HelioQueryService port = queryService.getHelioQueryServicePort();
		if (_LOGGER.isDebugEnabled()) {
			_LOGGER.debug("Created " + port.getClass().getSimpleName() + " for " + accessInterface);
		}
		return port;
	}

	@Override
	public HelioQueryResult query(String startTime, String endTime, String from, String where, Integer maxrecords, Integer startindex, String join) throws JobExecutionException,
			IllegalArgumentException {
		HelioQueryResult result = query(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), where, maxrecords, startindex, join);
		return result;
	}
	
	@Override
	public HelioQueryResult query(final List<String> startTime, final List<String> endTime,
			final List<String> from, final String where, final Integer maxrecords,
			final Integer startindex, final String join) {
		final long jobStartTime = System.currentTimeMillis();
		
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from.size());
		}

		List<LogRecord> logRecords = new ArrayList<LogRecord>();

        Set<AccessInterface> triedInterfaces = new HashSet<AccessInterface>();
        
        while (true) {
            // get the end point
            final AccessInterface bestAccessInterface = getBestAccessInterface();
            if (!triedInterfaces.add(bestAccessInterface)) {
                throw new JobExecutionException("All registered remote services are unavailable. Tried to access: " + triedInterfaces.toString());
            };

    		String callId = bestAccessInterface.getUrl() + "::syncQuery";
    		logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));

    		StringBuilder message = new StringBuilder();
    		message.append("Executing 'result=query(");
    		message.append("startTime=").append(startTime);
    		message.append(", ").append("endTime=").append(endTime);
    		message.append(", ").append("from=").append(from);
    		message.append(", ").append("where=").append(where);
    		message.append(", ").append("maxrecords=").append(maxrecords);
    		message.append(", ").append("startIndex=").append(startindex);
    		message.append(", ").append("join=").append(join);
    		message.append(")'");
    		
			_LOGGER.info(message.toString());
			logRecords.add(new LogRecord(Level.INFO, message.toString()));

    		// wait for result
			try {
	            final HelioQueryService port = getPort(bestAccessInterface);
	            
	            // wait for the result
	            // FIXME: change to prevent marshalling the result
        		VOTABLE votable = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
        			@Override
        			public VOTABLE call() throws Exception {
                        long start = System.currentTimeMillis(); 
        				VOTABLE result = port.query(startTime, endTime, from, where, null, maxrecords, startindex, join);
                        loadBalancer.updateAccessTime(bestAccessInterface, System.currentTimeMillis() - start);
        				return result;
        			}
        		}, callId, timeout);
    		
        		if (votable == null) {
        			throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        		}
        		int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
        		HelioQueryResult result = new HelioSyncQueryResult(votable, executionDuration, logRecords);
        
        		return result;
			} catch (WebServiceException e) {
                // get port fails
                String msg = "Timeout occurred. Trying to failover.";
                logRecords.add(new LogRecord(Level.INFO, msg));
                _LOGGER.info(msg);
                loadBalancer.updateAccessTime(bestAccessInterface, -1);
            } catch (JobExecutionException e) { // handle timeout
                if (e.getCause() instanceof TimeoutException) {
                    String msg = "Timeout occurred. Trying to failover.";
                    logRecords.add(new LogRecord(Level.INFO, msg));
                    _LOGGER.info(msg);
                    loadBalancer.updateAccessTime(bestAccessInterface, -1);                 
                } else {
                    throw e;
                }
            }
        }
	}

	@Override
	public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
		HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime), Collections.singletonList(from), maxrecords, startindex);
		return result;
	}
	
	@Override
	public HelioQueryResult timeQuery(final List<String> startTime, final List<String> endTime,
			final List<String> from, final Integer maxrecords, final Integer startindex) {
		
		final long jobStartTime = System.currentTimeMillis();
		
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		AssertUtil.assertArgumentNotEmpty(from, "from");
		
		if (startTime.size() != endTime.size()) {
			throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + startTime.size() + "!=" + endTime.size());
		}
		
		if (startTime.size() > 1 && from.size() > 1 && startTime.size() != from.size()) {
			throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + startTime.size() + "!=" + from.size());
		}

		List<LogRecord> logRecords = new ArrayList<LogRecord>();
		

        Set<AccessInterface> triedInterfaces = new HashSet<AccessInterface>();
        
        while (true) {
            // get the end point
            final AccessInterface bestAccessInterface = getBestAccessInterface();
            if (!triedInterfaces.add(bestAccessInterface)) {
                throw new JobExecutionException("All registered remote services are unavailable. Tried to access: " + triedInterfaces.toString());
            };

            String callId = bestAccessInterface.getUrl() + "::syncQuery";
            logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));


    		StringBuilder message = new StringBuilder();
    		message.append("Executing 'result=timeQuery(");
    		message.append("startTime=").append(startTime);
    		message.append(", ").append("endTime=").append(endTime);
    		message.append(", ").append("from=").append(from);
    		message.append(", ").append("maxrecords=").append(maxrecords);
    		message.append(", ").append("startIndex=").append(startindex);
    		message.append(")'");
    		
    		if (_LOGGER.isTraceEnabled()) {
    			_LOGGER.trace(message.toString());
    		}
    		
    		logRecords.add(new LogRecord(Level.INFO, message.toString()));

    		// get the result
            try {
                final HelioQueryService port = getPort(bestAccessInterface);
                
                // wait for the result
                // FIXME: change to prevent marshalling the result
        		VOTABLE votable = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
        			@Override
        			public VOTABLE call() throws Exception {
        				VOTABLE result = port.timeQuery(startTime, endTime, from, maxrecords, startindex);
        				return result;
        			}
        		}, callId, timeout);
    		
        		if (votable == null) {
        			throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        		}
    		
        		int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
        		HelioQueryResult result = new HelioSyncQueryResult(votable, executionDuration, logRecords);
        		return result;
            } catch (WebServiceException e) {
                // get port fails
                String msg = "Timeout occurred. Trying to failover.";
                logRecords.add(new LogRecord(Level.INFO, msg));
                _LOGGER.info(msg);
                loadBalancer.updateAccessTime(bestAccessInterface, -1);
            } catch (JobExecutionException e) { // handle timeout
                if (e.getCause() instanceof TimeoutException) {
                    String msg = "Timeout occurred. Trying to failover.";
                    logRecords.add(new LogRecord(Level.INFO, msg));
                    _LOGGER.info(msg);
                    loadBalancer.updateAccessTime(bestAccessInterface, -1);                 
                } else {
                    throw e;
                }
            }
        }
	}
	
	@Override
	public HelioServiceName getServiceName() {
		return serviceName;
	}
	
	
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Implementation of the HELIO Query result.
	 * @author MarcoSoldati
	 *
	 */
	private static class HelioSyncQueryResult implements HelioQueryResult {
	    /**
	     * How long did it take to execute the process.
	     */
		private final int executionDuration;
		
		/**
		 * The actual result stored as in memory votable.
		 */
		private final VOTABLE voTable;
		
		/**
		 * Time when the method terminated is just when this object gets created
		 */
		private final Date destructionTime = new Date();

		/**
		 * Hold the log message for the user
		 */
		private final List<LogRecord> userLogs = new ArrayList<LogRecord>();
		
		/**
		 * Create the HELIO query result
		 * @param voTable the returned voTable
		 * @param executionDuration the time used for the query
		 * @param userLogs logs
		 */
		public HelioSyncQueryResult(VOTABLE voTable, int executionDuration, List<LogRecord> userLogs) {
			this.voTable = voTable;
			this.executionDuration = executionDuration;
			if (userLogs != null) {
				this.userLogs.addAll(userLogs);
			}
			if (executionDuration > 0) {
				this.userLogs.add(new LogRecord(Level.INFO, "Query terminated in " + MessageUtils.formatSeconds(getExecutionDuration()) + "."));
			}
		}
		
		@Override
		public Phase getPhase() {
			return Phase.COMPLETED;
		}

		@Override
		public int getExecutionDuration() {
			return executionDuration;
		}

		@Override
		public Date getDestructionTime() {
			return destructionTime;
		}

		@Override
		public URL asURL() throws JobExecutionException {
		    return asURL(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
		}

		@Override
		public URL asURL(long timeout, TimeUnit unit)
				throws JobExecutionException {
            File tavernaTmp = HelioFileUtil.getHelioTempDir("taverna");
            
            File outDir = new File(tavernaTmp, UUID.randomUUID().toString());
            try {
                FileUtils.forceMkdir(outDir);
            } catch (IOException e) {
                throw new JobExecutionException("Unable to create output directory: " + outDir + ". Cause: " + e.getMessage(), e);
            }
            File outFile = new File(outDir, "hec.votable.xml");

		    
		    FileWriter writer;
            try {
                writer = new FileWriter(outFile);
            } catch (IOException e) {
                throw new JobExecutionException("Error downloading votable: " + e.getMessage(), e);
            }
		    VOTableUtils.getInstance().voTable2Writer(voTable, writer, false);
		    try {
                return outFile.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new JobExecutionException("Error creating URL from File: " + e.getMessage(), e);
            }
		}

		@Override
		public VOTABLE asVOTable() throws JobExecutionException {
			return voTable;
		}

		@Override
		public VOTABLE asVOTable(long timeout, TimeUnit unit)
				throws JobExecutionException {
			return voTable;
		}

		@Override
		public LogRecord[] getUserLogs() {
			return userLogs.toArray(new LogRecord[userLogs.size()]);
		}

		@Override
		public String asString() throws JobExecutionException {
			return asString(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
		}
		
		@Override
		public String asString(long timeout, TimeUnit unit) throws JobExecutionException {
			VOTABLE voTable = asVOTable(timeout, unit);
			return VOTableUtils.getInstance().voTable2String(voTable, true);
		}
	}
}
