package eu.heliovo.clientapi.model.catalog.descriptor;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.heliovo.clientapi.model.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;

/**
 * Describe an instrument.
 * 
 * @author MarcoSoldati
 * 
 */
public class InstrumentDescriptor extends AbstractCatalogueDescriptor implements DomainValueDescriptor<String> {
    
    private static final HelioFieldDescriptor<?>[] fieldDescriptors = new HelioFieldDescriptor<?>[]  {
        new HelioFieldDescriptor<String>("name", "Instrument Name", "Instrument Name", FieldType.STRING),
        new HelioFieldDescriptor<String>("observatory_name", "Observatory Name", "Observatory Name", FieldType.STRING),
        new HelioFieldDescriptor<String>("obsinst_key", "HELIO ID", "Helio Instrument Name", FieldType.STRING),
        new HelioFieldDescriptor<String>("experiment_id", "Experiment ID", "Experiment ID", FieldType.STRING),
        new HelioFieldDescriptor<Date>("time_start", "Instrument Start Date", "Date when the instrument started to observe", FieldType.DATETIME),
        new HelioFieldDescriptor<Date>("time_end", "Instrument End Date", "Date when the instrument stopped to observe", FieldType.DATETIME),
        new HelioFieldDescriptor<String>("longname", "Instrument Full Name", "Instrument Full Name", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_type", "Instrument Type", "Type of the instrument", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_od1", "Observation Domain 1", "Instrument observation domain, 1st category", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_od2", "Observation Domain 2", "Instrument observation domain, 2nd category", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_oe1", "Observable Entity", "Instrument observation entity element", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_oe2", "Observable Entity", "Instrument observation entity type", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_fd", "Wavelength FD", "Instrument observation wave length fd", FieldType.STRING),
        new HelioFieldDescriptor<String>("inst_nd", "Wavelength ND", "Instrument observation wavelength nd", FieldType.STRING),
        new HelioFieldDescriptor<String>("group_name", "Group Name", "Group name", FieldType.STRING),
        new HelioFieldDescriptor<String>("keywords", "Keywords", "Complete focusing type", FieldType.STRING),
        new HelioFieldDescriptor<String>("net_key", "Net Key", "Net Key", FieldType.STRING),
        new HelioFieldDescriptor<Boolean>("isinpat", "Is in PAT", "Boolean values to indicate if a field from the ICS " +
                "is contained in the Provider Access Table, i.e. if the Data Access Provider Service is able " +
                "to access the specific instrument.", FieldType.BOOLEAN),

    };
    
    /**
     * Bean info class for the {@link InstrumentDescriptor}
     * @author MarcoSoldati
     *
     */
    static class InstrumentDescriptorBeanInfo extends AbstractCatalogueDescriptor.AbstractCatalogueDescriptorBeanInfo<InstrumentDescriptor> {
        private static InstrumentDescriptorBeanInfo instance = new InstrumentDescriptorBeanInfo();
        
        /**
         * Get the singleton instance of the BeanInfo
         * @return the catalog descriptor
         */
        public static InstrumentDescriptorBeanInfo getInstance() {
            return instance;
        }

        /**
         * Cache for the property descriptors.
         */
        private final PropertyDescriptor[] propertyDescriptors;
        
        /**
         * Hide the default constructor. Use getInstance() instead.
         */
        private InstrumentDescriptorBeanInfo() {
            super(InstrumentDescriptor.class);
                propertyDescriptors = asPropertyDescriptor(fieldDescriptors);
        }
        
        private PropertyDescriptor[] asPropertyDescriptor(HelioFieldDescriptor<?>[] fieldDescriptors) {
            PropertyDescriptor[] ret = new PropertyDescriptor[fieldDescriptors.length];
            for (int i = 0; i < fieldDescriptors.length; i++) {
                HelioFieldDescriptor<?> fd = fieldDescriptors[i];
                ret[i] = createPropertyDescriptor(fd.getId(), fd.getLabel(), fd.getDescription());
            }
            return ret;
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            return propertyDescriptors;
        }
    }

