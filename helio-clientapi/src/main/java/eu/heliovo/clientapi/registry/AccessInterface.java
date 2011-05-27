package eu.heliovo.clientapi.registry;

import java.net.URL;

public interface AccessInterface {
    /**
     * Get the URL assigned with this access interface
     * @return
     */
    public URL getUrl();
    
    /**
     * Get the type of the interface.
     * @return the interface type.
     */
    public AccessInterfaceType getInterfaceType();
    
    

}
