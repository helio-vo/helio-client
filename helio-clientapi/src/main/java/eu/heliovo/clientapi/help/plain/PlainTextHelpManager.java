package eu.heliovo.clientapi.help.plain;

import java.lang.reflect.AccessibleObject;

import eu.heliovo.clientapi.help.HelpFormatException;
import eu.heliovo.clientapi.help.HelpManager;
import eu.heliovo.clientapi.help.annotation.Description;

public class PlainTextHelpManager implements HelpManager {
	
    /**
     * The default detail level.
     */
    private static final int DEFAULT_LEVEL_OF_DETAIL = 5;

    /**
     * Currently used level of detail.
     */
    private int levelOfDetail = DEFAULT_LEVEL_OF_DETAIL;
    
	@Override
	public String getFormattedDoc(Class<?> annotatedClass)
			throws HelpFormatException {
		Description description = annotatedClass.getAnnotation(Description.class);
		if (description != null) {
			return getFormattedDoc(description);
		}
		return null;
	}

	@Override
	public String getFormattedDoc(AccessibleObject member)
			throws HelpFormatException {
		Description description = member.getAnnotation(Description.class);
		if (description != null) {
			return getFormattedDoc(description);
		}
		return null;
	}

	@Override
	public String getFormattedDoc(Description description)
			throws IllegalArgumentException, HelpFormatException {
        

		return description.toString();
	}
	
	
	
	

}
