package eu.heliovo.clientapi.registry;

import java.net.URL;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Descriptor of a concrete instance of a service.
 * @author MarcoSoldati
 *
 */
public class HelioServiceInstanceDescriptor {
	/**
	 * The assigned service descriptor
	 */
	private final HelioServiceDescriptor serviceDescriptor;
	
	/**
	 * Pointer to the WSDL file
	 */
	private final URL wsdlFile;
	
	public HelioServiceInstanceDescriptor(HelioServiceDescriptor serviceDescriptor, URL wsdlFile) {
		AssertUtil.assertArgumentNotNull(serviceDescriptor, "serviceDescriptor");
		AssertUtil.assertArgumentNotNull(wsdlFile, "wsdlFile");
		this.serviceDescriptor = serviceDescriptor;
		this.wsdlFile = wsdlFile;
	}

	/**
	 * Get the descriptor of the service.
	 * @return the service descriptor.
	 */
	public HelioServiceDescriptor getServiceDescriptor() {
		return serviceDescriptor;
	}
	
	/**
	 * Get a pointer to the WSDL file
	 * @return the wsdl file
	 */
	public URL getWsdlFile() {
		return wsdlFile;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof HelioServiceInstanceDescriptor)) {
			return false;
		}
		
		return serviceDescriptor.equals(((HelioServiceInstanceDescriptor)other).serviceDescriptor) &&
			wsdlFile.equals(((HelioServiceInstanceDescriptor)other).wsdlFile);
		
	}
	
	@Override
	public int hashCode() {
		return wsdlFile.hashCode() *13 + serviceDescriptor.hashCode() *11;
	}
}
