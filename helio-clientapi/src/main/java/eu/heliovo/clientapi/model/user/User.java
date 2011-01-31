package eu.heliovo.clientapi.model.user;

/**
 * Definition of a helio user. Typically this is implemented as a wrapper for the Helio user.
 * @author marco soldati at fhnw ch
 *
 */
public interface User {

	/**
	 * Get the user name.
	 * @return user name
	 */
	public String getUserName();
	
	/**
	 * Get the unique Id of the user. This id should not change at any time.
	 * @return the id of the user. 
	 */
	public String getUserId();
	
	/**
	 * Get the last name of the user.
	 * @return the user's last name.
	 */
	public String getLastName();
	
	/**
	 * Get the first name of the user.
	 * @return the user's first name.
	 */
	public String getFirstName();
}
