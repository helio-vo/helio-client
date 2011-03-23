package eu.heliovo.clientapi.model.field;

/**
 * Enum of operators supported by HELIO.
 * @author marco soldati at fhnw ch
 *
 */
public enum Operator {
	EQUALS("=", 2),
	//LESS_THAN("<", 2),
	LESS_EQUAL_THAN("<=", 2),
	//LARGER_THAN(">", 2),
	LARGER_EQUAL_THAN(">=", 2),
	LIKE("like", 2),
	NOT("not", 1),
	BETWEEN("between", 3);
	;
	
	/**
	 * Symbol or text string to represent the operator.
	 */
	private final String symbol;
	
	/**
	 * arity of the operator (1=unary, 2=binary, 3=ternary)
	 */
	private final int arity;

	/**
	 * Create an operator
	 * @param symbol the symbol used to represent the operator
	 * @param arity arity of the operator (1=unary, 2=binary, 3=ternary).
	 */
	private Operator(String symbol, int arity) {
		this.symbol = symbol;
		this.arity = arity;
	}
	
	/**
	 * Get the symbol used to represent this operator.
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * Get the arity of the operator (1=unary, 2=binary, 3=ternary).
	 * @return the arity.
	 */
	public int getArity() {
		return arity;
	}
}
