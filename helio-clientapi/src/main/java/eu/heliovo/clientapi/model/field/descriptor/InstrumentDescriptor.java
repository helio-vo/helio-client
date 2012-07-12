package eu.heliovo.clientapi.model.field.descriptor;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.heliovo.clientapi.model.field.DomainValueDescriptor;

/**
 * Describe an instrument.
 * 
 * @author MarcoSoldati
 * 
 */
public class InstrumentDescriptor implements DomainValueDescriptor<String> {
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
