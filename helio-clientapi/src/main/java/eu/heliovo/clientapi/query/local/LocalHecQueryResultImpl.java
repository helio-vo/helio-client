package eu.heliovo.clientapi.query.local;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.ivoa.xml.votable.v1.VOTABLE;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.utils.MessageUtils;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Implementation of the HELIO Query result for local query.
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryResultImpl implements HelioQueryResult{
	 /**
     * How long did it take to execute the process.
     */
	private final transient int executionDuration;
	
	/**
	 * Time when the method terminated is just when this object gets created
	 */
	private final transient Date destructionTime = new Date();

	/**
	 * Hold the log message for the user
	 */
	private final transient List<LogRecord> userLogs = new ArrayList<LogRecord>();
	
	/**
	 * File with VOTable in xml format
	 */
	private File file;
	
	/**
	 * Create the HELIO query result
	 * @param voTable the returned voTable
	 * @param executionDuration the time used for the query
	 * @param userLogs logs
	 */
	public LocalHecQueryResultImpl(int executionDuration, List<LogRecord> userLogs, File file) {
		AssertUtil.assertArgumentNotNull(file, "file");
		AssertUtil.assertArgumentNotNull(userLogs, "userLogs");
		
		this.file = file;
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
		try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new JobExecutionException("Error creating URL from File: " + e.getMessage(), e);
        }
	}

	@Override
	public URL asURL(long timeout, TimeUnit unit) throws JobExecutionException {
		return asURL();
	}

	@Override
	public VOTABLE asVOTable() throws JobExecutionException {
		VOTABLE voTable = VOTableUtils.getInstance().url2VoTable(asURL());
		return voTable;
	}

	@Override
	public VOTABLE asVOTable(long timeout, TimeUnit unit)
			throws JobExecutionException {
		return asVOTable();
	}

	@Override
	public String asString() throws JobExecutionException {
		VOTABLE voTable = asVOTable();
		return VOTableUtils.getInstance().voTable2String(voTable, true);
	}

	@Override
	public String asString(long timeout, TimeUnit unit)
			throws JobExecutionException {
		return asString();
	}

	@Override
	public LogRecord[] getUserLogs() {
		return userLogs.toArray(new LogRecord[userLogs.size()]);
	}

}
