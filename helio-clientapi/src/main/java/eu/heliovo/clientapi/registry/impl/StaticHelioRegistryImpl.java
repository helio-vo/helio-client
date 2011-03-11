package eu.heliovo.clientapi.registry.impl;

import java.net.MalformedURLException;
import java.net.URL;

import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.ServiceResolutionException;

public class StaticHelioRegistryImpl implements HelioServiceRegistry {

	@Override
	public URL getHfc() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getHec() throws ServiceResolutionException {
		return asURL("http://140.105.77.30:8080/helio-hec/HelioLongQueryService?wsdl");
	}
	
	@Override
	public URL getUoc() throws ServiceResolutionException {
		return asURL("http://140.105.77.30:8080/helio-uoc/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getDpas() throws ServiceResolutionException {
		return asURL("http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getIcs() throws ServiceResolutionException {
		return asURL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ics/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getIls() throws ServiceResolutionException {
		return asURL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ils/HelioLongQueryService?wsdl");
	}

	@Override
	public URL getCea() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getMdes() throws ServiceResolutionException {
		return asURL("http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService_MDES.wsdl");		
	}

	/**
	 * Convert String to URL. 
	 * @param url the url to convert
	 * @return the url as URL object
	 * @throws ServiceResolutionException if the URL is not valid
	 */
	private URL asURL(String url) throws ServiceResolutionException {
		try {
			return new URL("http://msslxw.mssl.ucl.ac.uk:8080/helio-dpas/HelioLongQueryService?wsdl");
		} catch (MalformedURLException e) {
			throw new ServiceResolutionException("Unable to parse URL: " + e.getMessage(), e);
		}
	}
}
