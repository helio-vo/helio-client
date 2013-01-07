package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.shared.util.AssertUtil;

public class WhereClause {

    /**
     * name of the catalog.
     */
    private String catalogName;
    
    /**
     * initialize with the field descriptors for this where clause
     */
    private final List<HelioFieldDescriptor<?>> fieldDescriptors;
    
    /**
     * Store the field that are currently set
     */
    private final Map<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> fieldMap = 
            new HashMap<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>>();

    /**
     * Create and initalize the where clause.
     * Use the {@link WhereClauseFactoryBeanImpl} to create instances of this class.
     * @param catalogName the name of the catalogue
     * @param fieldDescriptors the corresponding field descriptors.
     */
    WhereClause(String catalogName, List<HelioFieldDescriptor<?>> fieldDescriptors) {
        this.catalogName = catalogName;
        this.fieldDescriptors = Collections.unmodifiableList(fieldDescriptors);
    }

    /**
     * name of the catalog this where clause is assigned to
     * @return
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * The unmodifiable list of descriptors. 
     * @return the read-only list of descriptors
     */
    public List<HelioFieldDescriptor<?>> getFieldDescriptors() {
        return fieldDescriptors;
    }
    
    /**
     * Set the value for a given field descriptor.
     * @param fieldDescriptor the field descriptor, must not be null.
     * @param queryTerms the query terms to set for a given fieldDescriptor. Use null to reset a term. 
     * Any existing terms for this descriptor will be overwritten.
     */
    public void setQueryTerm(HelioFieldDescriptor<?> fieldDescriptor, HelioFieldQueryTerm<?> ...queryTerms) {
        AssertUtil.assertArgumentNotNull(fieldDescriptor, "fieldDescriptor");
        if (supportsFieldDescriptor(fieldDescriptor)) {
            if (queryTerms == null || queryTerms.length == 0) {
                fieldMap.remove(fieldDescriptor);
            } else {
                checkDataConsistency(fieldDescriptor, queryTerms);
                fieldMap.put(fieldDescriptor, Arrays.asList(queryTerms));
            }
        } else {
            throw new IllegalArgumentException("Field '"  + fieldDescriptor.getId() + 
                    "' is not supported by this where clause.");
        }
    }
    
    /**
     * Every query term must reference the given field descriptor.
     * @param fieldDescriptor 
     * @param queryTerms
     */
    private void checkDataConsistency(HelioFieldDescriptor<?> fieldDescriptor, HelioFieldQueryTerm<?>[] queryTerms) {
        for (HelioFieldQueryTerm<?> paramQueryTerm : queryTerms) {
            if (!fieldDescriptor.equals(paramQueryTerm.getHelioFieldDescriptor())) {
                throw new IllegalArgumentException("The given query term does not match the given field descriptor: " + paramQueryTerm + ", " + fieldDescriptor);
            }
        }
    }

    public List<HelioFieldQueryTerm<?>> getQueryTerms() {
        List<HelioFieldQueryTerm<?>> ret = new ArrayList<HelioFieldQueryTerm<?>>();
        for (List<HelioFieldQueryTerm<?>> paramQueryTerms : fieldMap.values()) {
            ret.addAll(paramQueryTerms);
        }
        return ret;
    }
    
    
    /**
     * Find a specific descriptor by its id.
     * @param id the id to look for.
     * @return the descriptor or null if not found.
     */
    public HelioFieldDescriptor<?> findFieldDescriptorById(String id) {
        for (HelioFieldDescriptor<?> helioFieldDescriptor : fieldDescriptors) {
            if (id.equals(helioFieldDescriptor.getId())) {
                return helioFieldDescriptor;
            }
        }
        return null;
    }
    
    /**
     * Check if the given field descriptor is contained in this.fieldDescriptors.
     * @param fieldDescriptor the descriptor to look for. 
     * @return true if found.
     */
    private boolean supportsFieldDescriptor(HelioFieldDescriptor<?> fieldDescriptor) {
        for (HelioFieldDescriptor<?> currentFieldDescriptor : this.fieldDescriptors) {
            if (currentFieldDescriptor.equals(fieldDescriptor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((catalogName == null) ? 0 : catalogName.hashCode());
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
        WhereClause other = (WhereClause) obj;
        if (catalogName == null) {
            if (other.catalogName != null)
                return false;
        } else if (!catalogName.equals(other.catalogName))
            return false;
        return true;
    }
}
