package eu.heliovo.clientapi.query.paramquery.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.catalog.HelioCatalog;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.AsyncQueryService;
import eu.heliovo.clientapi.query.paramquery.ParamQueryService;
import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.util.AssertUtil;
/**
 * Abstract base class for the implementations of the param query service 
 * @author marco soldati at fhnw ch 
 */
abstract class AbstractParamQueryServiceImpl implements ParamQueryService {
	
	/**
	 * Hold the conversion service for data type conversion.
	 */
	protected ConversionService conversionService;
	
	/**
	 * Service to access a remote resource.
	 */
	protected final AsyncQueryService queryService;
	
	/**
	 * Create the param query impl and assign a query Service.
	 * @param queryService instance of the query service to use.
	 */
	public AbstractParamQueryServiceImpl(AsyncQueryService queryService) {
		this.queryService = queryService;
	}
	
    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.UNDEFINED;
    }

	@Override
	public HelioQueryResult query(final List<ParamQueryTerm<?>> terms) throws JobExecutionException {
		AssertUtil.assertArgumentNotNull(terms, "terms");
		
		TermList termList = new TermList(terms);
		
		String where = getWhere(termList.whereTerms);
		
		HelioQueryResult result = queryService.query(termList.starttime, termList.endtime, termList.catalogs, where, termList.maxrecords, termList.startindex, termList.join, termList.saveto);
		return result;
	}
	
	/**
	 * Helper class to handle the terms
	 * @author marco soldati at fhnw ch
	 *
	 */
	private class TermList {

		/**
		 * list of catalogs
		 */
		private List<String> catalogs;

		/**
		 * List of start times
		 */
		private List<String> starttime;

		/**
		 * List of end times
		 */
		private List<String> endtime;

		/**
		 *max records
		 */
		private Integer maxrecords;

		/**
		 *start index
		 */
		private Integer startindex;

		/**
		 * Join table
		 */
		public String join;

		/**
		 * Name of the saved votable
		 */
		private String saveto;
		
		/**
		 * List terms to be used for the where clause
		 */
		private final List<ParamQueryTerm<?>> whereTerms = new ArrayList<ParamQueryTerm<?>>();

		/**
		 * Create the term list and populate the known fields of this object.
		 * @param terms the terms to analyze.
		 */
		public TermList(List<ParamQueryTerm<?>> terms) {
			for (final ParamQueryTerm<?> term : terms) {
				HelioField<?> field = term.getHelioField();
				if (field.getId().equals(HelioCatalog.CATALOG_FIELD)) {
					catalogs = Arrays.asList(getAs(term, String[].class));
				} else if (field.getId().equals(HelioField.FIELD_STARTTIME)) {					
					starttime = Arrays.asList(getAs(term, String[].class));
				} else if (field.getId().equals(HelioField.FIELD_ENDTIME)) {
					endtime = Arrays.asList(getAs(term, String[].class));
				} else if (field.getId().equals(HelioField.FIELD_MAX_RECORDS)) {
					maxrecords = getAs(term, Integer.class);
				} else if (field.getId().equals(HelioField.FIELD_STARTINDEX)) {
					startindex = getAs(term, Integer.class);
				} else if (field.getId().equals(HelioField.FIELD_SAVE_TO)) {					
					saveto = getAs(term, String.class);
				} else {
					whereTerms.add(term);
				}
			}
			
			// check if mandatory fields are set.
			AssertUtil.assertArgumentNotEmpty(catalogs, HelioCatalog.CATALOG_FIELD);
			AssertUtil.assertArgumentNotEmpty(starttime, HelioCatalog.CATALOG_FIELD);
			AssertUtil.assertArgumentNotEmpty(endtime, HelioCatalog.CATALOG_FIELD);
		}
	}	
	
	/**
	 * Get object from map and convert to an appropriate type if possible.
	 * This method only works for binary operator types.
	 * Simple types will be converted to arrays if possible.
	 * @param terms the map to check
	 * @param key the key to search for
	 * @param type the excepted type
	 * @return the converted value. 
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getAs(ParamQueryTerm<?> term, Class<T> type) {
		final T[] values = (T[]) term.getArguments();		
		if (values == null) {
			throw new IllegalArgumentException("Attribute values of term " + term + " must not be null");
		}
		
		// convert to array if single value
		if (type.isArray() && !values[0].getClass().isArray()) {
			Object array = Array.newInstance(type.getComponentType(), 1);
			Array.set(array, 0, conversionService.convert(values[0], type.getComponentType()));
			return (T) array;
		}
		return conversionService.convert(values[0], type);
	}
	
	/**
	 * Create the where clause of the query.
	 * @param whereTerms the parameter map to be used for the where clause.
	 * @return the where clause in PQL.
	 */
	private String getWhere(List<ParamQueryTerm<?>> whereTerms) {
		StringBuilder sb = new StringBuilder("");
		
		return sb.toString();
	}

	/**
	 * Create a LogRecord for a user message at a given time.
	 * @param level the log level. May be null.
	 * @param message the message. May be null.
	 * @param e any throwable. May be null.
	 * @return
	 */
	protected static LogRecord getMessage(Level level, String message,
			Throwable e) {
		LogRecord msg = new LogRecord(level, message);
		msg.setThrown(e);
		return msg;
	}

	/**
	 * Get the conversion service
	 * @return the conversion service
	 */
	public ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Set the conversion service.
	 * @param conversionService
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
