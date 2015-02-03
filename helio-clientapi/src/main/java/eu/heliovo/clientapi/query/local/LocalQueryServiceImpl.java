package eu.heliovo.clientapi.query.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.HelioClientException;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.QueryService;
import eu.heliovo.clientapi.query.QueryType;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.query.paramquery.serialize.QuerySerializer;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.shared.util.DateUtil;

/**
 * Implementation of {@link LocalQueryService} that uses {@link LocalQueryDao} for data access 
 * and StarTable for VOTable serialization.
 * @author junia schoch at fhnw ch
 *
 */
public class LocalQueryServiceImpl extends AbstractServiceImpl implements QueryService {
	private static final String VOTABLE = "votable";
	private static final String HEC_ID = "hec_id";
	private LocalQueryDao localQueryDao;
	private VoTableWriter voTableWriter;
	private HelioFileUtil helioFileUtil;
	
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

	public LocalQueryServiceImpl() {
		setCapabilites(ServiceCapability.SYNC_QUERY_SERVICE, ServiceCapability.ASYNC_QUERY_SERVICE);
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
		setJoin(null);
		
		return execute();
	}

	@Override
	public HelioQueryResult execute() {
		long jobStartTime = System.currentTimeMillis();
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		int executionDuration = 0;
		
		StarTable[] starTables = getStarTablesFromQueries();
		File file = writeStarTablesToXml(starTables);
		
		executionDuration = (int) (System.currentTimeMillis() - jobStartTime);
		userLogs.add(new LogRecord(Level.INFO, "Created file in: " + file.getAbsolutePath().toString()));

		HelioQueryResult helioQueryResult = new LocalQueryResultImpl(executionDuration, userLogs, file);
		return helioQueryResult;
	}
	
	private StarTable[] getStarTablesFromQueries() {
		int numberOfQueries = getFrom().size();
		StarTable[] starTables = new StarTable[numberOfQueries];
		
		for (int i=0; i<numberOfQueries; i++) {
			String select = getSelectStatement(getWhereClauses().get(i));
			String fromStatement = getFrom().get(i);
			String where = getWhereStatement(getWhereClauses().get(i), getStartTime().get(i), getEndTime().get(i));

			StarTable starTable = localQueryDao.query(select, fromStatement, where, startIndex, maxRecords);
			starTables[i] = starTable;
		}
		
		return starTables;
	}
	

	private String getSelectStatement(WhereClause whereClause) {
		List<HelioFieldDescriptor<?>> fieldDescriptors = whereClause.getFieldDescriptors();
		StringBuilder select = new StringBuilder();
		boolean first = true;
		for (HelioFieldDescriptor<?> helioFieldDescriptor : fieldDescriptors) {
			if (first) {
				first = false;
			} else {
				select.append(", ");
			}
			if (HEC_ID.equalsIgnoreCase(helioFieldDescriptor.getName())) {
				select.append("id as " + HEC_ID);
			} else {
				select.append(helioFieldDescriptor.getName());
			}
		}
		
		return select.toString();
	}

	private String getWhereStatement(WhereClause whereClause, String startTime, String endTime) {
		String timewhere = "";
		if(!startTime.isEmpty() && !endTime.isEmpty()) {
			timewhere = "NOT ('" + endTime + "' < time_start AND '" + startTime + "' >= time_end)";
		} else if (!startTime.isEmpty() && endTime.isEmpty()) {
			timewhere = "time_start <= '" + startTime + "'";
		} else if (startTime.isEmpty() && !endTime.isEmpty()) {
			timewhere = "time_end >= '" + endTime + "'";
		}
		
		String where = querySerializer.getWhereClause(whereClause.getCatalogName(), whereClause.getQueryTerms());

		if(where.isEmpty()) {
			return timewhere;
		} else if (!timewhere.isEmpty() && !where.isEmpty()) {
			return "(" +  where + ") AND (" + timewhere + ")";
		} else  {
			return where;
		}
	}
	
	private File writeStarTablesToXml(StarTable[] starTables) {
		File file = getUuidFile();
		Map<String, String> attributes = getKeyValueAttrMap();
		
		try (FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw)) {

			voTableWriter.writeVoTableToXml(bw, starTables, attributes, getServiceName());

		} catch (IOException e) {
			throw new HelioClientException(
					"could not write votable to xml", e);
		}
		
		return file;
	}
	
	private Map<String, String> getKeyValueAttrMap() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("QUERY_STATUS", "COMPLETED");
		attributes.put("EXECUTED_AT", now());
		
		return attributes;
	}
 
	private static String now() {
		Calendar cal = Calendar.getInstance();
		return DateUtil.toIsoDateString(cal.getTime());
	}

	@Override
	public List<String> getFrom() {
		return from;
	}

	@Override
	public void setFrom(List<String> from) {
		this.from = from;
		updateWhereClauses();
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
	
    public WhereClauseFactoryBean getWhereClauseFactoryBean() {
        return whereClauseFactoryBean;
    }

    public void setWhereClauseFactoryBean(WhereClauseFactoryBean whereClauseFactoryBean) {
        this.whereClauseFactoryBean = whereClauseFactoryBean;
    }
    
    /**
     * @return the querySerializer
     */
    public QuerySerializer getQuerySerializer() {
        return querySerializer;
    }

    /**
     * @param querySerializer the querySerializer to set
     */
    public void setQuerySerializer(QuerySerializer querySerializer) {
        this.querySerializer = querySerializer;
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
	
	public LocalQueryDao getLocalQueryDao() {
		return localQueryDao;
	}

	public void setLocalQueryDao(LocalQueryDao localQueryDao) {
		this.localQueryDao = localQueryDao;
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
