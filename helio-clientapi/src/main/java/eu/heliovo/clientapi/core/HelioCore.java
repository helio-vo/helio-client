package eu.heliovo.clientapi.core;


/**
 * Provide the core methods to use the helio API.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioCore {
  /**
   * Login with the default credentials.
   * Credentials can be stored in a configuration file. If no credentials
   * are stored the anonymous identity will be used.
   */
  public void login();
  
  /**
   * Login as a specific user.
   * 
   * @param username
   * @param password
   */
  public void login(String username,String password);

 
}
