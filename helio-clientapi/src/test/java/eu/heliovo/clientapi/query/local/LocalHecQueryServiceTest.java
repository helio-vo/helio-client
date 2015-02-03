package eu.heliovo.clientapi.query.local;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.util.DataSource;
import eu.heliovo.clientapi.HelioClientException;
import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.MockWhereClauseFactoryBean;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.query.paramquery.serialize.SQLSerializer;
import eu.heliovo.clientapi.utils.convert.HelioConversionService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Test for {@link LocalQueryServiceImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryServiceTest {
	private LocalQueryServiceImpl localHecQueryService;
	private MockLocalHecQueryDao localHecQueryDao;
	private HelioFileUtil helioFileUtil;
	
	private static final String START_TIME = "2003-08-03 08:00:00";
	private static final String END_TIME = "2003-08-05 08:00:00";
	private static final Integer MAX_RECORDS_0 = 0;
	private static final Integer MAX_RECORDS_5 = 5;
	private static final Integer START_INDEX = 0;
	private static final String FROM = "hec__rhessi_hxr_flare";
	private static final String JOIN = null;
	private static final String VOTABLE_TAG = "VOTABLE";
	private static final String CATALOG_NAME = "test";
	private static final String FIELD_DESCRIPTOR_ID = "testId";
	private static final String FIELD_DESCRIPTOR_NAME = "testName";
	private static final String FIELD_DESCRIPTOR_NAME2 = "testName2";
	private static final Integer FIELD_DESCRIPTOR_VALUE = 5;
	
	@Before
	public void setup() {
		localHecQueryDao = new MockLocalHecQueryDao();
		
		helioFileUtil = new HelioFileUtil("test");
		VoTableWriter voTableWriter = new MockVoTableWriter();
		
		WhereClauseFactoryBean whereClauseFactoryBean = new MockWhereClauseFactoryBean(); 
		SQLSerializer sqlSerializer = new SQLSerializer();
		sqlSerializer.setConversionService(new HelioConversionService());
		
		this.localHecQueryService = new LocalQueryServiceImpl();
		this.localHecQueryService.setServiceName(HelioServiceName.HEC);
		this.localHecQueryService.setLocalQueryDao(localHecQueryDao);
		this.localHecQueryService.setHelioFileUtil(helioFileUtil);
		this.localHecQueryService.setVoTableWriter(voTableWriter);
		this.localHecQueryService.setQuerySerializer(sqlSerializer);
		this.localHecQueryService.setWhereClauseFactoryBean(whereClauseFactoryBean);
	}
	
	@After
	public void tearDown() throws Exception {
		this.localHecQueryService = null;
	}
	
	@Test
	public void test_query() {
		HelioQueryResult result = localHecQueryService.query(START_TIME, END_TIME, FROM, MAX_RECORDS_0, START_INDEX, JOIN);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_query_List() {
		HelioQueryResult result = localHecQueryService.query(Collections.singletonList(START_TIME), 
				Collections.singletonList(END_TIME), Collections.singletonList(FROM), MAX_RECORDS_0, START_INDEX, JOIN);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_timeQuery() {
		HelioQueryResult result = localHecQueryService.timeQuery(START_TIME, END_TIME, 
				FROM, MAX_RECORDS_0, START_INDEX);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_timeQuery_List() {
		HelioQueryResult result = localHecQueryService.timeQuery(Collections.singletonList(START_TIME), 
				Collections.singletonList(END_TIME), Collections.singletonList(FROM), MAX_RECORDS_0, START_INDEX);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_serviceName() {
		HelioServiceName expectedName = HelioServiceName.HEC;
		assertEquals(expectedName, localHecQueryService.getServiceName());
	}
	
	@Test
	public void test_whereStatement_withoutWhereClauses() {
		setDefaultProperties();
		localHecQueryService.execute();
		
		String expectedWhere = "NOT ('" + END_TIME + "' < time_start AND '" + START_TIME + "' >= time_end)";
		assertEquals(expectedWhere, localHecQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_withoutWhereClauses_onlyStartDate() {
		setDefaultProperties();
		localHecQueryService.setEndTime(Collections.singletonList(""));
		localHecQueryService.execute();

		String expectedWhere = "time_start <= '" + START_TIME + "'";
		assertEquals(expectedWhere, localHecQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_withoutWhereClauses_onlyEndDate() {
		setDefaultProperties();
		localHecQueryService.setStartTime(Collections.singletonList(""));
		localHecQueryService.execute();

		String expectedWhere = "time_end >= '" + END_TIME + "'";
		assertEquals(expectedWhere, localHecQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_empty() {
		setDefaultProperties();
		localHecQueryService.setEndTime(Collections.singletonList(""));
		localHecQueryService.setStartTime(Collections.singletonList(""));
		localHecQueryService.execute();
		
		assertEquals("", localHecQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_whereClause() {
		setDefaultProperties();
		setWhereClauseQueryTerm();
		localHecQueryService.setEndTime(Collections.singletonList(""));
		localHecQueryService.setStartTime(Collections.singletonList(""));
		
		
		localHecQueryService.execute();
		String expectedWhere = CATALOG_NAME + ".testName >= 5";
		assertEquals(expectedWhere, localHecQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement() {
		setDefaultProperties();
		setWhereClauseQueryTerm();
		
		localHecQueryService.execute();
		
		StringBuilder expectedWhere = new StringBuilder();
		expectedWhere.append("(").append(CATALOG_NAME).append(".").append(FIELD_DESCRIPTOR_NAME).append(" >= ");
		expectedWhere.append(FIELD_DESCRIPTOR_VALUE).append(") ");
		expectedWhere.append("AND (NOT ('").append(END_TIME).append("' < time_start");
		expectedWhere.append(" AND '").append(START_TIME).append("' >= time_end))");
		
		assertEquals(expectedWhere.toString(), localHecQueryDao.getWhere());
	}
	
	@Test
	public void test_fromStatement() {
		setDefaultProperties();
		localHecQueryService.execute();
		
		assertEquals(FROM, localHecQueryDao.getFrom());
	}
	
	@Test
	public void test_selectStatement() {
		setDefaultProperties();
		localHecQueryService.execute();
		
		String expectedSelect = FIELD_DESCRIPTOR_NAME + ", " + FIELD_DESCRIPTOR_NAME2;
		System.out.println(localHecQueryDao.getSelect());
		assertEquals(expectedSelect, localHecQueryDao.getSelect());
	}
	
	@Test
	public void test_executionDuration() {
		setDefaultProperties();
		HelioQueryResult result = localHecQueryService.execute();

		int expectedMinValue = 0;
		int executionDuration = result.getExecutionDuration();
		assertTrue(executionDuration >= expectedMinValue);
	}
	
	@Test
	public void test_userLogs() {
		setDefaultProperties();
		HelioQueryResult result = localHecQueryService.execute();
		
		LogRecord[] userLogs = result.getUserLogs();
		assertTrue(userLogs.length > 0);
	}
	
	@Test
	public void test_File() {
		setDefaultProperties();
		HelioQueryResult result = localHecQueryService.execute();
		
		File resultFile = null;
		try {
			resultFile = new File(result.asURL().toURI());
		} catch (Exception e) {
			fail(e.toString());
		}
		assertTrue(resultFile.exists());
	}
		
	
	/**
	 * Set default properties in localHecQueryService property
	 */
	private void setDefaultProperties() {
		localHecQueryService.setStartTime(Collections.singletonList(START_TIME));
		localHecQueryService.setEndTime(Collections.singletonList(END_TIME));
		localHecQueryService.setFrom(Collections.singletonList(FROM));
		localHecQueryService.setMaxRecords(MAX_RECORDS_5);
		localHecQueryService.setStartIndex(START_INDEX);
		localHecQueryService.setJoin(JOIN);
	}
	
	/**
	 * Get Sample StarTable.
	 */
	private static StarTable getStarTable()  {
		DataSource datsrc = new DataSource() {	
			@Override
			protected InputStream getRawInputStream() throws IOException {
				InputStream is = new ByteArrayInputStream("a, b, c\n1, 2, 3\n4, 5, 6\n".getBytes());
				return is;
			}
		};
		StarTable starTable;
		try {
			starTable = new StarTableFactory().makeStarTable(datsrc, "CSV");
		} catch (IOException e) {
			throw new HelioClientException("could not create StarTable", e);
		}
		return starTable;
	}
	
	/**
	 * Set one example queryTerm
	 */
	private WhereClause setWhereClauseQueryTerm() {
		WhereClause whereClause = localHecQueryService.getWhereClauseByCatalogName(CATALOG_NAME);
		HelioFieldDescriptor<Integer> fieldDescriptor = (HelioFieldDescriptor<Integer>) whereClause.findFieldDescriptorById(FIELD_DESCRIPTOR_ID);
		HelioFieldQueryTerm<Integer> queryTerm = new HelioFieldQueryTerm<Integer>(fieldDescriptor, Operator.LARGER_EQUAL_THAN, FIELD_DESCRIPTOR_VALUE);
		
		whereClause.setQueryTerm(fieldDescriptor, queryTerm);
		return whereClause;
	}
	
	/**
	 * Mock class for {@link VoTableWriterImpl}
	 *
	 */
	private final class MockVoTableWriter implements VoTableWriter {
		@Override
		public void writeVoTableToXml(Writer outWriter, StarTable[] starTables, Map<String, String> attrKeyValueMap, HelioServiceName helioServiceName) {
			try {
				outWriter.append("<" + VOTABLE_TAG + " version=\"1.1\" xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\">");
				outWriter.append("<INFO name=\"TEST\" value=\"This content ist just for testing purposes.\"/>");
				outWriter.append("</" + VOTABLE_TAG + ">");
			} catch (IOException e) {
				fail(e.toString());
			}
			
		}
	}
	
	/**
	 * Mock class for {@link LocalQueryDaoImpl}
	 *
	 */
	private static final class MockLocalHecQueryDao implements LocalQueryDao {
		private String select;
		private String from;
		private String where;
		
		@Override
		public StarTable query(String select, String from, String where, int startindex,
				int maxrecords) {
			this.select = select;
			this.from = from;
			this.where = where;
			
			return getStarTable();
		}
		
		public String getSelect() {
			return select;
		}

		public String getFrom() {
			return from;
		}

		public String getWhere() {
			return where;
		}
	}
}
