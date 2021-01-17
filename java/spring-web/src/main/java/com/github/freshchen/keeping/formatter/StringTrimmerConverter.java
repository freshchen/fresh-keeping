package com.github.freshchen.keeping.formatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * @author darcy
 * @since 2020/11/28
 **/
public class StringTrimmerConverter implements Converter<String, String> {

    private final String charsToDelete;

    private final boolean emptyAsNull;

    /**
     * Create a new StringTrimmerConverter.
     *
     * @param emptyAsNull {@code true} if an empty String is to be
     *                    transformed into {@code null}
     */
    public StringTrimmerConverter(boolean emptyAsNull) {
        this(null, emptyAsNull);
    }

    /**
     * Create a new StringTrimmerConverter.
     *
     * @param charsToDelete a set of characters to delete, in addition to
     *                      trimming an input String. Useful for deleting unwanted line breaks:
     *                      e.g. "\r\n\f" will delete all new lines and line feeds in a String.
     * @param emptyAsNull   {@code true} if an empty String is to be
     *                      transformed into {@code null}
     */
    public StringTrimmerConverter(String charsToDelete, boolean emptyAsNull) {
        this.charsToDelete = charsToDelete;
        this.emptyAsNull = emptyAsNull;
    }

    @Override
    public String convert(String source) {
        if (source == null) {
            return null;
        }
        String value = source.trim();
        if (this.charsToDelete != null) {
            value = StringUtils.deleteAny(value, this.charsToDelete);
        }
        if (this.emptyAsNull && "".equals(value)) {
            return null;
        }
        return value;
    }
}
