package eu.heliovo.clientapi.query.paramquery.expression;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.field.Operator;

/**
 * Single term to be used in an helio query.
 * @author marco soldati at fhnw ch
 *
 */
public class ParamQueryTerm<T> {
	/**
	 * The field to use as left side argument of the term
	 */
	private HelioField<T> helioField;
	
	/**
	 * The operator to use
	 */
	public Operator operator;
	
	/**
	 * Get the values of this query term. 
	 */
	public T[] values;
	
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
	 * Get the values of this query term. 
	 * For unary operators this will be an empty array. 
	 * For binary operators this will contain one element. 
	 * In general, n-ary operators will contain n-1 elements. 
	 * @return the values to be used.
	 */
	public T[] getValues() {
		return values;
	};
}
