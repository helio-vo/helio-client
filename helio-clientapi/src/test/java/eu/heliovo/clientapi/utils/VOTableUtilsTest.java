package eu.heliovo.clientapi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.StringWriter;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.junit.Test;

/**
 * The the VOTable utils.
 * @author MarcoSoldati
 *
 */
public class VOTableUtilsTest {

	/**
	 * Convert the voTable to a string.
	 */
	@Test public void testVoTable2String() {
		VOTABLE voTable = new VOTABLE();
		String table = VOTableUtils.getInstance().voTable2String(voTable, false);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><VOTABLE xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\"/>", table);
		table = VOTableUtils.getInstance().voTable2String(voTable, true);
		assertFalse("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><VOTABLE xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\"/>".equals(table));
	}
	
	/**
	 * Test {@link VOTableUtils#voTable2Writer(VOTABLE, java.io.Writer, boolean)}
	 */
	@Test public void testVoTable2Writer() {
		VOTABLE voTable = new VOTABLE();
		StringWriter writer = new StringWriter();
		VOTableUtils.getInstance().voTable2Writer(voTable, writer, false);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><VOTABLE xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\"/>", writer.getBuffer().toString());
	}
}
