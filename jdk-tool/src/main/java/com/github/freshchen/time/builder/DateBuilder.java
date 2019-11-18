package com.github.freshchen.time.builder;

import java.time.LocalDate;

/**
 * @author : freshchen
 * <P>Created on 2019-11-18 23:18 </p>
 **/
public class DateBuilder implements ITimeBuilder {

    private int year;
    private short month;
    private short day;

    public DateBuilder year(int year) {
        this.year = year;
        return this;
    }

    public DateBuilder month(int month) {
        this.month = (short) month;
        return this;
    }

    public DateBuilder day(int day) {
        this.day = (short) day;
        return this;
    }

    @Override
    public LocalDate now() {
        return LocalDate.now();
    }

    @Override
    public LocalDate build() {
        return LocalDate.of(year, month, day);
    }
}
