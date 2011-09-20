package eu.heliovo.clientapi.utils;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity class to represent a persisted VOTable. The actual table is stored in a
 * file whose path is returned by getPersistedFilePath()
 * 
 * @author SimonFelix
 */
@Entity
public class PersistedFile
{
  /**
   * The path where files are peristsed.
   */
  static final String PERSISTED_FILES_PATH="persist/";
  
  /**
   * The unique ID of a persisted file
   */
  @Id
  public String id;
  
  /**
   * The date and time when this file was persisted
   */
  @Column
  public Date created;
  
  /**
   * The date and time after which this file can be discarded
   */
  @Column
  public Date expires;
  
  
  /**
   * Constructor for use by Hibernate
   */
  public PersistedFile()
  {
  }
  
  /**
   * Creates a representation of a persisted file. Useful for lookups.
   * 
   * @param _id The id of the persisted file
   */
  public PersistedFile(String _id)
  {
    //check whether the id is valid, format-wise
    if(!_id.matches("\\p{XDigit}{8}\\-\\p{XDigit}{4}\\-\\p{XDigit}{4}\\-\\p{XDigit}{4}\\-\\p{XDigit}{12}"))
      throw new IllegalArgumentException("Supplied id is not in the expected format. Only ids returned by persist(...) are supported.");
    
    id=_id;
  }
  
  /**
   * Creates a new persisted file with a unique ID and a specified expiration date.
   * Note that no underlying file will be created.
   * 
   * @param _expiration The date after which this file can be safely discarded
   * @return a new instance of a persisted file.
   */
  public static PersistedFile createNewPersistedFile(Date _expiration)
  {
    PersistedFile pf=new PersistedFile(UUID.randomUUID().toString());
    pf.created=new Date();
    pf.expires=_expiration;
    
    return pf;
  }
  
  /**
   * Returns the path of the underlying file.
   * 
   * @return The path
   */
  public String getPersistedFilePath()
  {
    return PERSISTED_FILES_PATH+id;
  }
}
