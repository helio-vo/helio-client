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
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.MockWhereClauseFactoryBean;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.query.paramquery.serialize.SQLSerializer;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;

/**
 * Test for {@link LocalHecQueryServiceImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryServiceTest {
	private LocalHecQueryServiceImpl localHecQueryService;
	
	private static final String START_TIME = "2003-08-03 08:00:00";
	private static final String END_TIME = "2003-08-05 08:00:00";
	private static final Integer MAX_RECORDS = 0;
	private static final Integer START_INDEX = 0;
	private static final String FROM = "hec__rhessi_hxr_flare";
	private static final String JOIN = null;
	private static final String VOTABLE_TAG = "VOTABLE";
	
	@Before
	public void setup() {
		LocalHecQueryDao localHecQueryDao = new MockLocalHecQueryDao();
		
		HelioFileUtil helioFileUtil = new HelioFileUtil("test");
		VoTableWriter voTableWriter = new MockVoTableWriter();
		
		WhereClauseFactoryBean whereClauseFactoryBean = new MockWhereClauseFactoryBean(); 
		
		this.localHecQueryService = new LocalHecQueryServiceImpl();
		this.localHecQueryService.setServiceName(HelioServiceName.HEC);
		this.localHecQueryService.setLocalHecQueryDao(localHecQueryDao);
		this.localHecQueryService.setHelioFileUtil(helioFileUtil);
		this.localHecQueryService.setVoTableWriter(voTableWriter);
		this.localHecQueryService.setQuerySerializer(new SQLSerializer());
		this.localHecQueryService.setWhereClauseFactoryBean(whereClauseFactoryBean);
	}
	
	@After
	public void tearDown() throws Exception {
		this.localHecQueryService = null;
	}
	
	@Test
	public void test_query() {
		HelioQueryResult result = localHecQueryService.query(START_TIME, END_TIME, FROM, MAX_RECORDS, START_INDEX, JOIN);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_query_List() {
		HelioQueryResult result = localHecQueryService.query(Collections.singletonList(START_TIME), 
				Collections.singletonList(END_TIME), Collections.singletonList(FROM), MAX_RECORDS, START_INDEX, JOIN);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_timeQuery() {
		HelioQueryResult result = localHecQueryService.timeQuery(START_TIME, END_TIME, 
				FROM, MAX_RECORDS, START_INDEX);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_timeQuery_List() {
		HelioQueryResult result = localHecQueryService.timeQuery(Collections.singletonList(START_TIME), Collections.singletonList(END_TIME), 
				Collections.singletonList(FROM), MAX_RECORDS, START_INDEX);
		assertTrue(result.asString().contains(VOTABLE_TAG));
	}
	
	@Test
	public void test_serviceName() {
		HelioServiceName expectedName = HelioServiceName.HEC;
		assertEquals(expectedName, localHecQueryService.getServiceName());
	}
	
	@Test
	public void test_executionDuration() {
		HelioQueryResult result = execute();

		int expectedMinValue = 0;
		int executionDuration = result.getExecutionDuration();
		assertTrue(executionDuration >= expectedMinValue);
	}
	
	@Test
	public void test_userLogs() {
		HelioQueryResult result = execute();
		
		LogRecord[] userLogs = result.getUserLogs();
		assertTrue(userLogs.length > 0);
	}
	
	@Test
	public void test_File() {
		HelioQueryResult result = execute();
		
		File resultFile = null;
		try {
			resultFile = new File(result.asURL().toURI());
		} catch (Exception e) {
			fail(e.toString());
		}
		assertTrue(resultFile.exists());
	}
	
	/**
	 * Set properties in localHecQueryService and call execute().
	 * @author junia schoch at fhnw ch
	 */
	private HelioQueryResult execute() {
		localHecQueryService.setStartTime(Collections.singletonList(START_TIME));
		localHecQueryService.setEndTime(Collections.singletonList(END_TIME));
		localHecQueryService.setFrom(Collections.singletonList(FROM));
		localHecQueryService.setMaxRecords(MAX_RECORDS);
		localHecQueryService.setStartIndex(START_INDEX);
		localHecQueryService.setJoin(JOIN);
		
		HelioQueryResult result = localHecQueryService.execute();
		return result;
	}	
	
	/**
	 * Mock class for {@link VoTableWriterImpl}
	 * @author junia schoch at fhnw ch
	 *
	 */
	private final class MockVoTableWriter implements VoTableWriter {
		@Override
		public void writeVoTableToXml(Writer outWriter, StarTable[] starTables, Map<String, String> attrKeyValueMap) {
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
	 * Mock class for {@link LocalHecQueryDaoImpl}
	 * @author junia schoch at fhnw ch
	 *
	 */
	private static final class MockLocalHecQueryDao implements LocalHecQueryDao {
		@Override
		public StarTable query(String select, String from, String where, int startindex,
				int maxrecords) {
			return getStarTable();
		}
	}
	
	/**
	 * Get Sample StarTable.
	 * @author junia schoch at fhnw ch
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
}
