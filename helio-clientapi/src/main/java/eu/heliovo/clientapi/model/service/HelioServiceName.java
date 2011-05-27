package eu.heliovo.clientapi.model.service;

/**
 * Enumeration of well known HELIO service names.
 * @author MarcoSoldati
 *
 */
public enum HelioServiceName {
    HEC  ("ivo://helio-vo.eu/hec"),
    DPAS ("ivo://helio-vo.eu/dpas"),
    ILS  ("ivo://helio-vo.eu/ils"),
    ICS  ("ivo://helio-vo.eu/ics"),
    HFC  ("ivo://helio-vo.eu/hfc"),
    DES  ("ivo://helio-vo.eu/des"),
    MDES ("ivo://helio-vo.eu/mdes"),
    UOC  ("ivo://helio-vo.eu/uoc"),
    SMS  ("ivo://helio-vo.eu/sms")
    
    ;
    
    /**
     * Name of the service
     */
    private final String serviceName;

    /**
     * Create the HelioServiceName constant
     * @param serviceName name of the service
     */
    private HelioServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    @Override
    public String toString() {
        return serviceName;
    }

    public String getName() {
        return serviceName;
    }
}
