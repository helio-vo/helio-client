package eu.heliovo.clientapi.help.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * General purpose description of a documented element.
 * @author marco soldati at fhnw ch
 *
 */
@Target({ ANNOTATION_TYPE } ) 
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Description {
	/**
	 * Contains the actual description. Every entry in the array is treated as a single line.
	 * @return the description.
	 */
	public String[] value();
}
