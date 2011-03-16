package eu.heliovo.clientapi.registry.impl;

import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceType;

/**
 * Convenience enum for known ServiceDescriptor that implement the sync query service.
 * @author marco soldati at fhnw ch
 *
 */
public enum SyncServiceDescriptor implements HelioServiceDescriptor {

	SYNC_HEC  ("HEC", "Helio Event Catalog (HEC)", "Helio Event Catalog - Sync Query Interface"),
	SYNC_UOC  ("UOC", "Unified Observation Catalog (UOC)", "Unified Observation Catalog - Sync Query Interface"),
	SYNC_DPAS ("DPAS", "Data Provider Access Service (DPAS)", "Data Provider Access Service (DPAS) - Sync Query Interface"),
	SYNC_ICS  ("ICS", "Instrument Coverage Service (ICS)", "Instrument Coverage Service (ICS) - Sync Query Interface"),
	SYNC_ILS  ("ILS", "Instrument Location Service (ILS)", "Instrument Location Service (ILS) - Sync Query Interface"),
	SYNC_MDES ("MDES", "Metadata Evaluation Service (MDES)", "Metadata Evaluation Service (MDES) - Sync Query Interface"),
	;

	private final String name;
	private final String label;
	private final String description;

	/**
	 * Create the SyncServiceDescriptor
	 * @param name the name of the service. Must not be null
	 * @param label the label of the service. must not be null.
	 * @param description. The description may be null.
	 */
	private SyncServiceDescriptor(String name, String label, String description) {
		this.name = name;
		this.label = label;
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public HelioServiceType getType() {
		return HelioServiceType.SYNC_QUERY_SERVICE;
	}
}
