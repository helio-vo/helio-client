package eu.heliovo.clientapi.core;

/**
 * Interface to deal with configuration settings.
 * TODO: This is just a placeholder for a later, more sophisitcated implementation.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioConfiguration {
	
	/**
	 * Set a specific property in the system.
	 * This may be reach from setting up logging to setting the 
	 * preferred data provider in a given context.
	 * @param propertyName the name of the property.
	 * @param propertyValue the according value.
	 */
	public void setProperty(String propertyName, String propertyValue);
}
