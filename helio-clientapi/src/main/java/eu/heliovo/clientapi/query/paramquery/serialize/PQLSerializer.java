package eu.heliovo.clientapi.query.paramquery.serialize;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;

/**
 * convert a list of given param query terms to PQL.
 * @author marco soldati at fhnw ch
 *
 */
public class PQLSerializer implements QuerySerializer {
	/**
	 * Main template.
	 */
	private static final String ASSIGN_TEMPLATE = "%1$s.%2$s,%3$s";
	
	/**
	 * template for =
	 */
	private static final String EQUAL_TEMPLATE = "%1$s";
	
	/**
	 * template for &lt;=
	 */
	private static final String LESS_EQUAL_THAN_TEMPLATE = "/%1$s";
	
	/**
	 * template for &gt;=
	 */
	private static final String GREATER_EQUAL_THAN_TEMPLATE = "%1$s/";
	
	/**
	 * Template for LIKE constructs
	 */
	private static final String LIKE_TEMPLATE = "*%1$s*";

	/**
	 * Template for between constructs
	 */
	private static final String BETWEEN_TEMPLATE = "%1$s/%2$s";
	
	/**
	 * Symbol for OR
	 */
	private static final String LIST_SEPARATOR = ",";
		
	/**
	 * Separator between two parameters
	 */
	private static final String FIELD_SEPARATOR = ";";
	
    /**
     * Hold the conversion service for data type conversion.
     */
    protected ConversionService conversionService;
    

	@Override
	public String getWhereClause(String catalogueName, List<HelioFieldQueryTerm<?>> paramQueryTerms) throws QuerySerializationException {
		List<QuerySerializationException> exceptions = new ArrayList<QuerySerializationException>();
		
		StringBuilder sb = new StringBuilder();
		
		Map<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> groupedTerms = groupTerms(paramQueryTerms);
		
		// iterate over the grouped terms.
		for (Map.Entry<HelioFieldDescriptor<?>, List<HelioFieldQueryTerm<?>>> termGroup : groupedTerms.entrySet()) {
		    if (sb.length() > 0) {
		        sb.append(FIELD_SEPARATOR);
		    }
		    
		    // build the right side of a query.
		    StringBuilder rightSide = new StringBuilder();
		    String paramName = termGroup.getKey().getName();
		    
		    // iterate over the terms
		    for (HelioFieldQueryTerm<?> term : termGroup.getValue()) {
		        try {
		            if (rightSide.length() > 0) {
		                rightSide.append(LIST_SEPARATOR);
		            }
		            
		            String template = getTemplate(term);
		            
		            switch (term.getOperator().getArity()) {
		            case 1:
		                rightSide.append(handleUnaryTerm(paramName, template));
		            case 2:
		                rightSide.append(handleBinaryTerm(template, term.getArguments()[0]));
		                break;
		            case 3:
		                rightSide.append(handleTernaryTerm(template, term.getArguments()[0], term.getArguments()[1]));
		                break;
		            default:
		                break;
		            }
		        } catch (QuerySerializationException e) {
		            exceptions.add(e);
		            // continue, but throw exception at a later stage
		        }
		    }
		    sb.append(String.format(ASSIGN_TEMPLATE, catalogueName, paramName, rightSide.toString()));
		    
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
	 * Get the template according to the selected operator.
	 * @param term the query term.
	 * @return the template.
	 */
	private String getTemplate(HelioFieldQueryTerm<?> term) throws QuerySerializationException {
		Operator operator = term.getOperator();
		switch (operator) {
		case EQUALS:
			return EQUAL_TEMPLATE;
		case LESS_EQUAL_THAN:
			return LESS_EQUAL_THAN_TEMPLATE;
		case LARGER_EQUAL_THAN:
			return GREATER_EQUAL_THAN_TEMPLATE;
		case BETWEEN:
			return BETWEEN_TEMPLATE;
		case LIKE:
			return LIKE_TEMPLATE;
		default:
			throw new QuerySerializationException("Unsupported operator (" + operator + ") in term " + term.toString());
		}
	}
	
	/**
	 * Generate a PQL term of an unary operator
	 * @param paramName the name of the parameter (left side)
	 * @param template the template to use.
	 * @return String that applies the unary operator to the paramName.
	 */
	private String handleUnaryTerm(String paramName, String template) throws QuerySerializationException{
		throw new QuerySerializationException("Unary operators are not supported by PQL.");
	}

	/**
	 * Generate a PQL term for a binary operator.
 	 * @param template the template to apply to the arguments
	 * @param arg the argument to fill into the template. 
	 * @return a string with the right side of a query term.
	 */
	private String handleBinaryTerm(String template, Object arg)  throws QuerySerializationException {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(template, convertToString(arg)));
		return sb.toString();
	}
	
	/**
	 * Generate a PQL term for a ternary operator.
	 * @param template the template to apply to the arguments
	 * @param arg1 first argument.
	 * @param arg2 second argument.
	 * @return a string with the right side of a query term.
	 */
	private String handleTernaryTerm(String template, Object arg1, Object arg2)  throws QuerySerializationException {
		StringBuilder value = new StringBuilder();
		value.append(String.format(template, convertToString(arg1), convertToString(arg2)));
		return value.toString();
	}
	
	/**
	 * Convert a value to a string and encode as URL part.
	 * @param value the value to convert. Will be converted to a string.
	 * @return the encoded value.
	 */
	private String convertToString(Object value) {
	    String stringValue =  conversionService.convert(value, String.class);
	    
		StringBuilder uri = new StringBuilder(); // Encoded URL

		for(int i = 0; i < stringValue.length(); i++) {
			char c = stringValue.charAt(i);
			if((c >= '0' && c <= '9') || 
			   (c >= 'a' && c <= 'z') ||
			   (c >= 'A' && c <= 'Z') ||
			   (c == '-') ||
			   (c == '_') ||
			   (c == '.') ||
			   (c == '!') ||
			   (c == '~') ||
			   (c == '*') ||
			   (c == '\'') ||
			   (c == '(') ||
			   (c == ')') ||
			   (c == '\"'))  {
				uri.append(c);
			} else {
				uri.append("%");
				uri.append(Integer.toHexString((int)c));
			}
		}
		return uri.toString();
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