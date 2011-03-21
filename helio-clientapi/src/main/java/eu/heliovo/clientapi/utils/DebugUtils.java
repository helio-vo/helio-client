package eu.heliovo.clientapi.utils;

/**
 * Utility methods to debug SOAP calls.
 * @author MarcoSoldati
 *
 */
public class DebugUtils {

	/**
	 * Switch on dumping of client side jaxws requests and responses.
	 */
	public static void enableDump() {
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
	}
	
	/**
	 * Switch off dumping of client side jaxws requests and responses.
	 */
	public static void disableDump() {
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "false");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "false");
	}
	
}
