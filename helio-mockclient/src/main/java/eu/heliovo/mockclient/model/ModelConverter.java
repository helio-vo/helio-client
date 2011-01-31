package eu.heliovo.mockclient.model;

import java.io.InputStream;

import eu.heliovo.clientapi.model.HelioRoot;

public interface ModelConverter {

	/**
	 * Convert a VoTable to the Helio Data model
	 * @param voTable the VOTable to convert.
	 * @return the generated object model.
	 */
	public HelioRoot convert(InputStream voTable);
	
}
