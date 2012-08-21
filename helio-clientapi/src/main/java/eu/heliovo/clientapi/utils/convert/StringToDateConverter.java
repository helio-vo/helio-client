package eu.heliovo.clientapi.utils.convert;

import java.text.ParseException;
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
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String date) {
        if (date == null) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(String.class),
                    TypeDescriptor.valueOf(Date.class), date, null);
        }
        try {
            return DateUtil.fromIsoDate(date);
        } catch (ParseException e) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(String.class),
                TypeDescriptor.valueOf(Date.class), date, e);
        }
    }
}
