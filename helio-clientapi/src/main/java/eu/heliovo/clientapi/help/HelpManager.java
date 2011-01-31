package eu.heliovo.clientapi.help;

import java.lang.reflect.AccessibleObject;

import eu.heliovo.clientapi.help.annotation.Description;

/**
 * Manager to handle the help of a given interface, class, method, field or enum.
 * @author marco soldati at fhnw ch
 *
 */
public interface HelpManager {
	
	/**
	 * Extract the help text and return as text stub.
	 * Typically delegates to {@link #getFormattedDoc(Description)}
	 * @param annotatedClass the annotated class.
	 * @return formatted text stub or null if the submitted class does not contain a {@link Description} annotation.
	 */
	public String getFormattedDoc(Class<?> annotatedClass) throws HelpFormatException;
	
	/**
	 * Extract the help text from a Field, Method or Constructor and return it as a formatted text stub.
	 * Typically delegates to {@link #getFormattedDoc(Description)}
	 * @param member the annotated member
	 * @return formatted text stub or null if the submitted class does not contain a {@link Description} annotation.
	 */
	public String getFormattedDoc(AccessibleObject member) throws HelpFormatException;
	
	/**
	 * Extract the help text from {@link Description} and return it as a formatted text stub.
	 * @param description description. Must not be null. 
	 * @return formatted text stub.
	 * @throws IllegalArgumentException if description is null.
	 * @throws HelpFormatException if help format exception is null. 
	 */
	public String getFormattedDoc(Description description) throws IllegalArgumentException, HelpFormatException;
}
