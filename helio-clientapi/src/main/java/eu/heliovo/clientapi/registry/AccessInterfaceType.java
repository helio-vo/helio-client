package eu.heliovo.clientapi.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Type of an access interface.
 * This class provides some predefined types, but allows to be extended by 
 * additional types.
 * @author MarcoSoldati
 *
 */
public class AccessInterfaceType implements Iterable<AccessInterfaceType> {
    
    private static final Map<String, AccessInterfaceType> enumMap = new HashMap<String, AccessInterfaceType>();
    
    public static final AccessInterfaceType SOAP_SERVICE = register("SOAP_SERVICE", "vr:WebService");
    
    public static final AccessInterfaceType REST_SERVICE = register("REST_SERVICE", "vs:ParamHTTP");
    
    /**
     * Interface type is not specified (i.e. null).
     */
    public static final AccessInterfaceType UNDEFINED = register("undefined", null);
    
    /**
     * Name of the interface type
     */
    private final String name;
    
    /**
     * the xsitype
     */
    private final String xsiType;

    /**
     * Create the access interface
     * @param name the name of the interface
     * @param xsiType the type of the interface
     */
    private AccessInterfaceType(String name, String xsiType) {
        AssertUtil.assertArgumentNotNull(name, "name");
        this.name = name;
        this.xsiType = xsiType;
    }
    
    /**
     * Get the internal name of the interface type
     * @return the internal name of the interface type
     */
    public String getName() {
        return name;
    }
    
    /**
     * The XSI type of the interface type including the namespace part (if any).
     * @return the XSI type.
     */
    public String getXsiType() {
        return xsiType;
    }
    
    /**
     * Register an access interface type if it's not already there.
     * @param name the name of the type. must not be null. primary key.
     * @param xsiType the xsiType. May be null.
     * @return the access type either from the map or newly created.
     */
    public static synchronized AccessInterfaceType register(String name, String xsiType) {
        AccessInterfaceType accessInterfaceType = enumMap.get(name);
        if (accessInterfaceType == null) {
            accessInterfaceType = new AccessInterfaceType(name, xsiType);
            enumMap.put(name, accessInterfaceType);
        }
        return accessInterfaceType;
    }

    /**
     * Find the access interface type by XSI type.
     * @param xsiType the XSI Type
     * @return the access interface type.
     */
    public static AccessInterfaceType findInterfaceTypeByXsiType(String xsiType) {
        if (xsiType == null) {
            return UNDEFINED;
        }
        
        for (AccessInterfaceType type : values()) {
            if (xsiType.equals(type.xsiType)) {
                return type;
            }
        }
        return null;
    }

    private static Collection<AccessInterfaceType> values() {
        return enumMap.values();
    }

    @Override
    public Iterator<AccessInterfaceType> iterator() {
        return enumMap.values().iterator();
    }
    
    @Override
    public String toString() {
        return name;
    }
}