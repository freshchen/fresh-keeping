package util;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

public class DateTimesTest {


    @Test
    public void toJodaTime() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        DateTime dateTime = DateTimes.toJodaTime(offsetDateTime);
        Assert.assertNotNull(dateTime);
    }

    @Test
    public void fromJodaTime() {
        DateTime now = DateTime.now();
        OffsetDateTime offsetDateTime = DateTimes.fromJodaTime(now);
        Assert.assertNotNull(offsetDateTime);
    }

    @Test
    public void fromLong() {
        OffsetDateTime offsetDateTime = DateTimes.fromLong(1582101988L);
        Assert.assertNotNull(offsetDateTime);
    }

    @Test
    public void between() {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1582101988L), ZoneId.systemDefault());
        OffsetDateTime offsetDateTime1 = OffsetDateTime.ofInstant(Instant.ofEpochMilli(158211111L), ZoneId.systemDefault());
        OffsetDateTime offsetDateTime2 = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1582102222L), ZoneId.systemDefault());
        boolean between = DateTimes.between(offsetDateTime, offsetDateTime1, offsetDateTime2);
        Assert.assertTrue(between);

    }
}