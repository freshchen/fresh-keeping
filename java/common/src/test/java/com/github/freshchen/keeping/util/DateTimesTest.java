package com.github.freshchen.keeping.util;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class DateTimesTest {


    @Test
    public void toJodaTime() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        DateTime dateTime = DateTimes.toJodaTime(offsetDateTime);
        Assertions.assertNotNull(dateTime);
    }

    @Test
    public void fromJodaTime() {
        DateTime now = DateTime.now();
        OffsetDateTime offsetDateTime = DateTimes.fromJodaTime(now);
        Assertions.assertNotNull(offsetDateTime);
    }

    @Test
    public void fromLong() {
        OffsetDateTime offsetDateTime = DateTimes.fromLong(1582101988L);
        Assertions.assertNotNull(offsetDateTime);
    }

    @Test
    public void between() {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1582101988L), ZoneId.systemDefault());
        OffsetDateTime offsetDateTime1 = OffsetDateTime.ofInstant(Instant.ofEpochMilli(158211111L), ZoneId.systemDefault());
        OffsetDateTime offsetDateTime2 = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1582102222L), ZoneId.systemDefault());
        boolean between = DateTimes.between(offsetDateTime, offsetDateTime1, offsetDateTime2);
        Assertions.assertTrue(between);

    }

    @Test
    public void fromIsoLocalDate() {
        OffsetDateTime offsetDateTime = DateTimes.fromIsoDate("2019-08-15");
        boolean b = offsetDateTime.toString().startsWith("2019-08-15T00:00");
        Assertions.assertTrue(b);
    }

    @Test
    public void format() {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1582101988L), ZoneId.systemDefault());
        String format = DateTimes.format(offsetDateTime, "yyyy%MM%dd");
        Assertions.assertEquals("1970%01%19", format);
    }
}
