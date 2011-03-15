package eu.heliovo.clientapi.registry;

/**
 * Convenience enum for known ServiceDescriptor that implement then long running query service.
 * @author marco soldati at fhnw ch
 *
 */
public enum LongRunningServiceDescriptor implements HelioServiceDescriptor {

	ASYNC_HEC  ("HEC", "Helio Event Catalog (HEC)", "Helio Event Catalog - Async Query Interface"),
	ASYNC_UOC  ("UOC", "Unified Observation Catalog (UOC)", "Unified Observation Catalog - Async Query Interface"),
	ASYNC_DPAS ("DPAS", "Data Provider Access Service (DPAS)", "Data Provider Access Service (DPAS) - Async Query Interface"),
	ASYNC_ICS  ("ICS", "Instrument Coverage Service (ICS)", "Instrument Coverage Service (ICS) - Async Query Interface"),
	ASYNC_ILS  ("ILS", "Instrument Location Service (ILS)", "Instrument Location Service (ILS) - Async Query Interface"),
	ASYNC_MDES ("MDES", "Metadata Evaluation Service (MDES)", "Metadata Evaluation Service (MDES) - Async Query Interface"),
	;

	private final String name;
	private final String label;
	private final String description;

	/**
	 * Create the LongRunningServiceDescriptor
	 * @param name the name of the service. Must not be null
	 * @param label the label of the service. must not be null.
	 * @param description. The description may be null.
	 */
	private LongRunningServiceDescriptor(String name, String label, String description) {
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
		return HelioServiceType.LONGRUNNING_QUERY_SERVICE;
	}
}
