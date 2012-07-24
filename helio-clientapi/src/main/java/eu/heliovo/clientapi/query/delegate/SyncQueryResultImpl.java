package eu.heliovo.clientapi.query.delegate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.apache.commons.io.FileUtils;

import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.HelioServiceName;

/**
 * Implementation of the HELIO Query result.
 * @author MarcoSoldati
 *
 */
class SyncQueryResultImpl implements HelioQueryResult {
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
	public SyncQueryResultImpl(VOTABLE voTable, int executionDuration, List<LogRecord> userLogs) {
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
	    return asURL(SyncQueryDelegate.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
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
		return asString(SyncQueryDelegate.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
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