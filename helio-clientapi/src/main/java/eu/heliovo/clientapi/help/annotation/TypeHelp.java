package eu.heliovo.clientapi.help.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*; 

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
