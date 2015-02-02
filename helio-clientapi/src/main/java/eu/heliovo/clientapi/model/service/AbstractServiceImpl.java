package eu.heliovo.clientapi.model.service;

import java.util.HashSet;
import java.util.Set;

import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

public abstract class AbstractServiceImpl implements HelioService {

	/**
	 * Name of the service
	 */
	private HelioServiceName serviceName;
	
	/**
	 * The optional variant
	 */
	private String serviceVariant;

	private final Set<ServiceCapability> capabilities = new HashSet<ServiceCapability>();

	public AbstractServiceImpl() {
		super();
	}

	@Override
	public final HelioServiceName getServiceName() {
	    return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public final void setServiceName(HelioServiceName serviceName) {
	    this.serviceName = serviceName;
	}

	@Override
	public final String getServiceVariant() {
	    return serviceVariant;
	}

	/**
	 * @param serviceVariant
	 *            the serviceVariant to set
	 */
	public final void setServiceVariant(String serviceVariant) {
	    this.serviceVariant = serviceVariant;
	}
	
	@Override
	public final boolean supportsCapability(ServiceCapability capability) {
		return capabilities.contains(capability);
	}

	protected void setCapabilites(ServiceCapability ... capabilities) {
		for (ServiceCapability serviceCapability : capabilities) {
			this.capabilities.add(serviceCapability);
		}
	}
	
}