package eu.heliovo.clientapi.query.local;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.starlink.table.StarTable;

/**
 * Test for {@link TagWriter}
 * @author junia schoch at fhnw ch
 *
 */
public class TagWriterTest {
	private StringWriter writer = null;
	TagWriter tagWriter;
	
	private static final String TAG_NAME = "name";
	private static final String TAG_CONTENT = "content";
	private static final String ATTR_KEY = "key";
	private static final String ATTR_VALUE = "value";
	private static final String NEWLINE = System.getProperty("line.separator");
	private static Map<String, String> attributes = new HashMap<String, String>();
	
	
	@Before
	public void setup() {
		writer = new StringWriter();
		attributes.put(ATTR_KEY, ATTR_VALUE);
	}
	
	@After
	public void tearDown() throws Exception {
		writer.close();
		writer = null;
	}
	
	@Test (expected=IllegalArgumentException.class)	
	public void test_null_writer() {
		tagWriter = new TagWriter(null);
	}
	
	@Test
	public void test_getOut() {
		tagWriter = getValidTagWriter();
		assertEquals(writer, tagWriter.getOut());
	}

	@Test
	public void test_tag() {
		tagWriter = getValidTagWriter();
		String expectedString = "<" + TAG_NAME + ">" + TAG_CONTENT + "</" + TAG_NAME + ">";
		
		tagWriter.tag(TAG_NAME, TAG_CONTENT);
		try {
			tagWriter.getOut().flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test
	public void test_tag_attributes() {
		tagWriter = getValidTagWriter();
		String expectedString = "<" + TAG_NAME + " " + ATTR_KEY + "=\"" + ATTR_VALUE + "\">" + TAG_CONTENT + "</" + TAG_NAME + ">";
		
		tagWriter.tag(TAG_NAME, attributes, TAG_CONTENT);
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test
	public void test_openTag() {
		tagWriter = getValidTagWriter();
		String expectedString = "<" + TAG_NAME + ">";
		
		tagWriter.openTag(TAG_NAME);
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test
	public void test_openTag_attributes() {
		tagWriter = getValidTagWriter();
		String expectedString = "<" + TAG_NAME + " " + ATTR_KEY + "=\"" + ATTR_VALUE + "\">";
		
		tagWriter.openTag(TAG_NAME, attributes);
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test 
	public void test_closeTag() {
		tagWriter = getValidTagWriter();
		String expectedString = "</" + TAG_NAME + ">";
		
		tagWriter.closeTag(TAG_NAME);
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test 
	public void test_emptyTag() {
		tagWriter = getValidTagWriter();
		String expectedString = "<" + TAG_NAME + " " + ATTR_KEY + "=\"" + ATTR_VALUE + "\"/>";
		
		tagWriter.emptyTag(TAG_NAME, attributes);
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test
	public void test_text() {
		tagWriter = getValidTagWriter();
		String expectedString = TAG_NAME;
		
		tagWriter.text(TAG_NAME);
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	@Test
	public void test_newLine() {
		tagWriter = getValidTagWriter();
		String expectedString = NEWLINE;
		tagWriter.newLine();
		assertEquals(expectedString, tagWriter.getOut().toString());
	}
	
	private TagWriter getValidTagWriter() {
		TagWriter tagWriter = new TagWriter(writer);
		return tagWriter;
	}
	
	
}
