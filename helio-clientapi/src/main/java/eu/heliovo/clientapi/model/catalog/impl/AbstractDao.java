package eu.heliovo.clientapi.model.catalog.impl;

import eu.heliovo.clientapi.model.field.FieldTypeRegistry;

/**
 * Abstract base class for Dao implementations
 * @author MarcoSoldati
 *
 */
abstract class AbstractDao {

    /**
     * The field type registry to use
     */
    private FieldTypeRegistry fieldTypeRegistry;

    public AbstractDao() {
        super();
    }

    /**
     * Get the field type registry
     * @return the field type registry
     */
    public FieldTypeRegistry getFieldTypeRegistry() {
        return fieldTypeRegistry;
    }

    /**
     * Set the field type registry
     * @param fieldTypeRegistry
     */
    public void setFieldTypeRegistry(FieldTypeRegistry fieldTypeRegistry) {
        this.fieldTypeRegistry = fieldTypeRegistry;
    }
}