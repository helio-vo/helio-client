package eu.heliovo.clientapi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.junit.Test;

/**
 * The the VOTable utils.
 * @author MarcoSoldati
 *
 */
public class VOTableUtilsTest {

	private static final String SERIALIZED_TABLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><VOTABLE xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\"/>";

	/**
	 * Convert the voTable to a string.
	 */
	@Test public void testVoTable2String() {
		VOTABLE voTable = new VOTABLE();
		String table = VOTableUtils.getInstance().voTable2String(voTable, false);
		assertEquals(SERIALIZED_TABLE, table);
		table = VOTableUtils.getInstance().voTable2String(voTable, true);
		assertFalse(SERIALIZED_TABLE.equals(table));
	}
	
	/**
	 * Test {@link VOTableUtils#voTable2Writer(VOTABLE, java.io.Writer, boolean)}
	 */
	@Test public void testVoTable2Writer() {
		VOTABLE voTable = new VOTABLE();
		StringWriter writer = new StringWriter();
		VOTableUtils.getInstance().voTable2Writer(voTable, writer, false);
		assertEquals(SERIALIZED_TABLE, writer.getBuffer().toString());
	}
	
	/**
	 * Test {@link VOTableUtils#stream2VoTable(java.io.InputStream)}
	 */
	@Test public void testStream2VOTable() throws Exception {
		InputStream stream = new ByteArrayInputStream(SERIALIZED_TABLE.getBytes());
		VOTABLE voTable = VOTableUtils.getInstance().stream2VoTable(stream);
		assertNotNull(voTable);	
	}
	
	/**
	 * Test {@link VOTableUtils#reader2VoTable(Reader)}
	 */
	@Test public void testReader2VoTable() throws Exception {
		Reader reader = new StringReader(SERIALIZED_TABLE);
		VOTABLE voTable = VOTableUtils.getInstance().reader2VoTable(reader);
		assertNotNull(voTable);			
	}
	
	@Test public void testVoTable2DOM() throws Exception {
		
	}
	
}
