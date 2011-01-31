package eu.heliovo.mockclient.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableOutput;
import uk.ac.starlink.table.StarTableWriter;
import uk.ac.starlink.table.TableFormatException;

import eu.heliovo.clientapi.model.HelioRoot;

public class ModelConverterImpl implements ModelConverter {

	@Override
	public HelioRoot convert(InputStream voTable) {
		HelioRootImpl helioRoot = new HelioRootImpl();
		
		return helioRoot;
	}
	
	public static class HelioModelWriter implements StarTableWriter {
		private static final String FORMAT_NAME = "heliomodel"; 
		
		@Override
		public String getFormatName() {
			return FORMAT_NAME;
		}

		@Override
		public String getMimeType() {
			return "application/object";
		}

		@Override
		public boolean looksLikeFile(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void writeStarTable(StarTable starTable, OutputStream out)
				throws TableFormatException, IOException {
			
		}

		@Override
		public void writeStarTable(StarTable starTable, String arg1,
				StarTableOutput out) throws TableFormatException, IOException {	
		}
		
	}
}
