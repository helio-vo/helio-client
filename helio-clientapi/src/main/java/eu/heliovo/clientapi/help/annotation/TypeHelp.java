package eu.heliovo.clientapi.help.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Help for a class, an enum, or an interface.
 * @author marco soldati at fhnw ch
 *
 */
@Target({ TYPE } ) 
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TypeHelp {
	/**
	 * The description of the class. Mandatory
	 * @return the description
	 */
	public Description[] help();
}
