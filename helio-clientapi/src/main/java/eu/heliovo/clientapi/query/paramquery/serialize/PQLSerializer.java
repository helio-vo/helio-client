package eu.heliovo.clientapi.query.paramquery.serialize;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;

/**
 * convert a list of given param query terms to PQL.
 * @author marco soldati at fhnw ch
 *
 */
public class PQLSerializer implements QuerySerializer {
	/**
	 * Main template.
	 */
	private static final String ASSIGN_TEMPLATE = "%1$s=%2$s";
	
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
	private static final String OR_SYMBOL = ",";
	
	/**
	 * Symbol for AND.
	 */
	@SuppressWarnings("unused")
	private static final String AND_SYMBOL = ";";
	
	/**
	 * Separator between two parameters
	 */
	private static final String PARAMETER_SEPARATOR = "&";
	
    /**
     * Hold the conversion service for data type conversion.
     */
    protected ConversionService conversionService;
    

	@Override
	public String getWhereClause(List<ParamQueryTerm<?>> paramQueryTerms) throws QuerySerializationException {
		List<QuerySerializationException> exceptions = new ArrayList<QuerySerializationException>();
		
		StringBuilder sb = new StringBuilder();
		
		// iterate over the terms
		for (ParamQueryTerm<?> term : paramQueryTerms) {
			try {
				if (sb.length() > 0) {
					sb.append(PARAMETER_SEPARATOR);
				}
				
				String paramName = term.getHelioField().getName();
				String template = getTemplate(term);
				
				switch (term.getOperator().getArity()) {
				case 1:
					sb.append(handleUnaryTerm(paramName, template));
				case 2:
					sb.append(handleBinaryTerm(paramName, template, term.getArguments()[0]));
					break;
				case 3:
					sb.append(handleTernaryTerm(paramName, template, term.getArguments()[0], term.getArguments()[1]));
					break;
				default:
					break;
				}
			} catch (QuerySerializationException e) {
				exceptions.add(e);
				// continue, but throw exception at a later stage
			}
		}
		
		if (exceptions.size() == 1) {
			throw exceptions.get(0);
		} else if (exceptions.size() > 1) {
			throw new MultiQuerySerializationException(exceptions);
		}
		return sb.toString();
	}

	/**
	 * Get the template according to the selected operator.
	 * @param term the query term.
	 * @return the template.
	 */
	private String getTemplate(ParamQueryTerm<?> term) throws QuerySerializationException {
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
		//return String.format(template, paramName);
	}

	/**
	 * Generate a PQL term for a binary operator.
	 * @param paramName name of the parameter (left side of =)
 	 * @param template the template to apply to the arguments
	 * @param args the arguments to fill into the template. arguments will be or connected. 
	 * @return a string with the param name on the left side and the arguments as OR connected list.
	 */
	private String handleBinaryTerm(String paramName, String template, Object args)  throws QuerySerializationException {
		StringBuilder sb = new StringBuilder();
		if (args.getClass().isArray()) {
    		for (int i = 0; i < Array.getLength(args); i++) {
    			if (i > 0) { 
    				// prepend OR
    				sb.append(OR_SYMBOL);
    			}
    			sb.append(String.format(template, convertToString(Array.get(args, i))));
    		}
		} else {
		    sb.append(String.format(template, convertToString(args)));
		}
		return String.format(ASSIGN_TEMPLATE, paramName, sb.toString());
	}
	
	/**
	 * Generate a PQL term for a ternary operator.
	 * @param paramName the name of the parater (left side of =)
	 * @param template the template to apply to the arguments
	 * @param args1 first argument. May be an object or an object array.
	 * @param args2 second argument. May be an object or an object array.
	 * @return a string with the param name on the left side and the arguments as or connected list.
	 */
	private String handleTernaryTerm(String paramName, String template, Object args1, Object args2)  throws QuerySerializationException {
		Object[] argArray1 = args1 instanceof Object[] ? (Object[])args1 : new Object[] {args1};
		Object[] argArray2 = args2 instanceof Object[] ? (Object[])args2 : new Object[] {args2};

		if (argArray1.length != argArray2.length) {
			throw new IllegalArgumentException("Internal error: arrays must have the same size: " + argArray1.length + "!=" + argArray2.length);
		}
		
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < argArray1.length; i++) {
			if (i > 0) {
				// prepend OR
				value.append(OR_SYMBOL);
			}
			value.append(String.format(template, convertToString(argArray1[i]), convertToString(argArray2[i])));
		}
		return String.format(ASSIGN_TEMPLATE, paramName, value.toString());
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