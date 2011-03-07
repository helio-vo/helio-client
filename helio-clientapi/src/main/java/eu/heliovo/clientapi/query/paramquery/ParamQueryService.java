package eu.heliovo.clientapi.query.paramquery;

import java.util.List;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.paramquery.expression.ParamQueryTerm;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Methods to execute parameterized queries on the Helio system. 
 * @author marco soldati at fhnw ch
 *
 */
public interface ParamQueryService extends HelioService {	
	/**
	 * Execute a parameterized query.
	 * @param terms the terms used for the query. These terms will be transformed into a PQL query. 
	 * @return the result as a list of HelioQueryResults. Depending on the input terms multiple queries may
	 * be executed by the remote system. Each query result can be retrieved independently by the according {@link HelioQueryResult}.
	 * @throws IllegalArgumentException if the terms cannot be validated.
	 * @throws JobExecutionException if the execution of the query fails for some reason.
	 */
	public HelioQueryResult query(List<ParamQueryTerm<?>> terms) throws IllegalArgumentException, JobExecutionException;
	
	/**
	 * Get the description of all fields of a given catalog.
	 * If catalog is null a list of catalogs will be returned.   
	 * @param catalog the catalog to get fields for. If null the names of the allowed catalogs will be returned.
	 * @return the allowed fields for a specific catalog or the catalog definition field.
	 * @throws IllegalArgumentException if the catalog does not exist.
	 */
	 public HelioField<?>[] getFieldDescriptions(String catalog) throws IllegalArgumentException;
}
