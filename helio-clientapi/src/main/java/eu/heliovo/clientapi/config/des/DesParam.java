package eu.heliovo.clientapi.config.des;

/**
 * Description of a DES parameter
 * @author MarcoSoldati
 */
public class DesParam {
    private final String id;
    private final String name;
    private final String fullName;
    private final String unit;
    
    public DesParam(String id, String name, String fullName, String unit) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.unit = unit;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }
    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }
}
