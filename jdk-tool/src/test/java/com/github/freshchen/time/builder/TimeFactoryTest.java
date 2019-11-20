package com.github.freshchen.time.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.github.freshchen.time.TimeFactory;
import org.junit.Assert;
import org.junit.Test;

public class TimeFactoryTest {

    @Test
    public void date() {
        LocalDate date = TimeFactory.date().custom().day(1).month(3).year(1222).build();
        Assert.assertEquals("1222-03-01", date.toString());
    }

    @Test
    public void date1() {
        LocalDate date = TimeFactory.date().now();
        Assert.assertNotNull(date.toString());
    }

    @Test
    public void date2() {
        LocalDate date = TimeFactory.date().parse().dateText("1992-1-1").formatter("yyyy-M-d").build();
        Assert.assertEquals("1992-01-01", date.toString());
    }

    @Test
    public void time() {
        LocalTime time = TimeFactory.time().now();
        Assert.assertNotNull(time.toString());
    }

    @Test
    public void time1() {
        LocalTime time = TimeFactory.time().custom().hour(11).minute(23).second(1).build();
        Assert.assertEquals("11:23:01", time.toString());
    }

    @Test
    public void time2() {
        LocalTime time = TimeFactory.time().parse().timeText("11:23:01").build();
        Assert.assertEquals("11:23:01", time.toString());
    }

    @Test
    public void time3() {
        LocalTime time = TimeFactory.time().parse().timeText("11/23/01").formatter("HH/mm/ss").build();
        Assert.assertEquals("11:23:01", time.toString());
    }

    @Test
    public void dateTime() {
        LocalDateTime dateTime = TimeFactory.dateTime().now();
        Assert.assertNotNull(dateTime.toString());
    }

    @Test
    public void dateTime1() {
        LocalDateTime dateTime = TimeFactory.dateTime().custom().year(2011).month(3).day(12).hour(20).minute(11).second(1).build();
        Assert.assertEquals("2011-03-12T20:11:01", dateTime.toString());

    }

    @Test
    public void dateTime3() {
        LocalDateTime dateTime = TimeFactory.dateTime().parse().dateTimeText("2011-03-12-20-11-01").formatter("yyyy-MM-dd-HH-mm-ss").build();
        Assert.assertEquals("2011-03-12T20:11:01", dateTime.toString());

    }

}