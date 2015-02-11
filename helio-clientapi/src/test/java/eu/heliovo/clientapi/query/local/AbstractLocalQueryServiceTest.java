package eu.heliovo.clientapi.query.local;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.starlink.table.StarTable;
import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.MockWhereClauseFactoryBean;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.query.paramquery.serialize.SQLSerializer;
import eu.heliovo.clientapi.utils.convert.HelioConversionService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;

public abstract class AbstractLocalQueryServiceTest {
	private LocalQueryServiceImpl localQueryService;
	private MockLocalQueryDao localQueryDao;
	private HelioFileUtil helioFileUtil;
	
	private static final String START_TIME = "2003-08-03 08:00:00";
	private static final String END_TIME = "2003-08-05 08:00:00";
	private static final Integer MAX_RECORDS_0 = 0;
	private static final Integer MAX_RECORDS_5 = 5;
	private static final Integer START_INDEX = 0;
	private static final String FROM = "ics_pat";
	private static final String JOIN = null;
	
	private static final String VOTABLE_TAG = "VOTABLE";
	private static final String FIELD_DESCRIPTOR_ID = "testId";
	private static final Integer FIELD_DESCRIPTOR_VALUE = 5;
	private static final String AREA = "localquery-votable";
	private static final String APP_ID = "test";
	private static final String CATALOG_NAME = "test";
	
	public abstract HelioServiceName getHelioServiceName();
	
	@Before
	public void setup() {
		localQueryDao = new MockLocalQueryDao();
		
		helioFileUtil = new HelioFileUtil(APP_ID);
		VoTableWriter voTableWriter = new MockVoTableWriter();
		
		List<String> descriptorNames = new ArrayList<String>();
		descriptorNames.add("testId");
		descriptorNames.add("time_start");
		descriptorNames.add("time_end");
		MockWhereClauseFactoryBean whereClauseFactoryBean = getMockWhereClauseFactoryBean(descriptorNames);
		
		SQLSerializer sqlSerializer = new SQLSerializer();
		sqlSerializer.setConversionService(new HelioConversionService());
		
		this.localQueryService = new LocalQueryServiceImpl();
		this.localQueryService.setServiceName(getHelioServiceName());
		this.localQueryService.setLocalQueryDao(localQueryDao);
		this.localQueryService.setHelioFileUtil(helioFileUtil);
		this.localQueryService.setVoTableWriter(voTableWriter);
		this.localQueryService.setQuerySerializer(sqlSerializer);
		this.localQueryService.setWhereClauseFactoryBean(whereClauseFactoryBean);
		this.localQueryService.setHelioFileUtilArea(AREA);
	}
	
	@After
	public void tearDown() throws Exception {
		File file = localQueryService.getHelioFileUtil().getHelioTempDir(AREA);
		LocalQueryTestUtil.recursiveDelete(file);
	}
	
