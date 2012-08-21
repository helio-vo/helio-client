package eu.heliovo.clientapi.utils.convert;

import java.util.Date;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

import eu.heliovo.shared.util.DateUtil;

/**
 * Convert a date to the ISO date string.
 * 
 * @author MarcoSoldati
 * 
 */
public class DateToStringConverter implements Converter<Date, String> {

    @Override
    public String convert(Date date) {
        if (date == null) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(Date.class),
                    TypeDescriptor.valueOf(String.class), date, null);
        }
        return DateUtil.toIsoDateString(date);
    }
}