    /**
     * Id of the instrument
     */
    private final String id;

    /**
     * Label presented to the user
     */
    private final String label;

    /**
     * Optional description
     */
    private final String description;

    private String name;
    private String observatoryName;
    private String obsinstKey;
    private String experimentId;
    private Date timeStart;
    private Date timeEnd;
    private String longname;
    private String instType;
    private String instOd1;
    private String instOd2;
    private String instOe1;
    private String instOe2;
    private String instFd;
    private String instNd;
    private String groupName;
    private String keywords;
    private String netKey;
    private boolean isInPat;    
    
    private boolean isInInstrumentsXsd;
    private boolean isInIcs;
    
    /**
     * Store the providers
     */
    private final SortedSet<Provider> providers = new TreeSet<InstrumentDescriptor.Provider>();

    /**
     * The descriptor of an instrument
     * 
     * @param id
     *            must not be null.
     * @param label
     *            must not be null.
     * @param description
     *            optional floating text description of the instrument.
     */
    public InstrumentDescriptor(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }
    
    @Override
    public BeanInfo getBeanInfo() {
        return InstrumentDescriptorBeanInfo.getInstance();
    }

    @Override
    public List<HelioFieldDescriptor<?>> getFieldDescriptors() {
        return Arrays.asList(fieldDescriptors);
    }
    
    @Override
    public String getValue() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the providers property is not empty.
     * @return true if there is at least one registered provider.
     */
    public boolean hasProviders() {
        return providers.size() > 0;
    }

    /**
     * Add a new provider to the set of providers. 
     * 
     * @param provider
     *            the provider name
     * @param archive
     *            the archive name
     * @return the created or loaded provider instance
     */
    public Provider addProvider(String provider, String archive, int priority) {
        Provider newProvider = new Provider(provider, archive, priority);
        providers.add(newProvider);
        return newProvider;
    }
    
    /**
     * Get a copy of the providers assigned to this class. 
     * @return the providers.
     */
    public Set<Provider> getProviders() {
        return providers;
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
     * @return the observatoryName
     */
    public String getObservatoryName() {
        return observatoryName;
    }

    /**
     * @param observatoryName the observatoryName to set
     */
    public void setObservatoryName(String observatoryName) {
        this.observatoryName = observatoryName;
    }

    /**
     * @return the obsinstKey
     */
    public String getObsinstKey() {
        return obsinstKey;
    }

    /**
     * @param obsinstKey the obsinstKey to set
     */
    public void setObsinstKey(String obsinstKey) {
        this.obsinstKey = obsinstKey;
    }

    /**
     * @return the experimentId
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * @param experimentId the experimentId to set
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * @return the timeStart
     */
    public Date getTimeStart() {
        return timeStart;
    }

    /**
     * @param timeStart the timeStart to set
     */
    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }
    
    /**
     * @param timeStart the timeStart to set
     */
    public void setTimeStart(String timeStart) {
        this.timeStart = toDate(timeStart);
    }
    
    /**
     * @return the timeEnd
     */
    public Date getTimeEnd() {
        return timeEnd;
    }

