package eu.heliovo.mockclient.core;

import java.util.HashMap;
import java.util.Map;

import eu.heliovo.clientapi.core.HelioConfiguration;

/**
 * Implementation of the Helio Configuration.
 * @author marco soldati at fhnw ch
 *
 */
public class HelioConfigurationImpl implements HelioConfiguration {
	
	/**
	 * Map to store the configuration values.
	 */
	private Map<String, Object> configuration = new HashMap<String, Object>();

	@Override
	public void setProperty(String propertyName, String propertyValue) {
		configuration.put(propertyName, propertyValue);
	}

	
	
}
