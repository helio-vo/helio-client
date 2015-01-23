package eu.heliovo.clientapi.query.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.workerservice.HelioWorkerServiceHandler.Phase;

/**
 * Test class for {@link LocalHecQueryResultImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryResultTest {

	private StringWriter writer = null;
	private static final String SAMPLE_VOTABLE = "/eu/heliovo/clientapi/utils/resource/testdata_2_tables.xml";
	private static final String VOTABLE_CLOSE_TAG = "</VOTABLE>";
	
	@Before
	public void setup() {
		writer = new StringWriter();
	}
	
	@After
	public void tearDown() throws Exception {
		writer.close();
	}
	
	@Test (expected=IllegalArgumentException.class)	
	public void test_null_file() {
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		LocalHecQueryResultImpl hecQueryResultImpl = new LocalHecQueryResultImpl(0, userLogs, null);
	}
	
	@Test (expected=IllegalArgumentException.class)	
	public void test_null_userLogs() {
		LocalHecQueryResultImpl hecQueryResultImpl = new LocalHecQueryResultImpl(0, null, null);
	}
	
	@Test
	public void test_asURL() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertTrue(result.asURL().toString().contains(SAMPLE_VOTABLE));
	}
	
	@Test
	public void test_asURL_withParams() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertTrue(result.asURL((long)0,TimeUnit.SECONDS).toString().contains(SAMPLE_VOTABLE));
	}
	
	@Test
	public void test_asVoTable() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertEquals(2, result.asVOTable().getRESOURCE().size());
	}
	
	@Test
	public void test_asVoTable_withParams() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertEquals(2, result.asVOTable((long)0, TimeUnit.SECONDS).getRESOURCE().size());
	}
	
	
	@Test
	public void test_phase() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertEquals(Phase.COMPLETED, result.getPhase());
	}
	
	@Test
	public void test_destructionTime() {
		Date now = new Date();
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertTrue(result.getDestructionTime().compareTo(now) >= 0);
	}
	
	@Test
	public void test_asString() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertTrue(result.asString().contains(VOTABLE_CLOSE_TAG));
	}
	
	@Test
	public void test_asString_withParams() {
		LocalHecQueryResultImpl result = getValidLocalHecQueryResult();
		assertTrue(result.asString((long)0, TimeUnit.SECONDS).contains(VOTABLE_CLOSE_TAG));
	}
	
	private LocalHecQueryResultImpl getValidLocalHecQueryResult() {
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		LocalHecQueryResultImpl hecQueryResultImpl = new LocalHecQueryResultImpl(0, userLogs, getTestVOTable());
		return hecQueryResultImpl;
	}
	
    private File getTestVOTable() {
        URL resultFile = getClass().getResource(SAMPLE_VOTABLE);
        assertNotNull("resource not found: " + SAMPLE_VOTABLE, resultFile);
        try {
			return new File(resultFile.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }
}
