package eu.heliovo.clientapi.model;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Convenience class to create a domain value descriptor array.
 * @author marco soldati at fhnw ch
 *
 */
public class DomainValueDescriptorUtil {

	/**
	 * Convert an array or values to an array of domain values. 
	 * This method uses {@link #asDomainValue(Object)} for wrapping the individual members.
	 * @param <T> the type of the wrapped values
	 * @param values the values to be wrapped
	 * @return the array of domain values. 
	 */
	public static <T> DomainValueDescriptor<T>[] asValueDomain(T... values) {
		@SuppressWarnings("unchecked")
		DomainValueDescriptor<T>[] valueDomain = new DomainValueDescriptor[values.length];
		
		for (int i = 0; i < values.length; i++) {
			valueDomain[i] = asDomainValue(values[i]);
		}
		
		return valueDomain;
	}
	
	/**
	 * Convert a simple value to a domain value by wrapping it. The description will be empty, 
	 * the value will be the wrapped value, the label is derived from its toString method.
	 * @param <T> the type of the wrapped value.
	 * @param value the value to be wrapped.
	 * @return the value wrapped in a {@link GenericDomainValueDescriptor}.
	 */
	public static <T> DomainValueDescriptor<T> asDomainValue(T value) {
		GenericDomainValueDescriptor<T> descriptor = new GenericDomainValueDescriptor<T>(value, value.toString(), null);
		return descriptor;
	}
	
	/**
	 * Create a new DomainValue Descriptor
	 * @param <T> type of the wrapped value
	 * @param value the value itself
	 * @param label the label to use. defaults to label.toString() if null.
	 * @param description the description. May be null.
	 * @return the domain value descriptor
	 */
	public static <T> DomainValueDescriptor<T> asDomainValue(T value, String label, String description) {
		return new GenericDomainValueDescriptor<T>(value, label, description);
	}

	static class GenericDomainValueDescriptor<T> implements DomainValueDescriptor<T> {
		/**
		 * The value of this domain
		 */
		private final T value;
		
		
		private final String label;
		
		
		private final String description;
		
		/**
		 * Create the domain value with an emtpy description, the value submitted in the constructor. The label is the toString value.
		 * @param value the value to wrap. Msut not be null.
		 */
		public GenericDomainValueDescriptor(T value, String label, String description) {
			AssertUtil.assertArgumentNotNull(value, "value");
			this.value = value;
			this.label = label == null ? value.toString() : label;
			this.description = description;
		}

		@Override
		public T getValue() {
			return value;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			sb.append("value: ").append(value.toString());
			sb.append(", label: ").append(label);
			if (description != null) {
				sb.append(", description: ").append(description);
			}
			sb.append("]");
			return sb.toString();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof GenericDomainValueDescriptor<?>))
				return false;
			
			return value.equals(((GenericDomainValueDescriptor<?>)obj).value);
		}
		
		@Override
		public int hashCode() {
			return value.hashCode();
		}
	}
}
