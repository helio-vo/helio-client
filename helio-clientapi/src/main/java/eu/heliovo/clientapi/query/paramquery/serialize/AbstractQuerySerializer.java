package eu.heliovo.clientapi.query.paramquery.serialize;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;

abstract class AbstractQuerySerializer implements QuerySerializer {

	/**
	 * Hold the conversion service for data type conversion.
	 */
	protected ConversionService conversionService;

	protected abstract String getTemplate(HelioFieldQueryTerm<?> term) throws QuerySerializationException;

	public AbstractQuerySerializer() {
		super();
	}

	@Override
	public String getWhereClause(String catalogueName, List<HelioFieldQueryTerm<?>> paramQueryTerms) throws QuerySerializationException {
		List<QuerySerializationException> exceptions = new ArrayList<QuerySerializationException>();
		
		StringBuilder sb = new StringBuilder();
		
		Map<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> groupedTerms = groupTerms(paramQueryTerms);
		
		// iterate over the grouped terms.
		for (Map.Entry<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> termGroup : groupedTerms.entrySet()) {
		    if (sb.length() > 0) {
		        sb.append(getTermSeparator());
		    }
		    
		    // handle a term that may contain a list of values.
		    StringBuilder leftSide = new StringBuilder();
		    String paramName = catalogueName + "." + termGroup.getKey().getName();
		    
		    // iterate over the terms
		    for (HelioFieldQueryTerm<?> term : termGroup.getValue()) {
		        try {
		            String template = getTemplate(term);
		            String rightSide = null;
		            switch (term.getOperator().getArity()) {
		            case 1:
		                rightSide = handleUnaryTerm(paramName, template);
		            case 2:
		                rightSide = handleBinaryTerm(template, paramName, term.getArguments()[0]);
		                break;
		            case 3:
		                rightSide = handleTernaryTerm(template, paramName, term.getArguments()[0], term.getArguments()[1]);
		                break;
		            default:
		                break;
		            }
		            handleListTerm(leftSide, rightSide);
		        } catch (QuerySerializationException e) {
		            exceptions.add(e);
		            // continue, but throw exception at a later stage
		        }
		    }
		    sb.append(handleFullTerm(paramName, leftSide.toString()));
		    
        }
		
		if (exceptions.size() == 1) {
			throw exceptions.get(0);
		} else if (exceptions.size() > 1) {
			throw new MultiQuerySerializationException(exceptions);
		}
		return sb.toString();
	}

	/**
	 * Group the terms with the same field id
	 * @param paramQueryTerms the terms to group
	 * @return a map containing one entry per term with all set values.
	 */
    private Map<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> groupTerms(List<HelioFieldQueryTerm<?>> paramQueryTerms) {
        Map<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> ret = new LinkedHashMap<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>>();
        
        for (HelioFieldQueryTerm<?> paramQueryTerm : paramQueryTerms) {
            List<HelioFieldQueryTerm<?>> args = ret.get(paramQueryTerm.getHelioFieldDescriptor());
            if (args == null) {
                args = new ArrayList<HelioFieldQueryTerm<?>>();
                ret.put(paramQueryTerm.getHelioFieldDescriptor(), args);
            }
            args.add(paramQueryTerm);
        }
        return ret;
    }

	/**
	 * Get the separator between two terms
	 * @return the separator between terms.
	 */
	protected abstract String getTermSeparator();
	
	
	/**
	 * Append the right side to the left side and set brackets if needed.
	 * @param leftSide the current left side, may be of size 0.
	 * @param rightSide the current right side, may be null.
	 */
	protected abstract void handleListTerm(StringBuilder leftSide, String rightSide);
	
	/**
	 * Format one single term
	 * @param paramName the name of the current param including the catalogueName.
	 * @param rightSide 
	 * @return
	 */
	protected abstract String handleFullTerm(String paramName, String rightSide);

	/**
	 * Generate a SQL term of an unary operator
	 * @param paramName the name of the parameter (left side), including catalog name
	 * @param template the template to use.
	 * @return String that applies the unary operator to the paramName.
	 */
	protected String handleUnaryTerm(String template, String paramName) throws QuerySerializationException {
		return String.format(template, paramName);
	}

	/**
	 * Generate a SQL term for a binary operator.
	 * @param template the template to apply to the arguments
	 * @param paramName the name of the parameter (left side), including catalog name
	 * @param arg the argument to fill into the template. 
	 * @param arg 
	 * @return a string with the right side of a query term.
	 */
	protected String handleBinaryTerm(String template, String paramName, Object arg) throws QuerySerializationException {
		return String.format(template, paramName, convertToString(arg));
	}

	/**
	 * Generate a SQL term for a ternary operator.
	 * @param template the template to apply to the arguments
	 * @param paramName the name of the parameter (left side), including catalog name
	 * @param arg1 first argument.
	 * @param arg2 second argument.
	 * @return a string with the right side of a query term.
	 */
	protected String handleTernaryTerm(String template, String paramName, Object arg1, Object arg2)
	throws QuerySerializationException {
		return String.format(template, paramName, convertToString(arg1), convertToString(arg2));
	}

	/**
	 * Convert a value to a string.
	 * @param value the value to convert. 
	 * @return the string value.
	 */
	protected String convertToString(Object value) {
	    String stringValue = conversionService.convert(value, String.class);
	    return stringValue;
	}

	/**
	 * Get the conversion service to convert object from one type to another.
	 * @return the conversion service
	 */
	public ConversionService getConversionService() {
	    return conversionService;
	}

	/**
	 * Set the conversion service to convert objects from one type to another.
	 * @param conversionService the conversion service
	 */
	public void setConversionService(ConversionService conversionService) {
	    this.conversionService = conversionService;
	}

}