package eu.heliovo.clientapi.help.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Synopsis 
 * @author marco soldati at fhnw ch
 *
 */
@Target({ ANNOTATION_TYPE } ) 
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Synopsis {
	
}
