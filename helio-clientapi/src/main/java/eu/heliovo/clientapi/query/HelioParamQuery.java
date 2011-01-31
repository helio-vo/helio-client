package eu.heliovo.clientapi.query;

import java.util.Map;

import eu.heliovo.clientapi.model.infrastructure.HelioService;
import eu.heliovo.clientapi.result.HelioQueryResult;

/**
 * Methods to execute parameterized queries on the Helio system. 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioParamQuery extends HelioService {

	/**
	 * Execute a parameterized query on a specific service implementation.
	 * A set of well defined parameters is used to issue a specific query on the HELIO system.
	 * @param params the parameters to use. 
	 * @return the result as an URI to load the data from remote.
	 */
	public HelioQueryResult query(Map<String, ? extends Object> params);	
	
	/**
	 * Get the description of the allowed parameters in a specific context.
	 * The context is  
	 * @param context the context in which the  
	 * @return
	 */
	 public HelioParameter<?>[] getParameterDescription(Map<String, ? extends Object> context);
}
