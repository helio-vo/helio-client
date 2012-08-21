package eu.heliovo.clientapi.query;

import java.util.Arrays;

import eu.heliovo.clientapi.model.field.HelioFieldDescriptor;
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
	private final HelioFieldDescriptor<T> helioField;
	
	/**
	 * The operator to use
	 */
	private final Operator operator;
	
	/**
	 * Get the arguments of this query term. 
	 */
	private final T[] arguments;
	

	/**
	 * Create a term for value selections.
	 * @param field the field. must not be null.
	 * @param operator the operator. must not be null.
	 * @param arguments the arguments for the field-operator tuple. The length of this array must match the arity of the operator.
	 */
    public ParamQueryTerm(HelioFieldDescriptor<T> field, Operator operator, T... arguments) {
	    AssertUtil.assertArgumentNotNull(field, "field");
	    AssertUtil.assertArgumentNotNull(operator, "operator");
	    AssertUtil.assertArgumentNotNull(arguments, "arguments");
        if (operator.getArity() != Integer.MAX_VALUE && arguments.length != operator.getArity() - 1) {
            throw new IllegalArgumentException("Operator with arity " + operator.getArity() + " expects " + 
                    (operator.getArity()-1) + " arguments, but got " + arguments.length + ".");
        }
	    helioField = field;
	    this.operator = operator;
	    this.arguments = arguments;
	}
	
	/**
	 * The field to use as left side argument of the term
	 * @return the field.
	 */
	public HelioFieldDescriptor<T> getHelioFieldDescriptor() {
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
	 * @return the arguments to be used for the given operator. Multi-value selections will return a T[][].
	 */
	public T[] getArguments() {
		return arguments;
	}

    @Override
    public String toString() {
        return "ParamQueryTerm [helioField=" + helioField + ", operator=" + operator + ", arguments="
                + Arrays.toString(arguments) + "]";
    };
	
	
}
