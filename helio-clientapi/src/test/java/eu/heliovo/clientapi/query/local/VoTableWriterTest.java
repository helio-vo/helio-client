package eu.heliovo.clientapi.query.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.util.DataSource;

/**
 * Test class for {@link VoTableWriter}
 * @author junia schoch at fhnw ch
 *
 */
public class VoTableWriterTest {

	private StringWriter writer = null;
	
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
		VoTableWriter votableWriter = new VoTableWriter(writer, starTable);
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
		VoTableWriter votableWriter = new VoTableWriter(writer, new StarTable[]{starTable});
		votableWriter.writeVoTableToXml();
		System.out.println(writer.getBuffer());
	}
}
