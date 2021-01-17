package com.github.freshchen.keeping.formatter;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.ZoneId.systemDefault;

/**
 * @author darcy
 * @since 2020/11/28
 **/
public class OffsetDateTimeConverter implements Converter<String, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(String source) {
        if (!NumberUtils.isNumber(source)) {
            return null;
        }

        Long milli = NumberUtils.createLong(source);
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(milli), systemDefault());
    }
}
