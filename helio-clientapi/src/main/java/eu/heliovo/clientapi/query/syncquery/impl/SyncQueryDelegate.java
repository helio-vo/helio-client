package eu.heliovo.clientapi.query.syncquery.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import eu.helio_vo.xml.queryservice.v0.HelioQueryService;
import eu.helio_vo.xml.queryservice.v0.HelioQueryServiceService;
import eu.heliovo.clientapi.query.AbstractQueryServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.clientapi.utils.AsyncCallUtils;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Abstract base implementation of the sync query service
 * @author MarcoSoldati
 *
 */
class SyncQueryDelegate implements QueryDelegate {

	/**
	 * The logger instance
	 */
	private static final Logger _LOGGER = Logger.getLogger(SyncQueryDelegate.class);
    
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
	 * Hold reference to the file util.
	 */
	private transient HelioFileUtil helioFileUtil;
	
    /**
	 * Default constructor.
	 */
	public SyncQueryDelegate() {
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
	public HelioQueryResult callWebService(final AbstractQueryServiceImpl queryService, final AccessInterface accessInterface)
	        throws WebServiceException, JobExecutionException {
	    long jobStartTime = System.currentTimeMillis();
	    
        final HelioQueryService port = getPort(accessInterface);
        
        // wait for the result
        // FIXME: change to prevent marshalling the result
        VOTABLE votable = AsyncCallUtils.callAndWait(new Callable<VOTABLE>() {
            @Override
            public VOTABLE call() throws Exception {
                VOTABLE result = port.query(
                        queryService.getStartTime(), queryService.getEndTime(), 
                        queryService.getFrom(), queryService.getWhere(), 
                        null, // instruments is not used.  
                        queryService.getMaxRecords(), queryService.getStartIndex(), queryService.getJoin());
                return result;
            }
        }, getCallId(accessInterface), timeout);
    
        if (votable == null) {
            throw new JobExecutionException("Unspecified error occured on service provider. Got back null.");
        }
        int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
        HelioQueryResult result = createHelioSyncQueryResult(votable, executionDuration, queryService);

        return result;
	}

    @Override
	public String getMethodName() {
	    return "query";
	}
	
    public String getCallId(AccessInterface accessInterface) {
        return accessInterface.getUrl() + "::" + getMethodName();
    }
    
    public long getCallTimeout() {
        return timeout;
    }
    
	/**
	 * Create the result object
	 * @param votable the votable wrapped by the object
	 * @param executionDuration the time it took to get the data
	 * @param queryService the query service object.
	 * @return a new result instance
	 */
    protected HelioQueryResult createHelioSyncQueryResult(VOTABLE votable,
            int executionDuration, AbstractQueryServiceImpl queryService) {
        HelioSyncQueryResult result = new HelioSyncQueryResult(votable, executionDuration, queryService.getLogRecords());
        File tempDir = helioFileUtil.getHelioTempDir(queryService.getServiceName().getServiceName().toLowerCase() + "_sync");
        result.setTempDir(tempDir);
        result.setServiceName(queryService.getServiceName());
        return result;
    }
	
    /**
     * @return the helioFileUtil
     */
    @Required
    public HelioFileUtil getHelioFileUtil() {
        return helioFileUtil;
    }

    /**
     * @param helioFileUtil the helioFileUtil to set
     */
    public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
        this.helioFileUtil = helioFileUtil;
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
		private final transient int executionDuration;
		
		/**
		 * The actual result stored as in memory votable.
		 */
		private final VOTABLE voTable;
		
		/**
		 * Time when the method terminated is just when this object gets created
		 */
		private final transient Date destructionTime = new Date();

		/**
		 * Hold the log message for the user
		 */
		private final transient List<LogRecord> userLogs = new ArrayList<LogRecord>();
		
		/**
		 * The temp dir.
		 */
		private transient File tempDir;
		
		/**
		 * Name of the service this result belongs to.
		 */
		private HelioServiceName helioServiceName;
		
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
		
		public void setServiceName(HelioServiceName serviceName) {
            helioServiceName = serviceName;            
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
            File outDir = new File(tempDir, UUID.randomUUID().toString());
            try {
                FileUtils.forceMkdir(outDir);
            } catch (IOException e) {
                throw new JobExecutionException("Unable to create output directory: " + outDir + ". Cause: " + e.getMessage(), e);
            }
            // FIXME: set proper votable name.
            File outFile = new File(outDir, helioServiceName.toString().toLowerCase() + ".votable.xml");
		    
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
		
		/**
		 * Set the temporary dir to cache results
		 * @param tempDir the temp dir
		 */
		public void setTempDir(File tempDir) {
            this.tempDir = tempDir;
        }
	}
}
