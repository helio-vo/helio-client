package eu.heliovo.clientapi.result;

import java.util.UUID;

/**
 * ID that qualifies a specific Result. The Id can be used to query the client for further information about
 * a result. 
 * The Id consists of three parts separated by a colon (':'). 
 * <ul>
 * <li>clientId (16 char): Hex String representation of a 128 bit {@link UUID}.</li>
 * <li>userId (8 char): Hex String of a user id. User Id 'FFFFFFFF' is reserved for user 'anonymous'.</li>
 * <li>transactionId (8 char): Hex String of a transaction id. This is sequentially increased hex number. </li>
 * </ul>
 * The ID is created by the client API layer. It may be sent to services. 
 * @author marco soldati at fhnw ch
 *
 */
public final class ResultId {
	/**
	 * The unique id of the client. This value needs to be calculated at installation time. 
	 * http://java.sun.com/j2se/1.5.0/docs/api/java/util/UUID.html may be of help.
	 */
	private final String clientId;
	
	/**
	 * The user id is based on the users name and should be unique in the whole system.
	 *  
	 */
	private final String userId;
	
	/**
	 * The transactionId.
	 */
	private final String transactionId;

	/**
	 * Create the Job Id
	 * @param clientId the client id. Must have length 36 and be a valid hex number. Must not be null.
	 * @param userId user id. Must have length 8 and be a valid hex number. Must not be null.
	 * @param transactionId. Must have length 8 and be a valid hex number. Must not be null.
	 * @throws IllegalArgumentException if any of the arguments is not valid.
	 */
	public ResultId(String clientId, String userId, String transactionId) {
		validate(clientId, "clientId", 36);
		this.clientId = clientId;
		this.userId = userId;
		this.transactionId = transactionId;
	}

	/**
	 * Validate a the part of an id
	 * @param idPart the part to validate
	 * @param idPartName the name of the part
	 * @param expectedLen the expected length
	 * @throws IllegalArgumentException if the id part is not valid 
	 */
	private void validate(String idPart, String idPartName, int expectedLen) throws IllegalArgumentException {
		if (idPart == null) {
			throw new IllegalArgumentException("Id part '" + idPartName + "' must not be null.");			
		}		
		if (idPart.length() != expectedLen) {			
			throw new IllegalArgumentException("Length of id part '" + idPartName + "' expected to be " + expectedLen + ", but is " + idPart.length() + ": " + idPart);			
		}
//		try { 
//			Long.parseLong(idPart, 16);
//		} catch (NumberFormatException e) {
//			throw new IllegalArgumentException("Id part '" + idPartName + "' does not contain a valid hex number: " + idPart + ". Cause: " + e.getMessage(), e);						
//		}		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(clientId).append(":").append(userId).append(":").append(transactionId);
		return sb.toString();
	}	
}
