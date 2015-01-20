package eu.heliovo.clientapi.utils.convert;

import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Conversion service for HELIO specific data types.
 * @author MarcoSoldati
 *
 */
public class HelioConversionService extends DefaultConversionService {
    
    /**
     * Create a conversion service with the default converters defined by {@link DefaultConversionService}
     * and custom converters for HELIO.
     */
    public HelioConversionService() {
        addCustomConverters(this);
    }

    /**
     * Add the custom converters.
     * @param converterRegistry the registry where to add the converters to.
     */
    public static void addCustomConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverter(new DateToStringConverter());
    }

}
