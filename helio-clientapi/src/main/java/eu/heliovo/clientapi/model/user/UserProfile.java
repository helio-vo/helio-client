package eu.heliovo.clientapi.model.user;

import java.io.Serializable;

import eu.heliovo.clientapi.model.user.data.DataStore;

/**
 * Main entry class to the user profile. 
 * Every user in Helio, be it a guest or a signed in user has a
 * dedicated UserProfile. This profile is used to store properties
 * as well as for storing intermediate results or managing running Jobs.
 * @author marco soldati at fhnw ch
 */
public interface UserProfile extends Serializable {

	/**
	 * Get the data store for this profile.
	 */
	public DataStore getDataStore();
	
	
	
}
