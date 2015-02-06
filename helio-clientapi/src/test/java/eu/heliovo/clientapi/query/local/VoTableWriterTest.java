package eu.heliovo.clientapi.query.local;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.util.DataSource;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.DateUtil;

/**
 * Test class for {@link VoTableWriterImpl}
 * @author junia schoch at fhnw ch
 *
 */
public class VoTableWriterTest {
	private static final HelioServiceName SERVICE_NAME = HelioServiceName.HEC;
	
	private StringWriter writer = null;
	private VoTableWriterImpl votableWriter;
	private HelioServiceName serviceName;

	
	@Before
	public void setup() {
		writer = new StringWriter();
		votableWriter = new VoTableWriterImpl();
		serviceName = SERVICE_NAME;
	}
	
	@After
	public void tearDown() throws Exception {
		writer.close();
		writer = null;
	}
	
	@Test (expected=IllegalArgumentException.class)	
	public void test_null_votable() {
		StarTable[] starTable = null;
		votableWriter.writeVoTableToXml(writer, starTable, getProperties(), serviceName);
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
		votableWriter.writeVoTableToXml(writer, new StarTable[]{starTable}, getProperties(), serviceName);
		System.out.println(writer.getBuffer());
		assertTrue(writer.getBuffer().toString().contains(serviceName.toString()));
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
		return DateUtil.toIsoDateString(cal.getTime());
	}
}
