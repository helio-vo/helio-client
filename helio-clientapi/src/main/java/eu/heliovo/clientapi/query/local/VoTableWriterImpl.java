package eu.heliovo.clientapi.query.local;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	public VoTableWriterImpl() {}
	
	@Override
	public void writeVoTableToXml(Writer outWriter, StarTable[] starTables) {
		AssertUtil.assertArgumentNotNull(starTables, "starTables");
		
		TagWriter tagWriter = new TagWriter(outWriter);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("version", VoTableTag.VOTABLE_VERSION);
		attributes.put("xmlns", VoTableTag.XMLNS);
		
		tagWriter.openTag(VoTableTag.VOTABLE, attributes).newLine();
		{
			writeDescription(tagWriter, starTables);
			writeResources(tagWriter, starTables);
		}
		tagWriter.closeTag(VoTableTag.VOTABLE).newLine();
	}
	
	private void writeDescription(TagWriter tagWriter, StarTable[] starTables) {
		tagWriter.tag(VoTableTag.DESCRIPTION, properties.getProperty("sql.votable.head.desc")).newLine(); 
		writeInfoTags(tagWriter);
	}
	
	private void writeInfoTags(TagWriter tagWriter) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", "QUERY_STATUS");
		attributes.put("value", "todo"); //Value: comCriteriaTO.getQueryStatus()
		tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
		
		attributes.clear();
		attributes.put("name", "EXECUTED_AT");
		attributes.put("value", now());
		tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
		
//		if (comCriteriaTO.getMaxRecordsAllowed() > 0) {
//			attributes.clear();
//			attributes.put("name", "MAX_RECORD_ALLOWED");
//			attributes.put("value", "todo").newLine(); //Value: comCriteriaTO.getMaxRecordsAllowed()
//		}
		
		attributes.clear();
		attributes.put("name", "QUERY_STRING");
		attributes.put("value", "todo"); //Value: "<![CDATA["	+ comCriteriaTO.getQueryArray()[i] + "]]>"
		tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
		
		attributes.clear();
		attributes.put("name", "QUERY_URL");
		attributes.put("value", "todo"); //Value: + "<![CDATA["+ CommonUtils.getFullRequestUrl(comCriteriaTO) + "]]>"
		tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
		
//		int rowCount = comCriteriaTO.getQueryReturnCountArray()[i];
//		boolean overflow = false;
//		if (rowCount >= 0) {
//			bf.write("<INFO name=\"RETURNED_ROWS\" value=\"" + rowCount
//					+ "\"/>\n");
//			
//			attributes.clear();
//			attributes.put("name", "RETURNED_ROWS");
//			attributes.put("value", "todo"); //Value: rowCount
//			tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
//			
//			
//			if (rowCount > comCriteriaTO.getMaxRecordsAllowed()) {
//				// Extra checks means this or statement cannot happen: ||
//				// rowCount==Integer.parseInt(comCriteriaTO.getNoOfRows())){
//				attributes.clear();
//				attributes.put("name", "QUERY_STATUS");
//				attributes.put("value", "OVERFLOW");
//				tagWriter.emptyTag(VoTableTag.INFO, attributes).newLine();
//				overflow = true;
//			}
//		}
	}
	
	private static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

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
