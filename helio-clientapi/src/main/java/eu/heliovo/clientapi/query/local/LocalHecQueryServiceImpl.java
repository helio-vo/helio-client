package eu.heliovo.clientapi.query.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.HelioClientException;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Implementation of {@link LocalHecQueryService} that uses {@link LocalHecQueryDao} for data access 
 * and StarTable for VOTable serialization.
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryServiceImpl implements LocalHecQueryService {

	private static final String VOTABLE = "votable";

	private LocalHecQueryDao localHecQueryDao;
	private VoTableWriterImpl voTableWriter;
	private HelioFileUtil helioFileUtil;

	/**
	 * Saves a StarTable in xml format to a file and returns the file.
	 * Params are used for sql query to get StarTable content and must not be null.
	 * @param startTime DateTime String example: '2003-08-03 08:00:00'
	 * @param endTime DateTime String example: '2003-08-03 08:00:00'
	 * @param from 	name of a table in the db
	 * @param startindex
	 * @param maxrecords
	 * @return File
	 */
	@Override
	public HelioQueryResult query(String startTime, String endTime, String from, int startindex, int maxrecords) {
		long jobStartTime = System.currentTimeMillis();
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		StarTable starTable = localHecQueryDao.query(startTime, endTime, from, 0,0);
		File file = getUuidFile();

		try (FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw)) {

			voTableWriter.writeVoTableToXml(bw, new StarTable[]{starTable});

		} catch (IOException e) {
			throw new HelioClientException("could not write votable to xml", e);
		}

		int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
		userLogs.add(new LogRecord(Level.INFO, "Created file in: " + file.getAbsolutePath().toString()));
		
		HelioQueryResult helioQueryResult = new LocalHecQueryResultImpl(executionDuration, userLogs, file);
		return helioQueryResult;
	}
	
	/**
	 * Saves a StarTable in xml format to a file and returns the file.
	 * Params are used for sql query to get StarTable content and must not be null.
	 * @param startindex
	 * @param maxrecords
	 * @param from 	name of a table in the db
	 * @return File
	 */
	@Override
	public HelioQueryResult query(String whereClause, String from, int startindex, int maxrecords) {
		long jobStartTime = System.currentTimeMillis();
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		StarTable starTable = localHecQueryDao.query(whereClause, from, 0,0);
		File file = getUuidFile();

		try (FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw)) {
			
			voTableWriter.writeVoTableToXml(bw, new StarTable[]{starTable});

		} catch (IOException e) {
			throw new HelioClientException("could not write votable to xml", e);
		}

		int executionDuration = (int)(System.currentTimeMillis() - jobStartTime);
		userLogs.add(new LogRecord(Level.INFO, "Created file in: " + file.getAbsolutePath().toString()));
		
		HelioQueryResult helioQueryResult = new LocalHecQueryResultImpl(executionDuration, userLogs, file);
		return helioQueryResult;
	}

	private File getUuidFile() {
		UUID uuid = UUID.randomUUID();
		String uuidFilename = "votable_" + uuid.toString() + ".xml";
		File tempDir = helioFileUtil.getHelioTempDir(VOTABLE);
		File file = new File(tempDir, uuidFilename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new HelioClientException("could not create file "
						+ file.getAbsolutePath(), e);
			}
		}
		return file;
	}

	public LocalHecQueryDao getLocalHecQueryDao() {
		return localHecQueryDao;
	}

	public void setLocalHecQueryDao(LocalHecQueryDao localHecQueryDao) {
		this.localHecQueryDao = localHecQueryDao;
	}
	
	public HelioFileUtil getHelioFileUtil() {
		return helioFileUtil;
	}

	public void setHelioFileUtil(HelioFileUtil helioFileUtil) {
		this.helioFileUtil = helioFileUtil;
	}
	
	public VoTableWriterImpl getVoTableWriter() {
		return voTableWriter;
	}

	public void setVoTableWriter(VoTableWriterImpl voTableWriter) {
		this.voTableWriter = voTableWriter;
	}

}
