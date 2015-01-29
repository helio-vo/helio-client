package eu.heliovo.clientapi.query.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.HelioClientException;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.clientapi.query.QueryType;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.query.paramquery.serialize.QuerySerializer;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Implementation of {@link LocalHecQueryService} that uses {@link LocalHecQueryDao} for data access 
 * and StarTable for VOTable serialization.
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryServiceImpl implements QueryService {
	private static final String VOTABLE = "votable";
	private LocalHecQueryDao localHecQueryDao;
	private VoTableWriter voTableWriter;
	private HelioFileUtil helioFileUtil;
	
	private HelioServiceName serviceName;
	private String serviceVariant;
	
	private List<String> startTime;
	private List<String> endTime;
	private Integer maxRecords;
	private Integer startIndex;
	private List<String> from;
	private String join;
	
	private transient WhereClauseFactoryBean whereClauseFactoryBean;
	private List<WhereClause> whereClauses = new ArrayList<WhereClause>();
	private transient Map<String, WhereClause> whereClauseCache = new HashMap<String, WhereClause>();
	
	private transient QueryType queryType = QueryType.SYNC_QUERY;
	private transient QuerySerializer querySerializer; 
	
	public LocalHecQueryServiceImpl() {
		serviceName = HelioServiceName.HEC;
	}
	

	@Override	
	public HelioQueryResult query( String startTime, String endTime, String from, Integer maxrecords, Integer startindex, String join) {
		HelioQueryResult result = query(Collections.singletonList(startTime), Collections.singletonList(endTime),
				Collections.singletonList(from), maxrecords, startindex, join);
		return result;
	}
	
	@Override
	public HelioQueryResult query(List<String> startTime, List<String> endTime,
			List<String> from, Integer maxrecords, Integer startindex,
			String join) throws JobExecutionException, IllegalArgumentException {
		
		setStartTime(startTime);
		setEndTime(endTime);
		setFrom(from);
		setMaxRecords(maxrecords);
		setStartIndex(startindex);
		setJoin(join);
		
		return execute();
	}

	@Override
	public HelioQueryResult timeQuery(String startTime, String endTime,
			String from, Integer maxrecords, Integer startindex)
			throws JobExecutionException, IllegalArgumentException {
		
		HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime),
				Collections.singletonList(from), maxrecords, startindex);
		return result;
	}

	@Override
	public HelioQueryResult timeQuery(List<String> startTime,
			List<String> endTime, List<String> from, Integer maxrecords,
			Integer startindex) throws JobExecutionException,
			IllegalArgumentException {
		
		setStartTime(startTime);
		setEndTime(endTime);
		setFrom(from);
		setMaxRecords(maxrecords);
		setStartIndex(startindex);
		setJoin(join);
		
		return execute();
	}

	@Override
	public HelioQueryResult execute() {
		long jobStartTime = System.currentTimeMillis();
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		
		String select = "id, time_start, time_peak, time_end, nar, x_cart, y_cart, "
				+ "radial_arcsec, duration, count_sec_peak, total_count, energy_kev, flare_number";
		String from = "hec__rhessi_hxr_flare";
		String where = "time_start <= '" + startTime + "' AND time_end <= '" + endTime + "'";
		
		StarTable starTable = localHecQueryDao.query(select, from, where, startIndex, maxRecords);
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

	@Override
	public HelioServiceName getServiceName() {
		return serviceName;
	}
	
    public void setServiceName(HelioServiceName serviceName) {
        this.serviceName = serviceName;
    }

	@Override
	public boolean supportsCapability(ServiceCapability capability) {
		return capability == ServiceCapability.SYNC_QUERY_SERVICE
                || capability == ServiceCapability.ASYNC_QUERY_SERVICE;
	}

	@Override
	public String getServiceVariant() {
		return serviceVariant;
	}

	public void setServiceVariant(String serviceVariant) {
		this.serviceVariant = serviceVariant;
	}

	@Override
	public List<String> getFrom() {
		return from;
	}

	@Override
	public void setFrom(List<String> from) {
		this.from = from;
		//updateWhereClauses();
	}

	@Override
    public List<String> getStartTime() {
        return startTime;
    }

	@Override
    public void setStartTime(List<String> startDate) {
        this.startTime = startDate;
    }

	@Override
	public void setEndTime(List<String> endTime) {
		this.endTime = endTime;
		
	}

	@Override
	public List<String> getEndTime() {
		return endTime;
	}

	@Override
	public Integer getMaxRecords() {
		return maxRecords;
	}

	@Override
	public void setMaxRecords(Integer maxRecords) {
		this.maxRecords = maxRecords;
	}

	@Override
	public Integer getStartIndex() {
		return startIndex;
	}

	@Override
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public List<WhereClause> getWhereClauses() {
		return whereClauses;
	}

	@Override
	public WhereClause getWhereClauseByCatalogName(String catalogName) {
		for (WhereClause clause : whereClauses) {
            if (catalogName.equals(clause.getCatalogName())) {
                return clause;
            }
        }
        return null;
	}

	@Override
	public String getJoin() {
		return join;
	}

	@Override
	public void setJoin(String join) {
		this.join = join;
	}

	@Override
	public QueryType getQueryType() {
		return queryType;
	}

	@Override
	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
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
	
	public VoTableWriter getVoTableWriter() {
		return voTableWriter;
	}

	public void setVoTableWriter(VoTableWriter voTableWriter) {
		this.voTableWriter = voTableWriter;
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
	
    /**
     * Update the where clauses, depending on the from property. 
     */
    private void updateWhereClauses() {
        // empty the where clauses
        whereClauses.clear();
        
        // and repopulate from cache or create new clause
        for (String catalogue : getFrom()) {
            WhereClause clause = whereClauseCache.get(catalogue);
            if (clause == null) {
                clause = whereClauseFactoryBean.createWhereClause(getServiceName(), catalogue);
                whereClauseCache.put(catalogue, clause);
            }
            whereClauses.add(clause);
        }
    }

}
