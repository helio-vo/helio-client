package eu.heliovo.clientapi.model.catalog.descriptor;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Date;

import eu.heliovo.clientapi.config.ConfigurablePropertyDescriptor;
import eu.heliovo.clientapi.model.DomainValueDescriptor;

/**
 * Descriptor for one value that is permitted as "from"-value in a HEC catalogue.
 * Multiple descriptors can be rendered as a table.
 * @author MarcoSoldati
 *
 */
public class EventListDescriptor extends AbstractCatalogueDescriptor implements DomainValueDescriptor<String> {
    
    /**
     * Bean info class for the {@link EventListDescriptor}
     * @author MarcoSoldati
     *
     */
    public static class EventListDescriptorBeanInfo extends AbstractCatalogueDescriptor.AbstractCatalogueDescriptorBeanInfo<EventListDescriptor> {
        private static EventListDescriptorBeanInfo instance = new EventListDescriptorBeanInfo();
        
        /**
         * Get the singleton instance of the BeanInfo
         * @return the catalog descriptor
         */
        public static EventListDescriptorBeanInfo getInstance() {
            return instance;
        }

        /**
         * Cache for the property descriptors.
         */
        private final PropertyDescriptor[] propertyDescriptors;
        
        /**
         * Hide the default constructor. Use getInstance() instead.
         */
        private EventListDescriptorBeanInfo() {
            super(EventListDescriptor.class);
                propertyDescriptors = new ConfigurablePropertyDescriptor<?>[] {
                    createPropertyDescriptor("name", "Name", "Catalogue Name"),
                    createPropertyDescriptor("description", "Description", "Short description of the catalogue"),
                    createPropertyDescriptor("timefrom", "From", "Date of the earliest event in the catalogue"),
                    createPropertyDescriptor("timeto", "To", "Date of the latest event in the catalogue"),
                    createPropertyDescriptor("type", "Type", "Type of the list"),
                    createPropertyDescriptor("status", "Status",
                            "Active - The catalogue is being constantly updated,\n" +
                            "Static - The catalogue is not regularly updated at source.\n" +
                            "Inactive - The catalogue is currently not maintained at source,\n" +
                            "Closed - The catalogue is closed (i.e. from a paper) and will not be updated anymore"
                            ),
                    createPropertyDescriptor("flare", "Flare list?", "Is it a flare list?"),
                    createPropertyDescriptor("cme", "CME list?", "Is it a CME list?"),
                    createPropertyDescriptor("swind", "Solar wind list?", "Is it a Solar wind list?"),
                    createPropertyDescriptor("part", "Particle?", "Is it a Particle list?"),
                    createPropertyDescriptor("otyp", "Observation type", "Observation type: insitu or remote"),
                    createPropertyDescriptor("solar", "Solar Location", "Did the event occur on the Sun?"),
                    createPropertyDescriptor("ips", "Interplan. space", "Did the event occur in the Interplanetary Space?"),
                    createPropertyDescriptor("geo", "Geo", "Did the event occur on the Earth?"),
                    createPropertyDescriptor("planet", "Planet", "Did the event occur on any planet?"),
                    createPropertyDescriptor("infoUrl", "Info", "More information about a catalogue", true),
                };
        }
        
        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            return propertyDescriptors;
        }
    }
    
    /**
     * Template for the URL string
     */
    private static final String URL_TEMPLATE = "http://hec.helio-vo.eu/hec/stfc/HEC_ListsAll.html#%1$s";
    
    
    private String name;
    private String description;
    private Date timefrom;
    private Date timeto;
    private String type;
    private String status;
    private boolean flare;
    private boolean cme;
    private boolean swind;
    private boolean part;
    private String otyp;
    private boolean solar;
    private boolean ips;
    private boolean geo;
    private boolean planet;

    @Override
    public BeanInfo getBeanInfo() {
        return EventListDescriptorBeanInfo.getInstance();
    }
    
    @Override
    public String getValue() {
        return name;
    }

    @Override
    public String getLabel() {
        return description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the timefrom
     */
    public Date getTimefrom() {
        return timefrom;
    }

    /**
     * @param timefrom the timefrom to set
     */
    public void setTimefrom(String timefrom) {
        this.timefrom = toDate(timefrom);
    }

    /**
     * @param timefrom the timefrom to set
     */
    public void setTimefrom(Date timefrom) {
        this.timefrom = timefrom;
    }

    /**
     * @return the timeto
     */
    public Date getTimeto() {
        return timeto;
    }

    /**
     * @param timeto the timeto to set
     */
    public void setTimeto(String timeto) {
        this.timeto = toDate(timeto);
    }

    /**
     * @param timeto the timeto to set
     */
    public void setTimeto(Date timeto) {
        this.timeto = timeto;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the flare
     */
    public boolean isFlare() {
        return flare;
    }

    /**
     * @param flare the flare to set
     */
    public void setFlare(boolean flare) {
        this.flare = flare;
    }

    /**
     * @param flare the flare to set
     */
    public void setFlare(String flare) {
        this.flare = "y".equals(flare);
    }

    /**
     * @return the cme
     */
    public boolean isCme() {
        return cme;
    }

    /**
     * @param cme the cme to set
     */
    public void setCme(boolean cme) {
        this.cme = cme;
    }
    
    /**
     * @param cme the cme to set
     */
    public void setCme(String cme) {
        this.cme = "y".equals(cme);
    }

    /**
     * @return the swind
     */
    public boolean isSwind() {
        return swind;
    }

    /**
     * @param swind the swind to set
     */
    public void setSwind(boolean swind) {
        this.swind = swind;
    }
    
    /**
     * @param swind the swind to set
     */
    public void setSwind(String swind) {
        this.swind = "y".equals(swind);
    }

    /**
     * @return the part
     */
    public boolean isPart() {
        return part;
    }

    /**
     * @param part the part to set
     */
    public void setPart(boolean part) {
        this.part = part;
    }

    /**
     * @param part the part to set
     */
    public void setPart(String part) {
        this.part = "y".equals(part);
    }

    /**
     * @return the otyp
     */
    public String getOtyp() {
        return otyp;
    }

    /**
     * @param otyp the otyp to set
     */
    public void setOtyp(String otyp) {
        this.otyp = otyp;
    }

    /**
     * @return the solar
     */
    public boolean isSolar() {
        return solar;
    }

    /**
     * @param solar the solar to set
     */
    public void setSolar(boolean solar) {
        this.solar = solar;
    }
    
    /**
     * @param solar the solar to set
     */
    public void setSolar(String solar) {
        this.solar = "y".equals(solar);
    }

    /**
     * @return the ips
     */
    public boolean isIps() {
        return ips;
    }

    /**
     * @param ips the ips to set
     */
    public void setIps(boolean ips) {
        this.ips = ips;
    }
    
    /**
     * @param ips the ips to set
     */
    public void setIps(String ips) {
        this.ips = "y".equals(ips);
    }

    /**
     * @return the geo
     */
    public boolean isGeo() {
        return geo;
    }

    /**
     * @param geo the geo to set
     */
    public void setGeo(boolean geo) {
        this.geo = geo;
    }
    
    /**
     * @param geo the geo to set
     */
    public void setGeo(String geo) {
        this.geo = "y".equals(geo);
    }

    /**
     * @return the planet
     */
    public boolean isPlanet() {
        return planet;
    }

    /**
     * @param planet the planet to set
     */
    public void setPlanet(boolean planet) {
        this.planet = planet;
    }
    
    /**
     * @param planet the planet to set
     */
    public void setPlanet(String planet) {
        this.planet = "y".equals(planet);
    }

    /**
     * @return the url
     */
    public String getInfoUrl() {
        return String.format(URL_TEMPLATE, name);
    }
}