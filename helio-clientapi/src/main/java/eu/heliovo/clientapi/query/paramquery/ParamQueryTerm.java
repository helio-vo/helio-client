package eu.heliovo.clientapi.query.paramquery;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.field.Operator;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Single term to be used in an helio query.
 * @author marco soldati at fhnw ch
 *
 */
public class ParamQueryTerm<T> {
	/**
	 * The field to use as left side argument of the term
	 */
	private final HelioField<T> helioField;
	
	/**
	 * The operator to use
	 */
	private final Operator operator;
	
	/**
	 * Get the arguments of this query term. 
	 */
	private final T[] arguments;
	
	/**
	 * Create a term
	 * @param field the field. must not be null.
	 * @param operator the operator. must not be null.
	 * @param arguments the arguments for the field-operator tuple. The length must match the arity of the operator.
	 */
	public ParamQueryTerm(HelioField<T> field, Operator operator, T... arguments) {
		AssertUtil.assertArgumentNotNull(field, "field");
		AssertUtil.assertArgumentNotNull(operator, "operator");
		AssertUtil.assertArgumentNotNull(arguments, "arguments");
		if (arguments.length != operator.getArity() - 1) {
			throw new IllegalArgumentException("Operator with arity " + operator.getArity() + " expects " + (operator.getArity()-1) + " arguments, but got " + arguments.length);
		}  
		helioField = field;
		this.operator = operator;
		this.arguments = arguments;
	}
	
	/**
	 * The field to use as left side argument of the term
	 * @return the field.
	 */
	public HelioField<T> getHelioField() {
		return helioField;
	};
	
	/**
	 * The operator to use
	 * @return the operator.
	 */
	public Operator getOperator() {
		return operator;
	};
	
	/**
	 * Get the arguments of this query term. 
	 * For unary operators this will be an empty array. 
	 * For binary operators this will contain one element. 
	 * In general, n-ary operators will contain n-1 elements. 
	 * @return the arguments to be used.
	 */
	public T[] getArguments() {
		return arguments;
	};
}