	@Test
	public void test_query() {
		HelioQueryResult result = localQueryService.query(START_TIME, END_TIME, FROM, MAX_RECORDS_0, START_INDEX, JOIN);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_query_List() {
		HelioQueryResult result = localQueryService.query(Collections.singletonList(START_TIME), 
				Collections.singletonList(END_TIME), Collections.singletonList(FROM), MAX_RECORDS_0, START_INDEX, JOIN);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_timeQuery() {
		HelioQueryResult result = localQueryService.timeQuery(START_TIME, END_TIME, 
				FROM, MAX_RECORDS_0, START_INDEX);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_timeQuery_List() {
		HelioQueryResult result = localQueryService.timeQuery(Collections.singletonList(START_TIME), 
				Collections.singletonList(END_TIME), Collections.singletonList(FROM), MAX_RECORDS_0, START_INDEX);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_whereStatement() {
		setDefaultProperties();
		setWhereClauseQueryTerm();
		
		localQueryService.execute();
		
		StringBuilder expectedWhere = new StringBuilder();
		expectedWhere.append("(").append(CATALOG_NAME).append(".").append(FIELD_DESCRIPTOR_ID).append(" >= ");
		expectedWhere.append(FIELD_DESCRIPTOR_VALUE).append(") ");
		expectedWhere.append("AND (");
		expectedWhere.append("('").append(START_TIME).append("' < time_end AND '").append(END_TIME).append("' > time_start))");
		
		assertEquals(expectedWhere.toString(), localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_startTime_endTime() {
        setDefaultProperties();
		localQueryService.execute();
		
		String expectedWhere = "('" + START_TIME + "' < time_end AND '" + END_TIME + "' > time_start)";
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_startTime() {
		//set different WhereClauseFactoryBean
		List<String> descriptorNames = new ArrayList<String>();
		descriptorNames.add("testId");
		descriptorNames.add("time_start");
		MockWhereClauseFactoryBean whereClauseFactoryBean = getMockWhereClauseFactoryBean(descriptorNames);
		localQueryService.setWhereClauseFactoryBean(whereClauseFactoryBean);
		
		setDefaultProperties();		
		localQueryService.execute();
		
		String expectedWhere = "('" + START_TIME + "' < time_end)";
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_endTime() {
		//set different WhereClauseFactoryBean
		List<String> descriptorNames = new ArrayList<String>();
		descriptorNames.add("testId");
		descriptorNames.add("time_end");
		MockWhereClauseFactoryBean whereClauseFactoryBean = getMockWhereClauseFactoryBean(descriptorNames);
		localQueryService.setWhereClauseFactoryBean(whereClauseFactoryBean);
		
		setDefaultProperties();		
		localQueryService.execute();
		
		String expectedWhere = "('" + END_TIME + "' > time_start)";
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_time() {
		//set different WhereClauseFactoryBean
		List<String> descriptorNames = new ArrayList<String>();
		descriptorNames.add("testId");
		descriptorNames.add("time");
		MockWhereClauseFactoryBean whereClauseFactoryBean = getMockWhereClauseFactoryBean(descriptorNames);
		localQueryService.setWhereClauseFactoryBean(whereClauseFactoryBean);
		
		setDefaultProperties();		
		localQueryService.execute();
		
		String expectedWhere = "('" + START_TIME + "' <= time AND '" + END_TIME + "' >= time)"; 
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_parameter_onlyStartDate() {
		setDefaultProperties();
		localQueryService.setEndTime(Collections.singletonList(""));
		localQueryService.execute();

		String expectedWhere = "";
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_parameter_onlyEndDate() {
		setDefaultProperties();
		localQueryService.setStartTime(Collections.singletonList(""));
		localQueryService.execute();

		String expectedWhere = "";
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_parameter_empty_startEnd() {
		setDefaultProperties();
		localQueryService.setEndTime(Collections.singletonList(""));
		localQueryService.setStartTime(Collections.singletonList(""));
		localQueryService.execute();
		
		assertEquals("", localQueryDao.getWhere());
	}
	
	@Test
	public void test_whereStatement_whereClause() {
		setDefaultProperties();
		setWhereClauseQueryTerm();
		localQueryService.setEndTime(Collections.singletonList(""));
		localQueryService.setStartTime(Collections.singletonList(""));
		
		
		localQueryService.execute();
		String expectedWhere = CATALOG_NAME + ".testId >= 5";
		assertEquals(expectedWhere, localQueryDao.getWhere());
	}
	
	@Test
	public void test_fromStatement() {
		setDefaultProperties();
		localQueryService.execute();
		
		assertEquals(FROM, localQueryDao.getFrom());
	}
	
	@Test
	public void test_selectStatement() {
		setDefaultProperties();
		localQueryService.execute();
		
		String expectedSelect = FIELD_DESCRIPTOR_ID + ", time_start, time_end";
		assertEquals(expectedSelect, localQueryDao.getSelect());
	}
	
	@Test
	public void test_serviceName() {
		HelioServiceName expectedName = getHelioServiceName();
		assertEquals(expectedName, localQueryService.getServiceName());
	}
	
	@Test
	public void test_executionDuration() {
		setDefaultProperties();
		HelioQueryResult result = localQueryService.execute();

		int expectedMinValue = 0;
		int executionDuration = result.getExecutionDuration();
		assertTrue(executionDuration >= expectedMinValue);
	}
	
	@Test
	public void test_userLogs() {
		setDefaultProperties();
		HelioQueryResult result = localQueryService.execute();
		
		LogRecord[] userLogs = result.getUserLogs();
		assertTrue(userLogs.length > 0);
	}
	
	@Test
	public void test_File() {
		setDefaultProperties();
		HelioQueryResult result = localQueryService.execute();
		
		File resultFile = null;
		try {
			resultFile = new File(result.asURL().toURI());
		} catch (Exception e) {
			fail(e.toString());
		}
		assertTrue(resultFile.exists());
	}
	
	private MockWhereClauseFactoryBean getMockWhereClauseFactoryBean(List<String> descriptorNames) {
		List<HelioFieldDescriptor<?>> fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
		for(String name:descriptorNames) {
			fieldDescriptors.add(new HelioFieldDescriptor<Integer>(name, name, name, FieldType.INTEGER));
		}
		
//		fieldDescriptors.add(new HelioFieldDescriptor<Integer>("testId", "testName", "testDescription", FieldType.INTEGER));
//		fieldDescriptors.add(new HelioFieldDescriptor<Integer>("time_start", "time_start", "time_start", FieldType.INTEGER));
//        fieldDescriptors.add(new HelioFieldDescriptor<Integer>("time_end", "time_end", "time_end", FieldType.INTEGER));
        
        MockWhereClauseFactoryBean mockWhereClauseFactoryBean = new MockWhereClauseFactoryBean(fieldDescriptors);
        return mockWhereClauseFactoryBean;
	}
	
	/**
	 * Set default properties in localQueryService property
	 */
	private void setDefaultProperties() {
		localQueryService.setStartTime(Collections.singletonList(START_TIME));
		localQueryService.setEndTime(Collections.singletonList(END_TIME));
		localQueryService.setFrom(Collections.singletonList(FROM));
		localQueryService.setMaxRecords(MAX_RECORDS_5);
		localQueryService.setStartIndex(START_INDEX);
		localQueryService.setJoin(JOIN);
	}
	
	/**
	 * Set one example queryTerm
	 */
	private WhereClause setWhereClauseQueryTerm() {
		WhereClause whereClause = localQueryService.getWhereClauseByCatalogName(CATALOG_NAME);
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
}
