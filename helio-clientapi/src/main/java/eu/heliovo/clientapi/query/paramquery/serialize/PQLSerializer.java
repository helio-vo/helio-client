package eu.heliovo.clientapi.query.paramquery.serialize;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;

/**
 * convert a list of given param query terms to PQL.
 * @author marco soldati at fhnw ch
 *
 */
public class PQLSerializer extends AbstractQuerySerializer {
	/**
	 * Main template.
	 */
	private static final String ASSIGN_TEMPLATE = "%1$s,%2$s";
	
	/**
	 * template for =
	 */
	private static final String EQUAL_TEMPLATE = "%2$s";
	
	/**
	 * template for &lt;=
	 */
	private static final String LESS_EQUAL_THAN_TEMPLATE = "/%2$s";
	
	/**
	 * template for &gt;=
	 */
	private static final String GREATER_EQUAL_THAN_TEMPLATE = "%2$s/";
	
	/**
	 * Template for LIKE constructs
	 */
	private static final String LIKE_TEMPLATE = "*%2$s*";

	/**
	 * Template for between constructs
	 */
	private static final String BETWEEN_TEMPLATE = "%2$s/%3$s";
	
	/**
	 * Symbol for OR
	 */
	private static final String LIST_SEPARATOR = ",";
		
	/**
	 * Separator between two parameters
	 */
	private static final String FIELD_SEPARATOR = ";";
    
    /**
	 * Get the template according to the selected operator.
	 * @param term the query term.
	 * @return the template.
	 */
	protected String getTemplate(HelioFieldQueryTerm<?> term) throws QuerySerializationException {
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
	
	@Override
	protected void handleListTerm(StringBuilder leftSide, String rightSide) {
        if (leftSide.length() > 0) {
            leftSide.append(LIST_SEPARATOR);
        }
        if (rightSide != null) {
        	leftSide.append(rightSide);
        }
	}
	
	@Override
	protected String getTermSeparator() {
		return FIELD_SEPARATOR;
	}
	
	@Override
	protected String handleFullTerm(String paramName, String rightSide) {
		return String.format(ASSIGN_TEMPLATE, paramName, rightSide);
	}
	
	/**
	 * Generate a PQL term of an unary operator
	 * @param paramName the name of the parameter (left side)
	 * @param template the template to use.
	 * @return String that applies the unary operator to the paramName.
	 */
	protected String handleUnaryTerm(String paramName, String template) throws QuerySerializationException{
		throw new QuerySerializationException("Unary operators are not supported by PQL.");
	}
	
	/**
	 * Convert a value to a string and encode as URL part.
	 * @param value the value to convert. Will be converted to a string.
	 * @return the encoded value.
	 */
	protected String convertToString(Object value) {
	    String stringValue = super.convertToString(value);
	    
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
}