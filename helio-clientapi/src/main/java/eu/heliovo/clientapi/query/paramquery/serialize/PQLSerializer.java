package eu.heliovo.clientapi.query.paramquery.serialize;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;

/**
 * convert a list of given param query terms to PQL.
 * @author marco soldati at fhnw ch
 *
 */
public class PQLSerializer implements QuerySerializer {
	
	@Override
	public String getWhereClause(List<ParamQueryTerm<?>> paramQueryTerms) throws QuerySerializationException {
		List<QuerySerializationException> exceptions = new ArrayList<QuerySerializationException>();
		
		StringBuilder sb = new StringBuilder();
		
		// iterate over the terms
		for (ParamQueryTerm<?> term : paramQueryTerms) {
			try {
				switch (term.getOperator()) {
				case EQUALS:
					handleEquals(sb, term);
					break;
				case LESS_THAN:
				case LESS_EQUAL_THAN:
				case LARGER_THAN:
				case LARGER_EQUAL_THAN:
					handleUnequal(sb, term);
					break;
				case BETWEEN:
					
				default:
					throw new QuerySerializationException("Unsupported operator (" + term.getOperator() + ") in term " + term);
				}
				
				// handle the field
				term.getHelioField();
				
				// handle the operator
				term.getOperator();
				
				// handle the arguments
				term.getArguments();
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
	 * Handle the equals
	 * @param sb
	 * @param term
	 */
	private void handleEquals(StringBuilder sb, ParamQueryTerm<?> term) {
		sb.append(term.getHelioField().getName());
		sb.append(",");
		Object[] args=term.getArguments();
		// sanity check
		if (args.length != 1) {
			throw new RuntimeException("Internal Error: term.arguments() should have lenght 1, but is " + args.length + " for term: " + term);
		}
		
		if (args[0] instanceof Object[]) {
			handleArgumentList(sb, (Object[]) args[0]);			
		} else {
			sb.append(args[0]);
		}
	}

	/**
	 * Handle the <code>&lt;, &gt;, &lt;=, and &gt;=</code>
	 * @param sb the string buffer to append to
	 * @param term the term to render
	 */
	private void handleUnequal(StringBuilder sb, ParamQueryTerm<?> term) {
		sb.append(term.getHelioField().getName());
		sb.append(",");
		Object[] args=term.getArguments();		
		if (args.length != 1) {
			throw new RuntimeException("Internal Error: term.arguments() should have lenght 1, but is " + args.length + " for term: " + term);
		}
		if (args[0] instanceof Object[]) {
			throw new QuerySerializationException("Unequality with multiple values is currently not supported: " + term);			
		} else {
			
		}
	}

	/**
	 * Handle the arguments in a term
	 * @param sb the stringbuilder to append to
	 * @param term the term to process
	 */
	private void handleArgumentList(StringBuilder sb, Object[] values) {
		if (values.length == 0) {
			// nothing todo
		} else {
			sb.append(values[0]);			
			for (int i = 1; i < values.length; i++) {
				Object value = values[i];
				sb.append(",");
				sb.append(value);
			}
		}
	}
}