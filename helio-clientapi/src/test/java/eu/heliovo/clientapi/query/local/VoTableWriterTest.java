package eu.heliovo.clientapi.query.local;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.util.DataSource;

/**
 * Test class for {@link VoTableWriterImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class VoTableWriterTest {
	private static final String TEST_HEADER = "test header";
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	private StringWriter writer = null;
	private VoTableWriterImpl votableWriter;

	
	@Before
	public void setup() {
		writer = new StringWriter();
		votableWriter = new VoTableWriterImpl();
		votableWriter.setProperties(getSampleProperty());
	}
	
	@After
	public void tearDown() throws Exception {
		writer.close();
		writer = null;
	}
	
	@Test (expected=IllegalArgumentException.class)	
	public void test_null_votable() {
		StarTable[] starTable = null;
		votableWriter.writeVoTableToXml(writer, starTable, getProperties());
	}
	
	@Test	
	public void test_votable_output() throws Exception {
		DataSource datsrc = new DataSource() {	
			@Override
			protected InputStream getRawInputStream() throws IOException {
				InputStream is = new ByteArrayInputStream("a, b, c\n1, 2, 3\n4, 5, 6\n".getBytes());
				return is;
			}
		};
		StarTable starTable = new StarTableFactory().makeStarTable(datsrc, "CSV");
		votableWriter.writeVoTableToXml(writer, new StarTable[]{starTable}, getProperties());
		assertTrue(writer.getBuffer().toString().contains(TEST_HEADER));
	}

	private Properties getSampleProperty() {
		Properties properties = new Properties();
		properties.put("sql.votable.head.desc", TEST_HEADER);
		return properties;
	}
	
	private Map<String, String> getProperties() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("QUERY_STATUS", "COMPLETED");
		attributes.put("EXECUTED_AT", now());
		attributes.put("QUERY_STRING", "n/a");
		attributes.put("QUERY_URL", "n/a");
		
		return attributes;
	}
	
	private static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
}
