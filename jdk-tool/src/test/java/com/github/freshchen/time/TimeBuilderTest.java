package com.github.freshchen.time;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeBuilderTest {

    @Test
    public void nowDate() {
        System.out.println(TimeBuilder.date().year(2019).month(8).day(12).buildDate());

    }

    @Test
    public void buildDate() {
    }
}