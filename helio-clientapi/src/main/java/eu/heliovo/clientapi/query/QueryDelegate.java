package eu.heliovo.clientapi.query;

import java.util.List;

import javax.xml.ws.WebServiceException;

import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;

/**
 * Internal delegation object to call the queryXXX method on the SOAP proxy.
 * Additionally the method indicates gives some textual representation of the values.
 * 
 * @author MarcoSoldati
 *
 */
public interface QueryDelegate {
    /**
     * Name of the method executed within {@link #callWebService(AccessInterface, String, long, List)}.
     * @return the method name. for logging only.
     */
    public abstract String getMethodName();
    
    /**
     * Do the actual call to the web service.
     * @param queryService pointer to the current query service bean. The delegate uses the various getter methods from the 
     * service to create the query and then sends it to the backend
     * @param accessInterface the current access interface to use. It's up to the caller to re-call this delegate in case of a failure
     * @return the created result object.
     */
    public HelioQueryResult callWebService(final AbstractQueryServiceImpl queryService, final AccessInterface accessInterface)
            throws WebServiceException, JobExecutionException;
}