    /**
     * @param timeEnd the timeEnd to set
     */
    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    /**
     * @param timeEnd the timeEnd to set
     */
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = toDate(timeEnd);
    }

    /**
     * @return the longname
     */
    public String getLongname() {
        return longname;
    }

    /**
     * @param longname the longname to set
     */
    public void setLongname(String longname) {
        this.longname = longname;
    }

    /**
     * @return the instType
     */
    public String getInstType() {
        return instType;
    }

    /**
     * @param instType the instType to set
     */
    public void setInstType(String instType) {
        this.instType = instType;
    }

    /**
     * @return the instOd1
     */
    public String getInstOd1() {
        return instOd1;
    }

    /**
     * @param instOd1 the instOd1 to set
     */
    public void setInstOd1(String instOd1) {
        this.instOd1 = instOd1;
    }

    /**
     * @return the instOd2
     */
    public String getInstOd2() {
        return instOd2;
    }

    /**
     * @param instOd2 the instOd2 to set
     */
    public void setInstOd2(String instOd2) {
        this.instOd2 = instOd2;
    }

    /**
     * @return the instOe1
     */
    public String getInstOe1() {
        return instOe1;
    }

    /**
     * @param instOe1 the instOe1 to set
     */
    public void setInstOe1(String instOe1) {
        this.instOe1 = instOe1;
    }

    /**
     * @return the instOe2
     */
    public String getInstOe2() {
        return instOe2;
    }

    /**
     * @param instOe2 the instOe2 to set
     */
    public void setInstOe2(String instOe2) {
        this.instOe2 = instOe2;
    }

    /**
     * @return the instFd
     */
    public String getInstFd() {
        return instFd;
    }

    /**
     * @param instFd the instFd to set
     */
    public void setInstFd(String instFd) {
        this.instFd = instFd;
    }

    /**
     * @return the instNd
     */
    public String getInstNd() {
        return instNd;
    }

    /**
     * @param instNd the instNd to set
     */
    public void setInstNd(String instNd) {
        this.instNd = instNd;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the netKey
     */
    public String getNetKey() {
        return netKey;
    }

    /**
     * @param netKey the netKey to set
     */
    public void setNetKey(String netKey) {
        this.netKey = netKey;
    }

    /**
     * @return the isInPat
     */
    public boolean isInPat() {
        return isInPat;
    }

    /**
     * @param isInPat the isInPat to set
     */
    public void setIsInPat(String isInPat) {
        this.isInPat = Boolean.parseBoolean(isInPat);
    }
    
    /**
     * @param isInPat the isInPat to set
     */
    public void setIsInPat(boolean isInPat) {
        this.isInPat = isInPat;
    }

    /**
     * @return the isInInstrumentsXsd
     */
    public boolean isInInstrumentsXsd() {
        return isInInstrumentsXsd;
    }

    /**
     * @param isInInstrumentsXsd the isInInstrumentsXsd to set
     */
    public void setInInstrumentsXsd(boolean isInInstrumentsXsd) {
        this.isInInstrumentsXsd = isInInstrumentsXsd;
    }

    /**
     * @return the isInIcs
     */
    public boolean isInIcs() {
        return isInIcs;
    }

    /**
     * @param isInIcs the isInIcs to set
     */
    public void setInIcs(boolean isInIcs) {
        this.isInIcs = isInIcs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InstrumentDescriptor other = (InstrumentDescriptor) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }



    /**
     * The provider of a given instrument
     * 
     * @author MarcoSoldati
     * 
     */
    public static class Provider implements Comparable<Provider> {
        /**
         * The priority of a provider.
         */
        private final int priority;
        
        /**
         * The observatory assigned
         */
        private final String provider;

        /**
         * The archive to use
         */
        private final String archive;

        Provider(String provider, String archive, int priority) {
            this.provider = provider;
            this.archive = archive;
            this.priority = priority;
        }
        
        /**
         * @return the observatory
         */
        public String getProvider() {
            return provider;
        }

        /**
         * Get the archive, if known
         * 
         * @return the archive
         */
        public String getArchive() {
            return archive;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((archive == null) ? 0 : archive.hashCode());
            result = prime * result
                    + ((provider == null) ? 0 : provider.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Provider other = (Provider) obj;
            if (archive == null) {
                if (other.archive != null)
                    return false;
            } else if (!archive.equals(other.archive))
                return false;
            if (provider == null) {
                if (other.provider != null)
                    return false;
            } else if (!provider.equals(other.provider))
                return false;
            return true;
        }

        @Override
        public int compareTo(Provider o) {
            return this.priority - o.priority;
        }
    }
}
