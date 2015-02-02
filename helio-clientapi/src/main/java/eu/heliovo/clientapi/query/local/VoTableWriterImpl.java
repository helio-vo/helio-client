package eu.heliovo.clientapi.query.local;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.votable.DataFormat;
import uk.ac.starlink.votable.VOSerializer;
import eu.heliovo.clientapi.HelioClientException;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Serializes StarTable(s) to xml tags.
 * @author junia schoch at fhnw ch
 * 
 */
public class VoTableWriterImpl implements VoTableWriter{
	private Properties properties;
	public VoTableWriterImpl() {}
	
	@Override
	public void writeVoTableToXml(Writer outWriter, StarTable[] starTables, Map<String, String> attrKeyValueMap) {
		AssertUtil.assertArgumentNotNull(starTables, "starTables");
		
		TagWriter tagWriter = new TagWriter(outWriter);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("version", VoTableTag.VOTABLE_VERSION);
		attributes.put("xmlns", VoTableTag.XMLNS);
		
		tagWriter.openTag(VoTableTag.VOTABLE, attributes).newLine();
		{
			writeDescription(tagWriter, attrKeyValueMap);
			writeResources(tagWriter, starTables);
		}
		tagWriter.closeTag(VoTableTag.VOTABLE).newLine();
	}
	
	private void writeDescription(TagWriter tagWriter, Map<String, String> attrKeyValueMap) {
		tagWriter.tag(VoTableTag.DESCRIPTION, properties.getProperty("sql.votable.head.desc")).newLine(); 
		writeInfoTags(tagWriter, attrKeyValueMap);
	}
	
	private void writeInfoTags(TagWriter tagWriter, Map<String, String> attrKeyValueMap) {
		for (Map.Entry<String, String> entry : attrKeyValueMap.entrySet())
		{
		    writeInfoTag(tagWriter, entry.getKey(), entry.getValue());
		}
	}
	
	private void writeInfoTag(TagWriter tagWriter, String key, String value) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", key);
		attributes.put("value", value);
		tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
	}
	
	private void writeResources(TagWriter tagWriter, StarTable[] starTables) {
		for(StarTable table:starTables) {
			writeResource(tagWriter, table);
		}
	}

	private void writeResource(TagWriter tagWriter, StarTable starTable) {
		tagWriter.openTag(VoTableTag.RESOURCE).newLine();
		{
			try (StringWriter sw = new StringWriter(); BufferedWriter bf = new BufferedWriter(sw)) {
				VOSerializer.makeSerializer( DataFormat.TABLEDATA, starTable ).writeInlineTableElement(bf);
				bf.flush();
				tagWriter.getOut().append(sw.getBuffer().toString());
			} catch (IOException e) {
				throw new HelioClientException("failed to write voTable", e);
			}
		}
		tagWriter.closeTag(VoTableTag.RESOURCE).newLine();
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
