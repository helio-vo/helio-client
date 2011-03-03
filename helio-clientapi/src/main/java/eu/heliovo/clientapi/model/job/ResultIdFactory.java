package eu.heliovo.clientapi.model.job;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.UUID;

public class ResultIdFactory {
	/**
	 * The current time in millis
	 */
	private static long idCounter = System.currentTimeMillis();

	/**
	 * Create a result Id.
	 * @param userId the id of the current user.
	 * @param transactionId a transaction id.
	 * @return
	 */
	public static ResultId createResultId(String userId, String transactionId) {
		String clientId = getClientId();
		return new ResultId(clientId, userId, transactionId);
	}

	/**
	 * Read the client id from the configuration.
	 * @return the client id.
	 */
	private static String getClientId() {
		try {
			return UUID.nameUUIDFromBytes(Inet4Address.getLocalHost().getAddress()).toString();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Unable to load address of local host: " + e.getMessage(), e);
		}
	}

	/**
	 * Generate a new transaction id. The current implementation
	 * provides a incremented counter as hex string.
	 * <p><b>Warning:</b> The generated id is unique only per JVM instance.</b></p>. 
	 * @return a new unique id. .
	 */
	public synchronized static String newTransactionId() {
		idCounter++;
		return Long.toHexString(idCounter);
	}
}
