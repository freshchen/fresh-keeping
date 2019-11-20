package com.github.freshchen.time.temporal;

import com.github.freshchen.time.TimeFactory;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

/**
 * @anthor freshchen
 */
public class ExtAdjustersTest {

    @Test
    public void extAdjusters() {
        LocalDate date = TimeFactory.date().custom().day(15).month(11).year(2019).build();
        Assert.assertEquals("2019-11-15", date.toString());
        date = date.with(ExtAdjusters.nextWorkingDay);
        Assert.assertEquals("2019-11-18", date.toString());
    }

}