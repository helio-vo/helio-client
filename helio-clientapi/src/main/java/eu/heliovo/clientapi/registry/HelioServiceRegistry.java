package eu.heliovo.clientapi.registry;

import java.net.URL;


public interface HelioServiceRegistry {
	public URL getHfc() throws ServiceResolutionException;

	/**
	 * Get the "best" URL for the HEC.
	 * 
	 * @throws ServiceResolutionException if the service cannot be found.
	 */
	public URL getHec() throws ServiceResolutionException;

	/**
	 * Get the "best" URL for the DPAS.
	 * 
	 * @throws ServiceResolutionException if the service cannot be found.
	 */
	public URL getDpas() throws ServiceResolutionException;

	/**
	 * Get the "best" URL for the ICS.
	 * 
	 * @throws ServiceResolutionException if the service cannot be found.
	 */
	public URL getIcs() throws ServiceResolutionException;

	/**
	 * Get the "best" URL for the ILS.
	 * 
	 * @throws ServiceResolutionException if the service cannot be found.
	 */
	public URL getIls() throws ServiceResolutionException;

	/**
	 * Get the "best" URL for the CEA.
	 * @return URL to the "best" service instance.
	 * @throws ServiceResolutionException if the service cannot be found.
	 */
	public URL getCea() throws ServiceResolutionException;

	/**
	 * Get the "best" URL for the MDES.
	 * @return URL to the "best" service instance.
	 * @throws ServiceResolutionException if the service cannot be found.
	 */
	public URL getMdes() throws ServiceResolutionException;
}
