package eu.heliovo.clientapi.query.local;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;



//import eu.heliovo.queryservice.common.util.CommonUtils;
//import eu.heliovo.queryservice.common.util.ConfigurationProfiler;
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
public class VoTableWriter extends TagWriter {
	private StarTable[] starTables;

	public VoTableWriter(Writer outWriter, StarTable[] starTable) {
		super(outWriter);
		AssertUtil.assertArgumentNotNull(starTable, "starTable");
		this.starTables = starTable;
	}
	
	public void writeVoTableToXml() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("version", VoTableTag.VOTABLE_VERSION);
		attributes.put("xmlns", VoTableTag.XMLNS);
		
		openTag(VoTableTag.VOTABLE, attributes).newLine();
		{
			writeDescription();
			writeResources();
		}
		closeTag(VoTableTag.VOTABLE).newLine();
	}
	
	private void writeDescription() {
		tag(VoTableTag.DESCRIPTION, "todo").newLine(); //Content: ConfigurationProfiler.getInstance().getProperty("sql.votable.head.desc"); 
		writeInfoTags();
	}
	
	private void writeInfoTags() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", "QUERY_STATUS");
		attributes.put("value", "todo"); //Value: comCriteriaTO.getQueryStatus()
		emptyTag(VoTableTag.INFO, attributes).newLine();
		
		attributes.clear();
		attributes.put("name", "EXECUTED_AT");
		attributes.put("value", "todo"); //Value: currentDate?
		emptyTag(VoTableTag.INFO, attributes).newLine();
		
//		if (comCriteriaTO.getMaxRecordsAllowed() > 0) {
//			attributes.clear();
//			attributes.put("name", "MAX_RECORD_ALLOWED");
//			attributes.put("value", "todo").newLine(); //Value: comCriteriaTO.getMaxRecordsAllowed()
//		}
		
		attributes.clear();
		attributes.put("name", "QUERY_STRING");
		attributes.put("value", "todo"); //Value: "<![CDATA["	+ comCriteriaTO.getQueryArray()[i] + "]]>"
		emptyTag(VoTableTag.INFO, attributes).newLine();
		
		attributes.clear();
		attributes.put("name", "QUERY_URL");
		attributes.put("value", "todo"); //Value: + "<![CDATA["+ CommonUtils.getFullRequestUrl(comCriteriaTO) + "]]>"
		emptyTag(VoTableTag.INFO, attributes).newLine();
		
//		int rowCount = comCriteriaTO.getQueryReturnCountArray()[i];
//		boolean overflow = false;
//		if (rowCount >= 0) {
//			bf.write("<INFO name=\"RETURNED_ROWS\" value=\"" + rowCount
//					+ "\"/>\n");
//			
//			attributes.clear();
//			attributes.put("name", "RETURNED_ROWS");
//			attributes.put("value", "todo"); //Value: rowCount
//			emptyTag(VoTableTag.INFO, attributes).newLine();
//			
//			
//			if (rowCount > comCriteriaTO.getMaxRecordsAllowed()) {
//				// Extra checks means this or statement cannot happen: ||
//				// rowCount==Integer.parseInt(comCriteriaTO.getNoOfRows())){
//				attributes.clear();
//				attributes.put("name", "QUERY_STATUS");
//				attributes.put("value", "OVERFLOW");
//				emptyTag(VoTableTag.INFO, attributes).newLine();
//				overflow = true;
//			}
//		}
	}
	
	private void writeResources() {
		for(StarTable table:starTables) {
			writeResource(table);
		}
	}

	private void writeResource(StarTable starTable) {
		openTag(VoTableTag.RESOURCE).newLine();
		{
			try (StringWriter sw = new StringWriter(); BufferedWriter bf = new BufferedWriter(sw)) {
				VOSerializer.makeSerializer( DataFormat.TABLEDATA, starTable ).writeInlineTableElement(bf);
				bf.flush();
				getOut().append(sw.getBuffer().toString());
			} catch (IOException e) {
				throw new HelioClientException("failed to write voTable", e);
			}
		}
		closeTag(VoTableTag.RESOURCE).newLine();
	}
}
