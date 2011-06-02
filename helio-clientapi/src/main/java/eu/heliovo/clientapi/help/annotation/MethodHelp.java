package eu.heliovo.clientapi.help.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ METHOD } ) 
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MethodHelp {
	/**
	 * The description of the method. Mandatory
	 * @return the description
	 */
	public Description[] help();
	
	/**
	 * Synopsis of the method. Optional. Defaults to an empty synopsis.
	 * @return the synopsis.
	 */
	public Synopsis synopsis() default @Synopsis();
	
}
