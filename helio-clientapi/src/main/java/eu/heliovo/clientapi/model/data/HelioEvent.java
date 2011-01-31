package eu.heliovo.clientapi.model.data;

import java.util.Date;
import java.util.Map;

import eu.heliovo.clientapi.model.HelioResource;

/**
 * Single entry in an event list.
 * The interface implements a Map to support custom attributes depending on the implemented list. 
 * 
 * @author marco soldati at fhnw ch
 *
 */
public interface HelioEvent extends HelioResource, Map<String, Object> {
	/**
	 * Supported event types.
	 *
	 */
	public static enum EventType {
		/**
		 * A flare event. 
		 */
		FLARE,
		
		/**
		 * A coronal mass ejection.
		 */
		CME
	}
	
	/**
	 * Get the start date assigned to an event. May be null.
	 * <p><b>Warning:</b> there is no standardized way to define the start and end date of an event. 
	 * Therefore events of different event lists cannot be compared.
	 * @return the date and time when the event has started.
	 */
	public Date getStartDate();

	/**
	 * Get the end date assigned to an event. May be null.
	 * <p><b>Warning:</b> there is no standardized way to define the start and end date of an event. 
	 * Therefore events of different event lists cannot be compared.
	 * @return the date and time when the event has ended.
	 */
	public Date getEndDate();
	
	/**
	 * Get the peak date assigned to an event. May be null.
	 * <p><b>Warning:</b> there is no standardized way to define the start and end date of an event. 
	 * Therefore events of different event lists cannot be compared.
	 * @return the date and time when the event peak has reached.
	 */
	public Date getPeakDate();
	
	/**
	 * Get the event type.
	 * @return the type of the event.
	 */
	public EventType getEventType();
}
