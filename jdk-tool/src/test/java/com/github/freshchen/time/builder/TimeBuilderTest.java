package com.github.freshchen.time.builder;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TimeBuilderTest {

    @Test
    public void date() {
        LocalDate date = TimeBuilder.date().year(1111).month(12).day(1).build();
        Assert.assertEquals("1111-12-01", date.toString());
    }

    @Test
    public void date1() {
        LocalDate date = TimeBuilder.date().now();
        Assert.assertNotNull(date.toString());
    }

}