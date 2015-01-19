package eu.heliovo.clientapi.query.local;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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

	private StringWriter writer = null;
	private static final String TEST_HEADER = "test header";
	
	@Before
	public void setup() {
		writer = new StringWriter();
	}
	
	@After
	public void tearDown() throws Exception {
		writer.close();
	}
	
	@Test (expected=IllegalArgumentException.class)	
	public void test_null_votable() {
		StarTable[] starTable = null;
		VoTableWriterImpl votableWriter = new VoTableWriterImpl();
		votableWriter.writeVoTableToXml(writer, starTable);
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
		VoTableWriterImpl votableWriter = new VoTableWriterImpl();
		votableWriter.setProperties(getSampleProperty());
		votableWriter.writeVoTableToXml(writer, new StarTable[]{starTable});
		assertTrue(writer.getBuffer().toString().contains(TEST_HEADER));
	}

	private Properties getSampleProperty() {
		Properties properties = new Properties();
		properties.put("sql.votable.head.desc", TEST_HEADER);
		return properties;
	}
}
