package eu.heliovo.clientapi.query.paramquery.serialize;



import java.util.Date;

import eu.heliovo.clientapi.model.field.HelioFieldQueryTerm;
import eu.heliovo.clientapi.model.field.Operator;

/**
 * convert a list of given param query terms to SQL.
 * @author marco soldati at fhnw ch
 *
 */
public class SQLSerializer extends AbstractQuerySerializer {
	/**
	 * NOT operator
	 */
	private static final String NOT_TEMPLATE = "NOT %1$s";	
	
	/**
	 * template for =
	 */
	private static final String EQUAL_TEMPLATE = "%1$s=%2$s";
	
	/**
	 * template for =
	 */
	private static final String QUOTED_EQUAL_TEMPLATE = "%1$s='%2$s'";
	
	/**
	 * template for &lt;=
	 */
	private static final String LESS_EQUAL_THAN_TEMPLATE = "%1$s <= %2$s";

	/**
	 * template for &lt;=
	 */
	private static final String QUOTED_LESS_EQUAL_THAN_TEMPLATE = "%1$s <= '%2$s'";
	
	/**
	 * template for &gt;=
	 */
	private static final String GREATER_EQUAL_THAN_TEMPLATE = "%1$s >= %2$s";
	
	/**
	 * template for &gt;=
	 */
	private static final String QUOTED_GREATER_EQUAL_THAN_TEMPLATE = "%1$s >= '%2$s'";
	
	/**
	 * Template for LIKE constructs
	 */
	private static final String QUOTED_LIKE_TEMPLATE = "%1$s LIKE '%%%2$s%%'";

	/**
	 * Template for between constructs
	 */
	private static final String BETWEEN_TEMPLATE = "%1$s BETWEEN %2$s AND %3$s";
	
	/**
	 * Template for between constructs
	 */
	private static final String QUOTED_BETWEEN_TEMPLATE = "%1$s BETWEEN '%2$s' AND '%3$s'";
	
	/**
	 * AND
	 */
	static final String AND = " AND ";
	
	/**
	 * OR
	 */
	static final String OR = " OR ";

    /**
	 * Get the template according to the selected operator.
	 * @param term the query term.
	 * @return the template.
	 */
	@Override
	protected String getTemplate(HelioFieldQueryTerm<?> term) throws QuerySerializationException {
		Operator operator = term.getOperator();
		switch (operator) {
		case NOT: 
			return NOT_TEMPLATE;
		case EQUALS:
			return needsQuote(term.getArguments()[0]) ? QUOTED_EQUAL_TEMPLATE : EQUAL_TEMPLATE;
		case LESS_EQUAL_THAN:
			return needsQuote(term.getArguments()[0]) ? QUOTED_LESS_EQUAL_THAN_TEMPLATE : LESS_EQUAL_THAN_TEMPLATE;
		case LARGER_EQUAL_THAN:
			return needsQuote(term.getArguments()[0]) ? QUOTED_GREATER_EQUAL_THAN_TEMPLATE : GREATER_EQUAL_THAN_TEMPLATE;
		case BETWEEN:
			return needsQuote(term.getArguments()[0]) ? QUOTED_BETWEEN_TEMPLATE : BETWEEN_TEMPLATE;
		case LIKE:
			return QUOTED_LIKE_TEMPLATE;
		default:
			throw new QuerySerializationException("Unsupported operator (" + operator + ") in term " + term.toString());
		}
	}
	
	private boolean needsQuote(Object value) {
		if (value instanceof String || value instanceof Date) {
			return true;
		}
		return false;
	}

	@Override
	protected String handleFullTerm(String paramName, String term) {
		return term;
	}
	
	@Override
	protected String getTermSeparator() {
		return AND;
	}
	
	@Override
	protected void handleListTerm(StringBuilder leftSide, String rightSide) {
        if (leftSide.length() > 0) {
        	leftSide.insert(0, '(');
            leftSide.append(OR).append(rightSide);
            leftSide.append(')');
        } else {
        	if (rightSide != null) {
        		leftSide.append(rightSide);
        	}
        } 
	}
}