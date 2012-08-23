package eu.heliovo.clientapi.model.field;

import java.util.Arrays;

import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Instance of a HELIO field that can be used as single term in an HELIO query.
 * @author marco soldati at fhnw ch
 *
 */
public class HelioFieldQueryTerm<T> {
	/**
	 * The field to use as left side argument of the term
	 */
	private final HelioFieldDescriptor<T> helioFieldDescriptor;
	
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
	 * @param fieldDescriptor the field descriptor. must not be null.
	 * @param operator the operator. must not be null.
	 * @param arguments the arguments for the field-operator tuple. The length of this array must match the arity of the operator.
	 */
    public HelioFieldQueryTerm(HelioFieldDescriptor<T> fieldDescriptor, Operator operator, T... arguments) {
	    AssertUtil.assertArgumentNotNull(fieldDescriptor, "fieldDescriptor");
	    AssertUtil.assertArgumentNotNull(operator, "operator");
	    AssertUtil.assertArgumentNotNull(arguments, "arguments");
        if (operator.getArity() != Integer.MAX_VALUE && arguments.length != operator.getArity() - 1) {
            throw new IllegalArgumentException("Operator with arity " + operator.getArity() + " expects " + 
                    (operator.getArity()-1) + " arguments, but got " + arguments.length + ".");
        }
	    helioFieldDescriptor = fieldDescriptor;
	    this.operator = operator;
	    this.arguments = arguments;
	}
	
	/**
	 * The field to use as left side argument of the term
	 * @return the field.
	 */
	public HelioFieldDescriptor<T> getHelioFieldDescriptor() {
		return helioFieldDescriptor;
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
        return "ParamQueryTerm [helioField=" + helioFieldDescriptor + ", operator=" + operator + ", arguments="
                + Arrays.toString(arguments) + "]";
    };
}
