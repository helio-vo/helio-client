package eu.heliovo.clientapi.query.local;

import java.io.Writer;

import uk.ac.starlink.table.StarTable;

/**
 * Interface to write VoTable
 * @author junia schoch at fhnw ch
 *
 */
public interface VoTableWriter {
	
	/**
	 * Write StarTables as VoTable in xml format.
	 * @param outWriter must not be null
	 * @param starTables must not be null
	 */
	public void writeVoTableToXml(Writer outWriter, StarTable[] starTables);
}
