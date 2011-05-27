package eu.heliovo.clientapi.registry.impl;

import java.net.URL;

import eu.heliovo.clientapi.registry.AccessInterface;
import eu.heliovo.clientapi.registry.AccessInterfaceType;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Default implementation of the access interface
 * @author MarcoSoldati
 *
 */
public class AccessInterfaceImpl implements AccessInterface {

    /**
     * the interface type
     */
    private final AccessInterfaceType type;
    
    /**
     * the endpoint of the interface
     */
    private final URL url;

    /**
     * The type of the interface
     * @param type the interface type
     * @param url the URL of the endpoint
     */
    public AccessInterfaceImpl(AccessInterfaceType type, URL url) {
        AssertUtil.assertArgumentNotNull(type, "type");
        AssertUtil.assertArgumentNotNull(url, "url");
        this.type = type;
        this.url = url;
    }
    
    @Override
    public URL getUrl() {
        return url;
    }
    
    @Override
    public AccessInterfaceType getInterfaceType() {
        return type;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccessInterfaceImpl [");
        sb.append("url:").append(url);
        sb.append("type:").append(type);
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AccessInterface) {
            AccessInterface other = (AccessInterface) obj;
            return (other.getInterfaceType().equals(this.getInterfaceType()) 
                &&  other.getUrl().equals(this.getUrl()));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode() * 13 + this.url.hashCode() * 37;
    }
}
