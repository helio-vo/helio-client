package eu.heliovo.clientapi.model.user.data;


/**
 * Top level object of the user profile. The profile is used to store
 * all user related metadata.
 * @author marco soldati at fhnw ch
 *
 */
public interface UserProfile {

	/**
	 * Return the data store assigned to a particular user.
	 * @return the user's data store.
	 */
	public DataStore getDataStore();
	
	
	
	
}
