package eu.heliovo.mockclient.registry;

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
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getDpas() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getIcs() throws ServiceResolutionException {
		try {
			return new URL("http://msslxw.mssl.ucl.ac.uk:8080/helio-ics-longrunning/HelioLongQueryService?wsdl");
		} catch (MalformedURLException e) {
			throw new ServiceResolutionException("Unable to parse URL: " + e.getMessage(), e);
		}
	}

	@Override
	public URL getIls() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getCea() throws ServiceResolutionException {
		throw new ServiceResolutionException("No service available yet");
	}

	@Override
	public URL getMdes() throws ServiceResolutionException {
		try {
			return new URL("http://manunja.cesr.fr/Amda-Helio/WebServices/HelioLongQueryService_MDES.wsdl");
		} catch (MalformedURLException e) {
			throw new ServiceResolutionException("Unable to parse URL: " + e.getMessage(), e);
		}
		
	}

	
}
