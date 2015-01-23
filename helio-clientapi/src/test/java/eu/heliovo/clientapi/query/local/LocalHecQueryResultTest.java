package eu.heliovo.clientapi.query.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link LocalHecQueryResultImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryResultTest {

	private StringWriter writer = null;
	private static final String SAMPLE_VOTABLE = "/eu/heliovo/clientapi/utils/resource/testdata_2_tables.xml";
	
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
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		LocalHecQueryResultImpl hecQueryResultImpl = new LocalHecQueryResultImpl(0, userLogs, getTestVOTable());
		assertTrue(hecQueryResultImpl.asURL().toString().contains(SAMPLE_VOTABLE));
	}
	
	@Test
	public void test_asVoTable() {
		List<LogRecord> userLogs = new ArrayList<LogRecord>();
		LocalHecQueryResultImpl hecQueryResultImpl = new LocalHecQueryResultImpl(0, userLogs, getTestVOTable());
		assertEquals(2, hecQueryResultImpl.asVOTable().getRESOURCE().size());
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
