package eu.heliovo.clientapi.model.service;

import eu.heliovo.registryclient.HelioServiceName;

public abstract class AbstractServiceImpl implements HelioService {

	/**
	 * Name of the service
	 */
	private HelioServiceName serviceName;
	
	/**
	 * The optional variant
	 */
	private String serviceVariant;

	public AbstractServiceImpl() {
		super();
	}

	@Override
	public HelioServiceName getServiceName() {
	    return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(HelioServiceName serviceName) {
	    this.serviceName = serviceName;
	}

	@Override
	public String getServiceVariant() {
	    return serviceVariant;
	}

	/**
	 * @param serviceVariant
	 *            the serviceVariant to set
	 */
	public void setServiceVariant(String serviceVariant) {
	    this.serviceVariant = serviceVariant;
	}

}